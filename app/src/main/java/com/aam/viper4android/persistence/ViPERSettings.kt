package com.aam.viper4android.persistence

import com.aam.viper4android.driver.ViPERManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViPERSettings @Inject constructor(
    private val viperManager: ViPERManager,
) {
    val legacyMode = viperManager.legacyMode

    fun setLegacyMode(legacyMode: Boolean) {
        viperManager.setLegacyMode(legacyMode)
    }
}
