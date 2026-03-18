package com.example.videogames.ui.screens.gamedetails.logic

import androidx.lifecycle.viewModelScope
import com.example.videogames.core.response.Resource
import com.example.videogames.core.viewmodel.BaseViewModel
import com.example.videogames.domain.usecase.GetGameDetailsUseCase
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsIntent
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val getGameDetailsUseCase: GetGameDetailsUseCase,
) : BaseViewModel<GameDetailsState, GameDetailsIntent>(GameDetailsState.Idle) {

    override fun handleIntent(intent: GameDetailsIntent) {
        when (intent) {
            is GameDetailsIntent.LoadDetails -> loadGameDetails(intent.gameId)
            is GameDetailsIntent.Retry -> loadGameDetails(intent.gameId)
        }
    }

    private fun loadGameDetails(id: Int) {
        viewModelScope.launch {
            getGameDetailsUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { state ->
                            when (state) {
                                is GameDetailsState.Idle -> {
                                    GameDetailsState.Loaded(isLoading = resource.isLoading)
                                }
                                is GameDetailsState.Loaded -> {
                                    state.copy(isLoading = resource.isLoading)
                                }
                            }
                        }
                    }
                    is Resource.Success -> {
                        updateState { state ->
                            when (state) {
                                is GameDetailsState.Idle -> {
                                    GameDetailsState.Loaded(
                                        game = resource.data,
                                        isLoading = false,
                                        error = null
                                    )
                                }
                                is GameDetailsState.Loaded -> {
                                    state.copy(
                                        game = resource.data,
                                        isLoading = false,
                                        error = null
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        updateState { state ->
                            when (state) {
                                is GameDetailsState.Idle -> {
                                    GameDetailsState.Loaded(
                                        isLoading = false,
                                        error = resource.message
                                    )
                                }
                                is GameDetailsState.Loaded -> {
                                    state.copy(
                                        isLoading = false,
                                        error = resource.message
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateState(update: (GameDetailsState) -> GameDetailsState) {
        setState(update(viewState.value))
    }
}