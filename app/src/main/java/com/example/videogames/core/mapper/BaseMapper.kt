package com.example.videogames.core.mapper

abstract class BaseMapper<in From, out To> {

    abstract fun from(from: From): To

    fun from(fromList: List<From>?): List<To> {
        if (fromList.isNullOrEmpty()) return emptyList()
        return fromList.map { from(it) }
    }
}