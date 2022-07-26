package icu.ketal.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal inline fun <reified T> T.encodeToJson(): String {
    return json.encodeToString(this)
}

internal inline fun <reified T> String.decodeToDataClass(): T {
    return json.decodeFromString(this)
}

