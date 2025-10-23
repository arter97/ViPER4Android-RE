package com.aam.viper4android.driver

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.media.audiofx.AudioEffect
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.aam.viper4android.R
import com.aam.viper4android.ViPERApplication
import com.aam.viper4android.ktx.getBootCount
import com.aam.viper4android.persistence.SessionDao
import com.aam.viper4android.persistence.model.PersistedSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class ViPERService : LifecycleService() {
    @Inject lateinit var viperManager: ViPERManager
    @Inject lateinit var sessionDao: SessionDao

    private var bootCount: Int = 0

    private var sessionMutex = Mutex(locked = true)

    override fun onCreate() {
        super.onCreate()
        bootCount = getBootCount(contentResolver)

        deleteObsoleteSessions()
        restoreSessions()
        collectFlows()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        try {
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                getNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } catch (e: Exception) {
            Timber.e(e, "onStartCommand: Failed to start foreground service")
        }

        Timber.d("onStartCommand: Intent: $intent")

        when (intent?.action) {
            AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                val packageName = intent.getStringExtra(AudioEffect.EXTRA_PACKAGE_NAME)
                addSession(sessionId, packageName, startId)
            }
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                val packageName = intent.getStringExtra(AudioEffect.EXTRA_PACKAGE_NAME)
                removeSession(sessionId, packageName, startId)
            }
            else -> {
                checkSessions(startId)
            }
        }

        return START_STICKY
    }

    private fun deleteObsoleteSessions() {
        lifecycleScope.launch(Dispatchers.IO) {
            sessionDao.deleteObsolete(bootCount)
        }
    }

    private fun restoreSessions() {
        lifecycleScope.launch {
            setLegacyModeLocked(viperManager.legacyMode.value)
            sessionMutex.unlock()
        }
    }

    private fun addSession(id: Int, packageName: String?, startId: Int) {
        Timber.d("addSession: Adding session $id for package $packageName")
        lifecycleScope.launch {
            sessionMutex.withLock {
                addSessionLocked(id, packageName, startId)
            }
        }
    }

    private suspend fun addSessionLocked(id: Int, packageName: String?, startId: Int) {
        Timber.d("addSessionLocked: Adding session $id for package $packageName")
        if (id != -1) {
            val now = Instant.now()
            withContext(Dispatchers.IO) {
                sessionDao.insert(PersistedSession(
                    id = id,
                    packageName = packageName,
                    bootCount = bootCount,
                    startedAt = now,
                ))
            }
            if (!viperManager.legacyMode.value) {
                viperManager.addSession(id, packageName, now)
            } else {
                Timber.d("addSessionLocked: Legacy mode is enabled, not adding session $id for package $packageName")
            }
        }

        if (!viperManager.hasSessions()) {
            Timber.d("addSession: No sessions are active, stopping service (startId: $startId)")
            stopSelf(startId)
        }
    }

    private fun removeSession(id: Int, packageName: String?, startId: Int) {
        Timber.d("removeSession: Removing session $id for package $packageName")
        lifecycleScope.launch {
            sessionMutex.withLock {
                removeSessionLocked(id, packageName, startId)
            }
        }
    }

    private suspend fun removeSessionLocked(id: Int, packageName: String?, startId: Int) {
        Timber.d("removeSessionLocked: Removing session $id for package $packageName")
        if (id != -1) {
            withContext(Dispatchers.IO) {
                sessionDao.delete(id)
            }
            if (!viperManager.legacyMode.value) {
                viperManager.removeSession(id, packageName)
            } else {
                Timber.d("removeSessionLocked: Legacy mode is enabled, not removing session $id for package $packageName")
            }
        }

        if (!viperManager.hasSessions()) {
            Timber.d("removeSessionLocked: No sessions are active, stopping service (startId: $startId)")
            stopSelf(startId)
        }
    }

    private fun checkSessions(startId: Int) {
        lifecycleScope.launch {
            sessionMutex.withLock {
                if (!viperManager.hasSessions()) {
                    Timber.d("checkSessions: No sessions are active, stopping service (startId: $startId)")
                    stopSelf(startId)
                }
            }
        }
    }

    private suspend fun setLegacyModeLocked(legacyMode: Boolean) {
        viperManager.removeAllSessions()
        if (legacyMode) {
            Timber.d("setLegacyModeLocked: Legacy mode is enabled")
            viperManager.addSession(0, packageName, Instant.now())
        } else {
            Timber.d("setLegacyModeLocked: Restoring sessions from database")
            withContext(Dispatchers.IO) { sessionDao.getAll(bootCount) }.forEach { session ->
                Timber.d("setLegacyModeLocked: Restoring session ${session.id} for package ${session.packageName}")
                viperManager.addSession(session.id, session.packageName, session.startedAt)
            }
        }
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            viperManager.legacyMode.drop(1).collect { legacyMode ->
                sessionMutex.withLock {
                    setLegacyModeLocked(legacyMode)
                }
                Timber.d("collectFlows: legacyMode: updateNotification (legacyMode: $legacyMode)")
                updateNotification(
                    legacyMode = legacyMode
                )
            }
        }

        lifecycleScope.launch {
            viperManager.currentRoute.drop(1).collect { route ->
                Timber.d("collectFlows: route: updateNotification (route: $route)")
                updateNotification(
                    route = route
                )
            }
        }

        lifecycleScope.launch {
            viperManager.currentSessions.drop(1).collect { sessions ->
                Timber.d("collectFlows: sessions: updateNotification (sessions: $sessions)")
                updateNotification(
                    sessions = sessions,
                )
            }
        }
    }


    private fun updateNotification(
        route: ViPERRoute = viperManager.currentRoute.value,
        legacyMode: Boolean = viperManager.legacyMode.value,
        sessions: List<Session> = viperManager.currentSessions.value,
    ) {
        try {
            val notification = getNotification(
                route = route,
                legacyMode = legacyMode,
                sessions = sessions
            )

            NotificationManagerCompat.from(this)
                .notify(SERVICE_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Timber.e(e, "updateNotification: Failed to update notification")
        }
    }

    private fun getNotification(
        route: ViPERRoute = viperManager.currentRoute.value,
        legacyMode: Boolean = viperManager.legacyMode.value,
        sessions: List<Session> = viperManager.currentSessions.value,
    ): Notification {
        Timber.d("getNotification: route: $route, legacyMode: $legacyMode, sessions: $sessions")

        val pendingIntent = packageManager.getLaunchIntentForPackage(packageName).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val title = getString(R.string.device_connected, route.getName())

        val text = if (legacyMode)
            getString(R.string.legacy_mode)
        else
            getSessionAppLabelsString(
                sessions = sessions
            )

        return NotificationCompat.Builder(this, ViPERApplication.SERVICES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setColor(Color.parseColor("#6100ED"))
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun getSessionAppLabelsString(
        sessions: List<Session>,
    ): String {
        return sessions.map {
            it.getApplicationLabel(this)
        }.distinct().joinToString().ifEmpty { getString(R.string.no_active_sessions) }
    }

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 1
    }
}