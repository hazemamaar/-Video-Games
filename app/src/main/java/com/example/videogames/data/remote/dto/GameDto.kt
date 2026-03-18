package com.example.videogames.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("released") val released: String?,
    @SerializedName("description_raw") val descriptionRaw: String?,
    @SerializedName("genres") val genres: List<GenreDto>?
)

