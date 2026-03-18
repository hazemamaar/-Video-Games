package com.example.videogames.ui.screens.gamedetails.contract

import com.example.videogames.domain.model.GameDetails

data class GameDetailsState(
    val game: GameDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

