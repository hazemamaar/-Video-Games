package com.example.videogames.ui.screens.gamedetails.contract

import com.example.videogames.core.viewmodel.State
import com.example.videogames.domain.model.GameDetails

sealed interface GameDetailsState : State {
    data object Idle : GameDetailsState
    
    data class Loaded(
        val game: GameDetails? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : GameDetailsState
}

