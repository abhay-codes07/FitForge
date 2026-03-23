package com.fitforge.app.util

import java.util.Locale

fun Throwable.readableMessage(defaultMessage: String = "Something went wrong"): String {
    return message?.takeIf { it.isNotBlank() } ?: defaultMessage
}

fun String.toTitleCase(locale: Locale = Locale.getDefault()): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(locale)
        } else {
            char.toString()
        }
    }
}

