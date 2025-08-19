package com.educost.kanone.presentation.screens.card

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.usecase.ObserveCardUseCase
import com.educost.kanone.presentation.util.SnackbarEvent
import com.educost.kanone.presentation.util.UiText
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CardViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val observeCardUseCase: ObserveCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<CardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()


    fun onIntent(intent: CardIntent) {
        when(intent) {
            is CardIntent.ObserveCard -> observeCard(intent.cardId)
        }
    }


    private fun observeCard(cardId: Long) {
        viewModelScope.launch(dispatcherProvider.main) {
            observeCardUseCase(cardId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = _uiState.value.copy(card = result.data)

                    is Result.Error -> sendSnackbar(
                        SnackbarEvent(
                            message = UiText.StringResource(R.string.card_snackbar_observe_card_error),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    )
                }
            }
        }
    }

    private fun sendSnackbar(snackbarEvent: SnackbarEvent) {
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(
                CardSideEffect.ShowSnackBar(
                    snackbarEvent
                )
            )
        }
    }

}