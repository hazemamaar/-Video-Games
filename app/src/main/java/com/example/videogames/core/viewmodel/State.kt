package com.example.videogames.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface State // Represents UI states
interface Intent // Represents user intents or actions
interface Event // Represents one time events


/**
 * Base ViewModel to handle UI state, user intents, and one-time events.
 *
 * @param S Represents the UI state.
 * @param E Represents one-time events.
 * @param I Represents user intents or actions.
 */
abstract class BaseViewModel<S : State, I : Intent>(initialState: S) : ViewModel() {

    private val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<S> = _viewState.asStateFlow()

    private val _eventChannel = MutableSharedFlow<I>()
    val eventFlow: Flow<I> = _eventChannel.asSharedFlow()


    init {
        viewModelScope.launch {
            _eventChannel.collect { handleIntent(it) }
        }
    }

    protected fun setState(state: S) {
        _viewState.value = state
    }

    protected abstract fun handleIntent(intent: I)

    fun sendIntent(intent: I) {
        viewModelScope.launch {
            _eventChannel.emit(intent)
        }
    }
}