package com.example.videogames.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenresResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<GenreDto>
)

