package com.educost.kanone.presentation.screens.markdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.domain.usecase.UpdateMarkdownUseCase
import com.educost.kanone.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MarkdownViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val updateMarkdownUseCase: UpdateMarkdownUseCase
) : ViewModel() {

    private val _markdown = MutableStateFlow<String?>(null)
    val markdown = _markdown.asStateFlow()


    fun loadMarkdown(cardId: Long) {
        viewModelScope.launch {
            val cardResult = cardRepository.getCard(cardId)
            if (cardResult is Result.Success) {
                _markdown.value = cardResult.data.description
            }
        }
    }

    fun saveMarkdown(cardId: Long, markdown: String) {
        viewModelScope.launch {
            updateMarkdownUseCase(cardId, markdown)
        }
    }

}