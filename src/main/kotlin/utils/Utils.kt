package com.bwasik.utils

inline fun <reified T : Enum<T>> String.safeValueOf(): T? {
    return try {
        enumValueOf<T>(this.uppercase())
    } catch (e: IllegalArgumentException) {
        null
    }
}