package com.aam.viper4android.persistence.converter

import androidx.room.TypeConverter

class FloatListConverter {
    @TypeConverter
    fun fromFloatList(list: List<Float>?): String {
        // convert [0.1, 0.2, 0.3] → "0.1,0.2,0.3"
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        if (value.isBlank()) return emptyList()
        // split "0.1,0.2,0.3" → [0.1f, 0.2f, 0.3f]
        return value.split(",").mapNotNull {
            it.toFloatOrNull()
        }
    }
}