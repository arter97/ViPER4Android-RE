package com.aam.viper4android.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aam.viper4android.driver.Preset
import com.aam.viper4android.driver.ViPERManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FIREqualizerViewModel @Inject constructor(
    private val viperManager: ViPERManager,
) : ViewModel() {
    private val firEqualizer = viperManager.firEqualizer

    val enabled = firEqualizer.enabled
    val gains = firEqualizer.gains

    fun setEnabled(enabled: Boolean) {
        firEqualizer.setEnabled(enabled)
    }

    fun setGains(gains: List<Float>) {
        Log.i("FUCK:FIREqualizerViewModel", "setGains: $gains")
        firEqualizer.setGains(gains)
    }

    fun setBandCount(count: Int) {
        Log.i("FUCK:FIREqualizerViewModel", "setBandCount: $count")
        val current = firEqualizer.gains.value
        val adjusted = when {
            current.size == count -> current
            current.size > count -> current.take(count)
            else -> current + List(count - current.size) { 0f }
        }
        firEqualizer.setGains(adjusted)
    }

    fun resetGains() {
        Log.i("FUCK:FIREqualizerViewModel", "resetGains")
        val size = firEqualizer.gains.value.size
        val defaults = if (size == Preset.FIREqualizer.DEFAULT_GAINS.size) {
            Preset.FIREqualizer.DEFAULT_GAINS
        } else {
            List(size) { 0f }
        }
        firEqualizer.setGains(defaults)
    }
}