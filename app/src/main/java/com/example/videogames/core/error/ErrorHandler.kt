package com.example.videogames.core.error

import com.example.videogames.core.VideoGameException

interface ErrorHandler {
    fun getError(throwable: Throwable): VideoGameException

}