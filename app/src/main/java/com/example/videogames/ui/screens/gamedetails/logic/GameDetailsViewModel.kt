package com.example.videogames.ui.screens.gamedetails.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videogames.core.response.Resource
import com.example.videogames.domain.usecase.GetGameDetailsUseCase
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsIntent
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val getGameDetailsUseCase: GetGameDetailsUseCase,
    ) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsState())
    val state: StateFlow<GameDetailsState> = _state.asStateFlow()


    fun processIntent(intent: GameDetailsIntent) {
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
                        if (resource.isLoading)
                            _state.update { it.copy(isLoading = true) }
                        else
                            _state.update { it.copy(isLoading = false) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                game = resource.data,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }
}