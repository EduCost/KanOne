package com.educost.kanone.domain.util

interface JsonConverter {
    fun <T> fromJson(json: String, type: Class<T>): T
    fun <T> toJson(value: T): String
}