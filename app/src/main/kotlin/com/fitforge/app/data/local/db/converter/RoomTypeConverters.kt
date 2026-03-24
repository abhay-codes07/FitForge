package com.fitforge.app.data.local.db.converter

import androidx.room.TypeConverter

class RoomTypeConverters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value.orEmpty().encodeCollection()

    @TypeConverter
    fun toStringList(value: String): List<String> = decodeCollection(value)

    @TypeConverter
    fun fromStringSet(value: Set<String>?): String = value.orEmpty().toList().encodeCollection()

    @TypeConverter
    fun toStringSet(value: String): Set<String> = decodeCollection(value).toSet()

    private fun List<String>.encodeCollection(): String = joinToString(separator = SEPARATOR) {
        buildString {
            it.forEach { char ->
                when (char) {
                    ESCAPE_CHAR -> append(ESCAPE_CHAR).append(ESCAPE_CHAR)
                    SEPARATOR_CHAR -> append(ESCAPE_CHAR).append(SEPARATOR_CHAR)
                    else -> append(char)
                }
            }
        }
    }

    private fun decodeCollection(value: String): List<String> {
        if (value.isEmpty()) return emptyList()

        val items = mutableListOf<String>()
        val current = StringBuilder()
        var escaping = false

        value.forEach { char ->
            when {
                escaping -> {
                    current.append(char)
                    escaping = false
                }
                char == ESCAPE_CHAR -> escaping = true
                char == SEPARATOR_CHAR -> {
                    items += current.toString()
                    current.clear()
                }
                else -> current.append(char)
            }
        }

        if (escaping) {
            current.append(ESCAPE_CHAR)
        }

        items += current.toString()
        return items
    }

    private companion object {
        const val SEPARATOR = "\u001F"
        const val SEPARATOR_CHAR = '\u001F'
        const val ESCAPE_CHAR = '\\'
    }
}
