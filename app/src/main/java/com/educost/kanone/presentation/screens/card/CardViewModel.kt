package com.educost.kanone.presentation.screens.card

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.R
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.usecase.GetCardColumnIdUseCase
import com.educost.kanone.domain.usecase.ObserveCardUseCase
import com.educost.kanone.domain.usecase.UpdateCardUseCase
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType
import com.educost.kanone.presentation.util.SnackbarEvent
import com.educost.kanone.presentation.util.UiText
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CardViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val observeCardUseCase: ObserveCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getCardColumnIdUseCase: GetCardColumnIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<CardSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()


    fun onIntent(intent: CardIntent) {
        when (intent) {
            is CardIntent.ObserveCard -> observeCard(intent.cardId)

            // Description
            is CardIntent.StartEditingDescription -> startEditingDescription()
            is CardIntent.OnDescriptionChanged -> onDescriptionChanged(intent.description)
            is CardIntent.SaveDescription -> saveDescription()
            is CardIntent.CancelEditingDescription -> cancelEditingDescription()
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


    // Description
    private fun startEditingDescription() {
        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.DESCRIPTION,
                newDescription = it.card?.description
            )
        }
    }

    private fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(newDescription = description) }
    }

    private fun saveDescription() {
        viewModelScope.launch(dispatcherProvider.main) {
            val card = _uiState.value.card!!

            val newDescription = _uiState.value.newDescription
            val columnId = getColumnId(card.id)

            if (newDescription != null && columnId != null) {

                val newCard = card.copy(description = newDescription)
                val result = updateCardUseCase(newCard, columnId)

                if (result is Result.Error) sendSnackbar(
                    SnackbarEvent(
                        message = UiText.StringResource(R.string.card_snackbar_save_description_error),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                )

            } else sendSnackbar(
                SnackbarEvent(
                    message = UiText.StringResource(R.string.card_snackbar_save_description_error),
                    withDismissAction = true,
                )
            )

            cancelEditingDescription()
        }
    }

    private fun cancelEditingDescription() {
        _uiState.update {
            it.copy(
                appBarType = CardAppBarType.DEFAULT,
                newDescription = null
            )
        }
    }


    // Helper functions
    private fun sendSnackbar(snackbarEvent: SnackbarEvent) {
        viewModelScope.launch(dispatcherProvider.main) {
            _sideEffectChannel.send(
                CardSideEffect.ShowSnackBar(
                    snackbarEvent
                )
            )
        }
    }

    private suspend fun getColumnId(cardId: Long): Long? {

        val result = getCardColumnIdUseCase(cardId)

        return if (result is Result.Success) result.data else null
    }

}