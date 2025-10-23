package com.aam.viper4android.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aam.viper4android.driver.Preset

@Entity(tableName = "presets")
data class PersistedPreset(
    @PrimaryKey @ColumnInfo(name = "route_id") val routeId: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean,
    @ColumnInfo(name = "legacy_mode") val legacyMode: Boolean,
    @Embedded(prefix = "analog_x") val analogX: AnalogX,
    @Embedded(prefix = "auditory_system_protection") val auditorySystemProtection: AuditorySystemProtection,
    @Embedded(prefix = "convolver") val convolver: Convolver,
    @Embedded(prefix = "differential_surround") val differentialSurround: DifferentialSurround,
    @Embedded(prefix = "dynamic_system") val dynamicSystem: DynamicSystem,
    @Embedded(prefix = "fet_compressor") val fetCompressor: FETCompressor,
    @Embedded(prefix = "field_surround") val fieldSurround: FieldSurround,
    @Embedded(prefix = "fir_equalizer") val firEqualizer: FIREqualizer,
    @Embedded(prefix = "headphone_surround_plus") val headphoneSurroundPlus: HeadphoneSurroundPlus,
    @Embedded(prefix = "master_limiter") val masterLimiter: MasterLimiter,
    @Embedded(prefix = "playback_gain_control") val playbackGainControl: PlaybackGainControl,
    @Embedded(prefix = "reverberation") val reverberation: Reverberation,
    @Embedded(prefix = "speaker_optimization") val speakerOptimization: SpeakerOptimization,
    @Embedded(prefix = "spectrum_extension") val spectrumExtension: SpectrumExtension,
    @Embedded(prefix = "tube_simulator_6n1j") val tubeSimulator6N1J: TubeSimulator6N1J,
    @Embedded(prefix = "viper_bass") val viperBass: ViPERBass,
    @Embedded(prefix = "viper_clarity") val viperClarity: ViPERClarity,
    @Embedded(prefix = "viper_ddc") val viperDDC: ViPERDDC,
) {
    data class AnalogX(
        var enabled: Boolean,
        var level: Int
    )

    data class AuditorySystemProtection(
        var enabled: Boolean,
        var level: Int
    )

    data class Convolver(
        var enabled: Boolean,
        var irPath: String,
        var crossChannel: Int
    )

    data class DifferentialSurround(
        var enabled: Boolean,
        var delay: Int
    )

    data class DynamicSystem(
        var enabled: Boolean,
        var deviceType: String,
        var dynamicBassStrength: Int
    )

    data class FETCompressor(
        var enabled: Boolean,
        var operatingThreshold: Int,
        var compressionRatio: Int,
        var autoKnee: Boolean,
        var inflection: Int,
        var inflectionPointGain: Int,
        var autoGain: Boolean,
        var gain: Int,
        var autoAttack: Boolean,
        var attack: Int,
        var maximumAttack: Int,
        var autoRelease: Boolean,
        var release: Int,
        var maximumRelease: Int,
        var crest: Int,
        var adapt: Int,
        var clippingPrevention: Boolean
    )

    data class FieldSurround(
        var enabled: Boolean,
        var surroundStrength: Int,
        var midImageStrength: Int
    )

    data class FIREqualizer(
        var enabled: Boolean,
        var gains: List<Float>
    )

    data class HeadphoneSurroundPlus(
        var enabled: Boolean,
        var level: Int
    )

    data class MasterLimiter(
        var outputGain: Int,
        var outputPan: Int,
        var thresholdLimit: Int
    )

    data class PlaybackGainControl(
        var enabled: Boolean,
        var strength: Int,
        var maximumGain: Int,
        var outputThreshold: Int
    )

    data class Reverberation(
        var enabled: Boolean,
        var roomSize: Int,
        var soundField: Int,
        var damping: Int,
        var wetSignal: Int,
        var drySignal: Int
    )

    data class SpeakerOptimization(
        var enabled: Boolean
    )

    data class SpectrumExtension(
        var enabled: Boolean,
        var strength: Int
    )

    data class TubeSimulator6N1J(
        var enabled: Boolean
    )

    data class ViPERBass(
        var enabled: Boolean,
        var mode: Int,
        var frequency: Int,
        var gain: Int
    )

    data class ViPERClarity(
        var enabled: Boolean,
        var mode: Int,
        var gain: Int
    )

    data class ViPERDDC(
        var enabled: Boolean,
        var ddcPath: String
    )

    fun toPreset(): Preset {
        return Preset(
            enabled = enabled,
            legacyMode = legacyMode,
            analogX = Preset.AnalogX(
                enabled = analogX.enabled,
                level = analogX.level
            ),
            auditorySystemProtection = Preset.AuditorySystemProtection(
                enabled = auditorySystemProtection.enabled,
                level = auditorySystemProtection.level
            ),
            convolver = Preset.Convolver(
                enabled = convolver.enabled,
                irPath = convolver.irPath,
                crossChannel = convolver.crossChannel
            ),
            differentialSurround = Preset.DifferentialSurround(
                enabled = differentialSurround.enabled,
                delay = differentialSurround.delay
            ),
            dynamicSystem = Preset.DynamicSystem(
                enabled = dynamicSystem.enabled,
                deviceType = Preset.DynamicSystem.DeviceType.valueOf(dynamicSystem.deviceType),
                dynamicBassStrength = dynamicSystem.dynamicBassStrength
            ),
            fetCompressor = Preset.FETCompressor(
                enabled = fetCompressor.enabled,
                operatingThreshold = fetCompressor.operatingThreshold,
                compressionRatio = fetCompressor.compressionRatio,
                autoKnee = fetCompressor.autoKnee,
                inflection = fetCompressor.inflection,
                inflectionPointGain = fetCompressor.inflectionPointGain,
                autoGain = fetCompressor.autoGain,
                gain = fetCompressor.gain,
                autoAttack = fetCompressor.autoAttack,
                attack = fetCompressor.attack,
                maximumAttack = fetCompressor.maximumAttack,
                autoRelease = fetCompressor.autoRelease,
                release = fetCompressor.release,
                maximumRelease = fetCompressor.maximumRelease,
                crest = fetCompressor.crest,
                adapt = fetCompressor.adapt,
                clippingPrevention = fetCompressor.clippingPrevention
            ),
            fieldSurround = Preset.FieldSurround(
                enabled = fieldSurround.enabled,
                surroundStrength = fieldSurround.surroundStrength,
                midImageStrength = fieldSurround.midImageStrength
            ),
            firEqualizer = Preset.FIREqualizer(
                enabled = firEqualizer.enabled,
                gains = firEqualizer.gains
            ),
            headphoneSurroundPlus = Preset.HeadphoneSurroundPlus(
                enabled = headphoneSurroundPlus.enabled,
                level = headphoneSurroundPlus.level
            ),
            masterLimiter = Preset.MasterLimiter(
                outputGain = masterLimiter.outputGain,
                outputPan = masterLimiter.outputPan,
                thresholdLimit = masterLimiter.thresholdLimit
            ),
            playbackGainControl = Preset.PlaybackGainControl(
                enabled = playbackGainControl.enabled,
                strength = playbackGainControl.strength,
                maximumGain = playbackGainControl.maximumGain,
                outputThreshold = playbackGainControl.outputThreshold
            ),
            reverberation = Preset.Reverberation(
                enabled = reverberation.enabled,
                roomSize = reverberation.roomSize,
                soundField = reverberation.soundField,
                damping = reverberation.damping,
                wetSignal = reverberation.wetSignal,
                drySignal = reverberation.drySignal
            ),
            speakerOptimization = Preset.SpeakerOptimization(
                enabled = speakerOptimization.enabled
            ),
            spectrumExtension = Preset.SpectrumExtension(
                enabled = spectrumExtension.enabled,
                strength = spectrumExtension.strength
            ),
            tubeSimulator6N1J = Preset.TubeSimulator6N1J(
                enabled = tubeSimulator6N1J.enabled
            ),
            viperBass = Preset.ViPERBass(
                enabled = viperBass.enabled,
                mode = viperBass.mode,
                frequency = viperBass.frequency,
                gain = viperBass.gain
            ),
            viperClarity = Preset.ViPERClarity(
                enabled = viperClarity.enabled,
                mode = viperClarity.mode,
                gain = viperClarity.gain
            ),
            viperDdc = Preset.ViPERDDC(
                enabled = viperDDC.enabled,
                ddcPath = viperDDC.ddcPath
            )
        )
    }

    companion object {
        fun fromPreset(routeId: String, preset: Preset): PersistedPreset {
            return PersistedPreset(
                routeId = routeId,
                enabled = preset.enabled,
                legacyMode = preset.legacyMode,
                analogX = AnalogX(
                    enabled = preset.analogX.enabled,
                    level = preset.analogX.level
                ),
                auditorySystemProtection = AuditorySystemProtection(
                    enabled = preset.auditorySystemProtection.enabled,
                    level = preset.auditorySystemProtection.level
                ),
                convolver = Convolver(
                    enabled = preset.convolver.enabled,
                    irPath = preset.convolver.irPath,
                    crossChannel = preset.convolver.crossChannel
                ),
                differentialSurround = DifferentialSurround(
                    enabled = preset.differentialSurround.enabled,
                    delay = preset.differentialSurround.delay
                ),
                dynamicSystem = DynamicSystem(
                    enabled = preset.dynamicSystem.enabled,
                    deviceType = preset.dynamicSystem.deviceType.name,
                    dynamicBassStrength = preset.dynamicSystem.dynamicBassStrength
                ),
                fetCompressor = FETCompressor(
                    enabled = preset.fetCompressor.enabled,
                    operatingThreshold = preset.fetCompressor.operatingThreshold,
                    compressionRatio = preset.fetCompressor.compressionRatio,
                    autoKnee = preset.fetCompressor.autoKnee,
                    inflection = preset.fetCompressor.inflection,
                    inflectionPointGain = preset.fetCompressor.inflectionPointGain,
                    autoGain = preset.fetCompressor.autoGain,
                    gain = preset.fetCompressor.gain,
                    autoAttack = preset.fetCompressor.autoAttack,
                    attack = preset.fetCompressor.attack,
                    maximumAttack = preset.fetCompressor.maximumAttack,
                    autoRelease = preset.fetCompressor.autoRelease,
                    release = preset.fetCompressor.release,
                    maximumRelease = preset.fetCompressor.maximumRelease,
                    crest = preset.fetCompressor.crest,
                    adapt = preset.fetCompressor.adapt,
                    clippingPrevention = preset.fetCompressor.clippingPrevention
                ),
                fieldSurround = FieldSurround(
                    enabled = preset.fieldSurround.enabled,
                    surroundStrength = preset.fieldSurround.surroundStrength,
                    midImageStrength = preset.fieldSurround.midImageStrength
                ),
                firEqualizer = FIREqualizer(
                    enabled = preset.firEqualizer.enabled,
                    gains = preset.firEqualizer.gains
                ),
                headphoneSurroundPlus = HeadphoneSurroundPlus(
                    enabled = preset.headphoneSurroundPlus.enabled,
                    level = preset.headphoneSurroundPlus.level
                ),
                masterLimiter = MasterLimiter(
                    outputGain = preset.masterLimiter.outputGain,
                    outputPan = preset.masterLimiter.outputPan,
                    thresholdLimit = preset.masterLimiter.thresholdLimit
                ),
                playbackGainControl = PlaybackGainControl(
                    enabled = preset.playbackGainControl.enabled,
                    strength = preset.playbackGainControl.strength,
                    maximumGain = preset.playbackGainControl.maximumGain,
                    outputThreshold = preset.playbackGainControl.outputThreshold
                ),
                reverberation = Reverberation(
                    enabled = preset.reverberation.enabled,
                    roomSize = preset.reverberation.roomSize,
                    soundField = preset.reverberation.soundField,
                    damping = preset.reverberation.damping,
                    wetSignal = preset.reverberation.wetSignal,
                    drySignal = preset.reverberation.drySignal
                ),
                speakerOptimization = SpeakerOptimization(
                    enabled = preset.speakerOptimization.enabled
                ),
                spectrumExtension = SpectrumExtension(
                    enabled = preset.spectrumExtension.enabled,
                    strength = preset.spectrumExtension.strength
                ),
                tubeSimulator6N1J = TubeSimulator6N1J(
                    enabled = preset.tubeSimulator6N1J.enabled
                ),
                viperBass = ViPERBass(
                    enabled = preset.viperBass.enabled,
                    mode = preset.viperBass.mode,
                    frequency = preset.viperBass.frequency,
                    gain = preset.viperBass.gain
                ),
                viperClarity = ViPERClarity(
                    enabled = preset.viperClarity.enabled,
                    mode = preset.viperClarity.mode,
                    gain = preset.viperClarity.gain
                ),
                viperDDC = ViPERDDC(
                    enabled = preset.viperDdc.enabled,
                    ddcPath = preset.viperDdc.ddcPath
                )
            )
        }
    }
}
