package com.bwasik.utils

import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RedisCache(
    redisConnection: StatefulRedisConnection<String, String>,
    val json: Json = Json { ignoreUnknownKeys = true }
) {
    val redis: RedisCommands<String, String> = redisConnection.sync()

    inline fun <reified T> cached(
        key: String,
        namespace: String? = null,
        cacheTime: Long,
        block: () -> T
    ): T {
        val namespacedKey = listOfNotNull(namespace, key).joinToString(":")
        redis.get(namespacedKey)?.let { cachedValue ->
            return json.decodeFromString<T>(cachedValue)
        }
        val value = block()
        redis.setex(namespacedKey, cacheTime, json.encodeToString(value))
        return value
    }
}