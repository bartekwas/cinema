package com.bwasik.plugins

import com.bwasik.utils.DatabaseFactory
import io.ktor.server.application.*
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.setupCache() {

}

interface Cache{
    fun cached(
        key: String,
        cacheTime: Long,
        block: () -> T
    ): T
}



class RedisCache<T>(
    redisConnection: StatefulRedisConnection<String, String>,
    val namespace: String,
    val json: Json = Json { ignoreUnknownKeys = true }
): Cache {
    val redis: RedisCommands<String, String> = redisConnection.sync()

    inline fun <reified T> cached(
        key: String,
        cacheTime: Long,
        block: () -> T
    ): T {
        val namespacedKey = "$namespace:$key"
        redis.get(namespacedKey)?.let { cachedValue ->
            return json.decodeFromString<T>(cachedValue)
        }
        val value = block()
        redis.setex(namespacedKey, cacheTime, json.encodeToString(value))
        return value
    }
}