package com.aam.viper4android.driver

data class Preset(
    var enabled: Boolean = DEFAULT_ENABLED,
    var legacyMode: Boolean = DEFAULT_LEGACY_MODE,
    val analogX: AnalogX = AnalogX(),
    val auditorySystemProtection: AuditorySystemProtection = AuditorySystemProtection(),
    val convolver: Convolver = Convolver(),
    val differentialSurround: DifferentialSurround = DifferentialSurround(),
    val dynamicSystem: DynamicSystem = DynamicSystem(),
    val fetCompressor: FETCompressor = FETCompressor(),
    val fieldSurround: FieldSurround = FieldSurround(),
    val firEqualizer: FIREqualizer = FIREqualizer(),
    val headphoneSurroundPlus: HeadphoneSurroundPlus = HeadphoneSurroundPlus(),
    val masterLimiter: MasterLimiter = MasterLimiter(),
    val playbackGainControl: PlaybackGainControl = PlaybackGainControl(),
    val reverberation: Reverberation = Reverberation(),
    val speakerOptimization: SpeakerOptimization = SpeakerOptimization(),
    val spectrumExtension: SpectrumExtension = SpectrumExtension(),
    val tubeSimulator6N1J: TubeSimulator6N1J = TubeSimulator6N1J(),
    val viperBass: ViPERBass = ViPERBass(),
    val viperClarity: ViPERClarity = ViPERClarity(),
    val viperDdc: ViPERDDC = ViPERDDC(),
) {
    data class AnalogX(
        var enabled: Boolean = DEFAULT_ENABLED,
        var level: Int = DEFAULT_LEVEL
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_LEVEL: Int = 1
        }
    }

    data class AuditorySystemProtection(
        var enabled: Boolean = DEFAULT_ENABLED,
        var level: Int = DEFAULT_LEVEL
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_LEVEL: Int = 1
        }
    }

    data class Convolver(
        var enabled: Boolean = DEFAULT_ENABLED,
        var irPath: String = DEFAULT_IR_PATH,
        var crossChannel: Int = DEFAULT_CROSS_CHANNEL
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_IR_PATH: String = ""
            const val DEFAULT_CROSS_CHANNEL: Int = 0
        }
    }

    data class DifferentialSurround(
        var enabled: Boolean = DEFAULT_ENABLED,
        var delay: Int = DEFAULT_DELAY
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_DELAY: Int = 5
        }
    }

    data class DynamicSystem(
        var enabled: Boolean = DEFAULT_ENABLED,
        var deviceType: DeviceType = DEFAULT_DEVICE_TYPE,
        var dynamicBassStrength: Int = DEFAULT_DYNAMIC_BASS_STRENGTH
    ) {
        enum class DeviceType(
            val xLow: Int, val xHigh: Int,
            val yLow: Int, val yHigh: Int,
            val gainX: Int, val gainY: Int,
        ) {
            EXTREME_HEADPHONE_V2(140, 6200, 40, 60, 10, 80),
            HIGH_END_HEADPHONE_V2(180, 5800, 55, 80, 10, 70),
            COMMON_HEADPHONE_V2(300, 5600, 60, 105, 10, 50),
            LOW_END_HEADPHONE_V2(600, 5400, 60, 105, 10, 20),
            COMMON_EARPHONE_V2(100, 5600, 40, 80, 50, 50),
            EXTREME_HEADPHONE_V1(1200, 6200, 40, 80, 0, 20),
            HIGH_END_HEADPHONE_V1(1000, 6200, 40, 80, 0, 10),
            COMMON_HEADPHONE_V1(800, 6200, 40, 80, 10, 0),
            COMMON_EARPHONE_V1(400, 6200, 40, 80, 10, 0),
            APPLE_EARPHONE(1200, 6200, 50, 90, 15, 10),
            MONSTER_EARPHONE(1000, 6200, 50, 90, 30, 10),
            MOTOROLA_EARPHONE(1100, 6200, 60, 100, 20, 0),
            PHILIPS_EARPHONE(1200, 6200, 50, 100, 10, 50),
            SHP2000(1200, 6200, 60, 100, 0, 30),
            SHP9000(1200, 6200, 40, 80, 0, 30),
            UNKNOWN_TYPE_I(1000, 6200, 60, 100, 0, 0),
            UNKNOWN_TYPE_II(1000, 6200, 60, 120, 0, 0),
            UNKNOWN_TYPE_III(1000, 6200, 80, 140, 0, 0),
            UNKNOWN_TYPE_IV(800, 6200, 80, 140, 0, 0),
            UNKNOWN_TYPE_V(0, 0, 0, 0, 0, 0),
            PITT_VAN_DE_WITT_FLAVOR_1(180, 5400, 40, 60, 50, 0),
            PITT_VAN_DE_WITT_FLAVOR_2(1200, 6000, 40, 60, 0, 80),
            PITT_VAN_DE_WITT_FLAVOR_3(140, 5400, 40, 60, 0, 0),
        }

        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            val DEFAULT_DEVICE_TYPE: DeviceType = DeviceType.COMMON_EARPHONE_V2
            const val DEFAULT_DYNAMIC_BASS_STRENGTH: Int = 0
        }
    }

    data class FETCompressor(
        var enabled: Boolean = DEFAULT_ENABLED,
        var operatingThreshold: Int = DEFAULT_OPERATING_THRESHOLD,
        var compressionRatio: Int = DEFAULT_COMPRESSION_RATIO,
        var autoKnee: Boolean = DEFAULT_AUTO_KNEE,
        var inflection: Int = DEFAULT_INFLECTION,
        var inflectionPointGain: Int = DEFAULT_INFLECTION_POINT_GAIN,
        var autoGain: Boolean = DEFAULT_AUTO_GAIN,
        var gain: Int = DEFAULT_GAIN,
        var autoAttack: Boolean = DEFAULT_AUTO_ATTACK,
        var attack: Int = DEFAULT_ATTACK,
        var maximumAttack: Int = DEFAULT_MAXIMUM_ATTACK,
        var autoRelease: Boolean = DEFAULT_AUTO_RELEASE,
        var release: Int = DEFAULT_RELEASE,
        var maximumRelease: Int = DEFAULT_MAXIMUM_RELEASE,
        var crest: Int = DEFAULT_CREST,
        var adapt: Int = DEFAULT_ADAPT,
        var clippingPrevention: Boolean = DEFAULT_CLIPPING_PREVENTION
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_OPERATING_THRESHOLD: Int = 100
            const val DEFAULT_COMPRESSION_RATIO: Int = 100
            const val DEFAULT_AUTO_KNEE: Boolean = true
            const val DEFAULT_INFLECTION: Int = 0
            const val DEFAULT_INFLECTION_POINT_GAIN: Int = 0
            const val DEFAULT_AUTO_GAIN: Boolean = true
            const val DEFAULT_GAIN: Int = 0
            const val DEFAULT_AUTO_ATTACK: Boolean = true
            const val DEFAULT_ATTACK: Int = 20
            const val DEFAULT_MAXIMUM_ATTACK: Int = 80
            const val DEFAULT_AUTO_RELEASE: Boolean = true
            const val DEFAULT_RELEASE: Int = 50
            const val DEFAULT_MAXIMUM_RELEASE: Int = 100
            const val DEFAULT_CREST: Int = 100
            const val DEFAULT_ADAPT: Int = 50
            const val DEFAULT_CLIPPING_PREVENTION: Boolean = true
        }
    }

    data class FieldSurround(
        var enabled: Boolean = DEFAULT_ENABLED,
        var surroundStrength: Int = DEFAULT_SURROUND_STRENGTH,
        var midImageStrength: Int = DEFAULT_MID_IMAGE_STRENGTH,
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_SURROUND_STRENGTH: Int = 0
            const val DEFAULT_MID_IMAGE_STRENGTH: Int = 5
        }
    }

    data class FIREqualizer(
        var enabled: Boolean = DEFAULT_ENABLED,
        var gains: List<Float> = DEFAULT_GAINS
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            val DEFAULT_GAINS: List<Float> = List(10) { 0f }
        }
    }

    data class HeadphoneSurroundPlus(
        var enabled: Boolean = DEFAULT_ENABLED,
        var level: Int = DEFAULT_LEVEL
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_LEVEL: Int = 1
        }
    }

    data class MasterLimiter(
        var outputGain: Int = DEFAULT_OUTPUT_GAIN,
        var outputPan: Int = DEFAULT_OUTPUT_PAN,
        var thresholdLimit: Int = DEFAULT_THRESHOLD_LIMIT
    ) {
        companion object {
            const val DEFAULT_OUTPUT_GAIN: Int = 100
            const val DEFAULT_OUTPUT_PAN: Int = 50
            const val DEFAULT_THRESHOLD_LIMIT: Int = 100
        }
    }

    data class PlaybackGainControl(
        var enabled: Boolean = DEFAULT_ENABLED,
        var strength: Int = DEFAULT_STRENGTH,
        var maximumGain: Int = DEFAULT_MAXIMUM_GAIN,
        var outputThreshold: Int = DEFAULT_OUTPUT_THRESHOLD
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_STRENGTH: Int = 0
            const val DEFAULT_MAXIMUM_GAIN: Int = 3
            const val DEFAULT_OUTPUT_THRESHOLD: Int = 3
        }
    }

    data class Reverberation(
        var enabled: Boolean = DEFAULT_ENABLED,
        var roomSize: Int = DEFAULT_ROOM_SIZE,
        var soundField: Int = DEFAULT_SOUND_FIELD,
        var damping: Int = DEFAULT_DAMPING,
        var wetSignal: Int = DEFAULT_WET_SIGNAL,
        var drySignal: Int = DEFAULT_DRY_SIGNAL
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_ROOM_SIZE: Int = 0
            const val DEFAULT_SOUND_FIELD: Int = 0
            const val DEFAULT_DAMPING: Int = 0
            const val DEFAULT_WET_SIGNAL: Int = 0
            const val DEFAULT_DRY_SIGNAL: Int = 50
        }
    }

    data class SpeakerOptimization(
        var enabled: Boolean = DEFAULT_ENABLED
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
        }
    }

    data class SpectrumExtension(
        var enabled: Boolean = DEFAULT_ENABLED,
        var strength: Int = DEFAULT_STRENGTH
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_STRENGTH: Int = 10
        }
    }

    data class TubeSimulator6N1J(
        var enabled: Boolean = DEFAULT_ENABLED
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
        }
    }

    data class ViPERBass(
        var enabled: Boolean = DEFAULT_ENABLED,
        var mode: Int = DEFAULT_MODE,
        var frequency: Int = DEFAULT_FREQUENCY,
        var gain: Int = DEFAULT_GAIN,
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_MODE: Int = 0
            const val DEFAULT_FREQUENCY: Int = 70
            const val DEFAULT_GAIN: Int = 1
        }
    }

    data class ViPERClarity(
        var enabled: Boolean = DEFAULT_ENABLED,
        var mode: Int = DEFAULT_MODE,
        var gain: Int = DEFAULT_GAIN,
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_MODE: Int = 0
            const val DEFAULT_GAIN: Int = 1
        }
    }

    data class ViPERDDC(
        var enabled: Boolean = DEFAULT_ENABLED,
        var ddcPath: String = DEFAULT_DDC_PATH
    ) {
        companion object {
            const val DEFAULT_ENABLED: Boolean = false
            const val DEFAULT_DDC_PATH: String = ""
        }
    }

    companion object {
        const val DEFAULT_ENABLED: Boolean = false
        const val DEFAULT_LEGACY_MODE: Boolean = false
    }
}