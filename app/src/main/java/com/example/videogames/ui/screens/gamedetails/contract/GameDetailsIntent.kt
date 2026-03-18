package com.example.videogames.ui.screens.gamedetails.contract

import com.example.videogames.core.viewmodel.Intent

sealed class GameDetailsIntent : Intent {
    data class LoadDetails(val gameId: Int) : GameDetailsIntent()
    data class Retry(val gameId: Int) : GameDetailsIntent()
}

