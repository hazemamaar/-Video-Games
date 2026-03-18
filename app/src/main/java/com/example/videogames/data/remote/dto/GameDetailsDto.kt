package com.example.videogames.data.remote.dto

import com.google.gson.annotations.SerializedName


data class GameDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("released") val released: String?,
    @SerializedName("description_raw") val descriptionRaw: String?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("publishers") val publishers: List<PublisherDto>?,
    @SerializedName("platforms") val platforms: List<PlatformWrapperDto>?
)

data class PublisherDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class PlatformWrapperDto(
    @SerializedName("platform") val platform: PlatformDto
)

data class PlatformDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

