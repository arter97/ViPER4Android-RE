package com.aam.viper4android.driver

import android.content.Context
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.aam.viper4android.persistence.PresetsDao
import com.aam.viper4android.persistence.model.PersistedPreset
import com.aam.viper4android.util.debounce
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViPERManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presetDao: PresetsDao,
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private val mediaRouter = MediaRouter.getInstance(context)

    private var _currentRoute = MutableStateFlow(ViPERRoute.fromRouteInfo(mediaRouter.selectedRoute))
    val currentRoute = _currentRoute.asStateFlow()

    private var _currentSessions = MutableStateFlow<List<Session>>(emptyList())
    val currentSessions = _currentSessions.asStateFlow()
    
    var preset: Preset = Preset()
        private set

    private val _legacyMode = MutableStateFlow(preset.legacyMode)
    val legacyMode = _legacyMode.asStateFlow()

    private val _enabled = MutableStateFlow(preset.enabled)
    val enabled = _enabled.asStateFlow()
    val analogX = AnalogX()
    val auditorySystemProtection = AuditorySystemProtection()
    val convolver = Convolver()
    val differentialSurround = DifferentialSurround()
    val dynamicSystem = DynamicSystem()
    val fetCompressor = FETCompressor()
    val fieldSurround = FieldSurround()
    val firEqualizer = FIREqualizer()
    val headphoneSurroundPlus = HeadphoneSurroundPlus()
    val masterLimiter = MasterLimiter()
    val playbackGainControl = PlaybackGainControl()
    val reverberation = Reverberation()
    val speakerOptimization = SpeakerOptimization()
    val spectrumExtension = SpectrumExtension()
    val tubeSimulator6N1J = TubeSimulator6N1J()
    val viperBass = ViPERBass()
    val viperClarity = ViPERClarity()
    val viperDdc = ViPERDDC()

    init {
        observeMediaRouter()
    }

    private fun observeMediaRouter() {
        scope.launch {
            callbackFlow {
                val callback = object : MediaRouter.Callback() {
                    override fun onRouteSelected(
                        router: MediaRouter,
                        route: MediaRouter.RouteInfo,
                        reason: Int
                    ) {
                        trySend(route)
                    }
                }
                mediaRouter.addCallback(
                    MediaRouteSelector.Builder()
                        .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                        .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                        .build(),
                    callback
                )

                // Update the current route immediately
                trySend(mediaRouter.selectedRoute)

                awaitClose {
                    mediaRouter.removeCallback(callback)
                }
            }.collect {
                // TODO: When adding Android Auto support, only update if not in Android Auto mode!
                val route = ViPERRoute.fromRouteInfo(it)
                _currentRoute.value = route

                val preset = presetDao.get(route.getId())?.toPreset() ?: Preset()
                setPreset(preset)
            }
        }
    }

    fun addSession(sessionId: Int, packageName: String?, startedAt: Instant) {
        if (_currentSessions.value.any { it.id == sessionId }) {
            Timber.d("addSession: Session $sessionId already exists, skipping")
            return
        }
        val session = try {
            Session(this, packageName, sessionId, startedAt)
        } catch (e: Exception) {
            Timber.e(e, "addSession: Failed to create session")
            return
        }
        _currentSessions.value = _currentSessions.value + session
    }

    fun removeSession(sessionId: Int, packageName: String?) {
        val session = _currentSessions.value.find { it.id == sessionId }
        if (session == null) {
            Timber.d("removeSession: Session $sessionId for package $packageName not found, skipping")
            return
        }
        _currentSessions.value = _currentSessions.value.filter { it != session }
        session.release()
    }

    fun removeAllSessions() {
        Timber.d("removeAllSessions: Removing all sessions")
        val sessions = _currentSessions.value
        _currentSessions.value = emptyList()
        sessions.forEach { session ->
            try {
                session.release()
            } catch (e: Exception) {
                Timber.e(e, "removeAllSessions: Failed to release session")
            }
        }
    }

    fun hasSessions(): Boolean {
        return currentSessions.value.isNotEmpty()
    }

    private fun setPreset(preset: Preset) {
        this.preset = preset

        _legacyMode.value = preset.legacyMode
        setEnabled(preset.enabled)
        analogX.setEnabled(preset.analogX.enabled)
        analogX.setLevel(preset.analogX.level)
        auditorySystemProtection.setEnabled(preset.auditorySystemProtection.enabled)
        convolver.setEnabled(preset.convolver.enabled)
        differentialSurround.setEnabled(preset.differentialSurround.enabled)
        differentialSurround.setDelay(preset.differentialSurround.delay)
        dynamicSystem.setEnabled(preset.dynamicSystem.enabled)
        dynamicSystem.setDeviceType(preset.dynamicSystem.deviceType)
        dynamicSystem.setDynamicBassStrength(preset.dynamicSystem.dynamicBassStrength)
        fetCompressor.setEnabled(preset.fetCompressor.enabled)
        fieldSurround.setEnabled(preset.fieldSurround.enabled)
        fieldSurround.setSurroundStrength(preset.fieldSurround.surroundStrength)
        fieldSurround.setMidImageStrength(preset.fieldSurround.midImageStrength)
        firEqualizer.setEnabled(preset.firEqualizer.enabled)
        headphoneSurroundPlus.setEnabled(preset.headphoneSurroundPlus.enabled)
        masterLimiter.setOutputGain(preset.masterLimiter.outputGain)
        masterLimiter.setOutputPan(preset.masterLimiter.outputPan)
        masterLimiter.setThresholdLimit(preset.masterLimiter.thresholdLimit)
        playbackGainControl.setEnabled(preset.playbackGainControl.enabled)
        reverberation.setEnabled(preset.reverberation.enabled)
        speakerOptimization.setEnabled(preset.speakerOptimization.enabled)
        spectrumExtension.setEnabled(preset.spectrumExtension.enabled)
        spectrumExtension.setStrength(preset.spectrumExtension.strength)
        tubeSimulator6N1J.setEnabled(preset.tubeSimulator6N1J.enabled)
        viperBass.setEnabled(preset.viperBass.enabled)
        viperBass.setMode(preset.viperBass.mode)
        viperBass.setFrequency(preset.viperBass.frequency)
        viperBass.setGain(preset.viperBass.gain)
        viperClarity.setEnabled(preset.viperClarity.enabled)
        viperClarity.setMode(preset.viperClarity.mode)
        viperClarity.setGain(preset.viperClarity.gain)
        viperDdc.setEnabled(preset.viperDdc.enabled)
    }

    private val savePreset = scope.debounce(1000) {
        val routeId = currentRoute.value.getId()
        withContext(Dispatchers.IO) {
            presetDao.insert(PersistedPreset.fromPreset(routeId, preset))
        }
    }

    fun setLegacyMode(legacyMode: Boolean) {
        _legacyMode.value = legacyMode
        if (preset.legacyMode != legacyMode) {
            preset.legacyMode = legacyMode
            savePreset()
        }
    }

    fun setEnabled(enabled: Boolean) {
        _enabled.value = enabled
        if (preset.enabled != enabled) {
            preset.enabled = enabled
            savePreset()
        }
    }

    inner class AnalogX {
        private val _enabled = MutableStateFlow(preset.analogX.enabled)
        val enabled = _enabled.asStateFlow()

        private val _level = MutableStateFlow(preset.analogX.level)
        val level = _level.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.analogX.enabled != enabled) {
                preset.analogX.enabled = enabled
                savePreset()
            }
        }

        fun setLevel(level: Int) {
            _level.value = level
            if (preset.analogX.level != level) {
                preset.analogX.level = level
                savePreset()
            }
        }
    }

    inner class AuditorySystemProtection {
        private val _enabled = MutableStateFlow(preset.auditorySystemProtection.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.auditorySystemProtection.enabled != enabled) {
                preset.auditorySystemProtection.enabled = enabled
                savePreset()
            }
        }
    }

    inner class Convolver {
        private val _enabled = MutableStateFlow(preset.convolver.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.convolver.enabled != enabled) {
                preset.convolver.enabled = enabled
                savePreset()
            }
        }
    }

    inner class DifferentialSurround {
        private val _enabled = MutableStateFlow(preset.differentialSurround.enabled)
        val enabled = _enabled.asStateFlow()

        private val _delay = MutableStateFlow(preset.differentialSurround.delay)
        val delay = _delay.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.differentialSurround.enabled != enabled) {
                preset.differentialSurround.enabled = enabled
                savePreset()
            }
        }

        fun setDelay(delay: Int) {
            _delay.value = delay
            if (preset.differentialSurround.delay != delay) {
                preset.differentialSurround.delay = delay
                savePreset()
            }
        }
    }

    inner class DynamicSystem {
        private val _enabled = MutableStateFlow(preset.dynamicSystem.enabled)
        val enabled = _enabled.asStateFlow()

        private val _deviceType = MutableStateFlow(preset.dynamicSystem.deviceType)
        val deviceType = _deviceType.asStateFlow()

        private val _dynamicBassStrength = MutableStateFlow(preset.dynamicSystem.dynamicBassStrength)
        val dynamicBassStrength = _dynamicBassStrength.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.dynamicSystem.enabled != enabled) {
                preset.dynamicSystem.enabled = enabled
                savePreset()
            }
        }

        fun setDeviceType(deviceType: Preset.DynamicSystem.DeviceType) {
            _deviceType.value = deviceType
            if (preset.dynamicSystem.deviceType != deviceType) {
                preset.dynamicSystem.deviceType = deviceType
                savePreset()
            }
        }

        fun setDynamicBassStrength(dynamicBassStrength: Int) {
            _dynamicBassStrength.value = dynamicBassStrength
            if (preset.dynamicSystem.dynamicBassStrength != dynamicBassStrength) {
                preset.dynamicSystem.dynamicBassStrength = dynamicBassStrength
                savePreset()
            }
        }
    }

    inner class FETCompressor {
        private val _enabled = MutableStateFlow(preset.fetCompressor.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.fetCompressor.enabled != enabled) {
                preset.fetCompressor.enabled = enabled
                savePreset()
            }
        }
    }

    inner class FieldSurround {
        private val _enabled = MutableStateFlow(preset.fieldSurround.enabled)
        val enabled = _enabled.asStateFlow()

        private val _surroundStrength = MutableStateFlow(preset.fieldSurround.surroundStrength)
        val surroundStrength = _surroundStrength.asStateFlow()

        private val _midImageStrength = MutableStateFlow(preset.fieldSurround.midImageStrength)
        val midImageStrength = _midImageStrength.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.fieldSurround.enabled != enabled) {
                preset.fieldSurround.enabled = enabled
                savePreset()
            }
        }

        fun setSurroundStrength(surroundStrength: Int) {
            _surroundStrength.value = surroundStrength
            if (preset.fieldSurround.surroundStrength != surroundStrength) {
                preset.fieldSurround.surroundStrength = surroundStrength
                savePreset()
            }
        }

        fun setMidImageStrength(midImageStrength: Int) {
            _midImageStrength.value = midImageStrength
            if (preset.fieldSurround.midImageStrength != midImageStrength) {
                preset.fieldSurround.midImageStrength = midImageStrength
                savePreset()
            }
        }
    }

    inner class FIREqualizer {
        private val _enabled = MutableStateFlow(preset.firEqualizer.enabled)
        val enabled = _enabled.asStateFlow()

        private val _gains = MutableStateFlow(preset.firEqualizer.gains)
        val gains = _gains.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.firEqualizer.enabled != enabled) {
                preset.firEqualizer.enabled = enabled
                savePreset()
            }
        }

        fun setGains(gains: List<Float>) {
            _gains.value = gains
            if (preset.firEqualizer.gains != gains) {
                preset.firEqualizer.gains = gains
                savePreset()
            }
        }
    }

    inner class HeadphoneSurroundPlus {
        private val _enabled = MutableStateFlow(preset.headphoneSurroundPlus.enabled)
        val enabled = _enabled.asStateFlow()

        private val _level = MutableStateFlow(preset.headphoneSurroundPlus.level)
        val level = _level.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.headphoneSurroundPlus.enabled != enabled) {
                preset.headphoneSurroundPlus.enabled = enabled
                savePreset()
            }
        }

        fun setLevel(level: Int) {
            _level.value = level
            if (preset.headphoneSurroundPlus.level != level) {
                preset.headphoneSurroundPlus.level = level
                savePreset()
            }
        }
    }

    inner class MasterLimiter {
        private val _outputGain = MutableStateFlow(preset.masterLimiter.outputGain)
        val outputGain = _outputGain.asStateFlow()

        private val _outputPan = MutableStateFlow(preset.masterLimiter.outputPan)
        val outputPan = _outputPan.asStateFlow()

        private val _thresholdLimit = MutableStateFlow(preset.masterLimiter.thresholdLimit)
        val thresholdLimit = _thresholdLimit.asStateFlow()

        fun setOutputGain(outputGain: Int) {
            _outputGain.value = outputGain
            if (preset.masterLimiter.outputGain != outputGain) {
                preset.masterLimiter.outputGain = outputGain
                savePreset()
            }
        }

        fun setOutputPan(outputPan: Int) {
            _outputPan.value = outputPan
            if (preset.masterLimiter.outputPan != outputPan) {
                preset.masterLimiter.outputPan = outputPan
                savePreset()
            }
        }

        fun setThresholdLimit(thresholdLimit: Int) {
            _thresholdLimit.value = thresholdLimit
            if (preset.masterLimiter.thresholdLimit != thresholdLimit) {
                preset.masterLimiter.thresholdLimit = thresholdLimit
                savePreset()
            }
        }
    }

    inner class PlaybackGainControl {
        private val _enabled = MutableStateFlow(preset.playbackGainControl.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.playbackGainControl.enabled != enabled) {
                preset.playbackGainControl.enabled = enabled
                savePreset()
            }
        }
    }

    inner class Reverberation {
        private val _enabled = MutableStateFlow(preset.reverberation.enabled)
        val enabled = _enabled.asStateFlow()

        private val _roomSize = MutableStateFlow(preset.reverberation.roomSize)
        val roomSize = _roomSize.asStateFlow()

        private val _soundField = MutableStateFlow(preset.reverberation.soundField)
        val soundField = _soundField.asStateFlow()

        private val _damping = MutableStateFlow(preset.reverberation.damping)
        val damping = _damping.asStateFlow()

        private val _wetSignal = MutableStateFlow(preset.reverberation.wetSignal)
        val wetSignal = _wetSignal.asStateFlow()

        private val _drySignal = MutableStateFlow(preset.reverberation.drySignal)
        val drySignal = _drySignal.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.reverberation.enabled != enabled) {
                preset.reverberation.enabled = enabled
                savePreset()
            }
        }

        fun setRoomSize(roomSize: Int) {
            _roomSize.value = roomSize
            if (preset.reverberation.roomSize != roomSize) {
                preset.reverberation.roomSize = roomSize
                savePreset()
            }
        }

        fun setSoundField(soundField: Int) {
            _soundField.value = soundField
            if (preset.reverberation.soundField != soundField) {
                preset.reverberation.soundField = soundField
                savePreset()
            }
        }

        fun setDamping(damping: Int) {
            _damping.value = damping
            if (preset.reverberation.damping != damping) {
                preset.reverberation.damping = damping
                savePreset()
            }
        }

        fun setWetSignal(wetSignal: Int) {
            _wetSignal.value = wetSignal
            if (preset.reverberation.wetSignal != wetSignal) {
                preset.reverberation.wetSignal = wetSignal
                savePreset()
            }
        }

        fun setDrySignal(drySignal: Int) {
            _drySignal.value = drySignal
            if (preset.reverberation.drySignal != drySignal) {
                preset.reverberation.drySignal = drySignal
                savePreset()
            }
        }
    }

    inner class SpeakerOptimization {
        private val _enabled = MutableStateFlow(preset.speakerOptimization.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.speakerOptimization.enabled != enabled) {
                preset.speakerOptimization.enabled = enabled
                savePreset()
            }
        }
    }

    inner class SpectrumExtension {
        private val _enabled = MutableStateFlow(preset.spectrumExtension.enabled)
        val enabled = _enabled.asStateFlow()

        private val _strength = MutableStateFlow(preset.spectrumExtension.strength)
        val strength = _strength.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.spectrumExtension.enabled != enabled) {
                preset.spectrumExtension.enabled = enabled
                savePreset()
            }
        }

        fun setStrength(strength: Int) {
            _strength.value = strength
            if (preset.spectrumExtension.strength != strength) {
                preset.spectrumExtension.strength = strength
                savePreset()
            }
        }
    }

    inner class TubeSimulator6N1J {
        private val _enabled = MutableStateFlow(preset.tubeSimulator6N1J.enabled)
        val enabled = _enabled.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.tubeSimulator6N1J.enabled != enabled) {
                preset.tubeSimulator6N1J.enabled = enabled
                savePreset()
            }
        }
    }

    inner class ViPERBass {
        private val _enabled = MutableStateFlow(preset.viperBass.enabled)
        val enabled = _enabled.asStateFlow()

        private val _mode = MutableStateFlow(preset.viperBass.mode)
        val mode = _mode.asStateFlow()

        private val _frequency = MutableStateFlow(preset.viperBass.frequency)
        val frequency = _frequency.asStateFlow()

        private val _gain = MutableStateFlow(preset.viperBass.gain)
        val gain = _gain.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.viperBass.enabled != enabled) {
                preset.viperBass.enabled = enabled
                savePreset()
            }
        }

        fun setMode(mode: Int) {
            _mode.value = mode
            if (preset.viperBass.mode != mode) {
                preset.viperBass.mode = mode
                savePreset()
            }
        }

        fun setFrequency(frequency: Int) {
            _frequency.value = frequency
            if (preset.viperBass.frequency != frequency) {
                preset.viperBass.frequency = frequency
                savePreset()
            }
        }

        fun setGain(bassGain: Int) {
            _gain.value = bassGain
            if (preset.viperBass.gain != bassGain) {
                preset.viperBass.gain = bassGain
                savePreset()
            }
        }
    }

    inner class ViPERClarity {
        private val _enabled = MutableStateFlow(preset.viperClarity.enabled)
        val enabled = _enabled.asStateFlow()

        private val _mode = MutableStateFlow(preset.viperClarity.mode)
        val mode = _mode.asStateFlow()

        private val _gain = MutableStateFlow(preset.viperClarity.gain)
        val gain = _gain.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.viperClarity.enabled != enabled) {
                preset.viperClarity.enabled = enabled
                savePreset()
            }
        }

        fun setMode(mode: Int) {
            _mode.value = mode
            if (preset.viperClarity.mode != mode) {
                preset.viperClarity.mode = mode
                savePreset()
            }
        }

        fun setGain(gain: Int) {
            _gain.value = gain
            if (preset.viperClarity.gain != gain) {
                preset.viperClarity.gain = gain
                savePreset()
            }
        }
    }

    inner class ViPERDDC {
        private val _enabled = MutableStateFlow(preset.viperDdc.enabled)
        val enabled = _enabled.asStateFlow()

        private val _ddcPath = MutableStateFlow(preset.viperDdc.ddcPath)
        val ddcPath = _ddcPath.asStateFlow()

        fun setEnabled(enabled: Boolean) {
            _enabled.value = enabled
            if (preset.viperDdc.enabled != enabled) {
                preset.viperDdc.enabled = enabled
                savePreset()
            }
        }

        fun setDdcPath(ddcPath: String) {
            _ddcPath.value = ddcPath
            if (preset.viperDdc.ddcPath != ddcPath) {
                preset.viperDdc.ddcPath = ddcPath
                savePreset()
            }
        }
    }
}