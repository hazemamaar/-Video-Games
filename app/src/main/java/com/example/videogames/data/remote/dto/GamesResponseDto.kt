package com.example.videogames.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GamesResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<GameDto>
)

