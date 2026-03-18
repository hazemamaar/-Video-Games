package com.example.videogames.ui.screens.gamedetails.contract

sealed class GameDetailsIntent {
    data class LoadDetails(val gameId: Int) : GameDetailsIntent()
    data class Retry(val gameId: Int) : GameDetailsIntent()
}

