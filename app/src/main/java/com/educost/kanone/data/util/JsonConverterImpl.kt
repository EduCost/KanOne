package com.educost.kanone.data.util

import com.educost.kanone.domain.util.JsonConverter
import com.google.gson.Gson

class JsonConverterImpl(private val gson: Gson = Gson()) : JsonConverter {
    override fun <T> fromJson(json: String, type: Class<T>): T {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(value: T): String {
        return gson.toJson(value)
    }
}