package com.bwasik.utils

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig

val config by lazy {
    HoconApplicationConfig(ConfigFactory.load("application.conf"))
}

inline fun <reified T : Enum<T>> String.safeValueOf(): T? =
    try {
        enumValueOf<T>(this.uppercase())
    } catch (e: IllegalArgumentException) {
        null
    }

fun String.envVariable() = config.property(this).getString()
