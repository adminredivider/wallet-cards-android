package io.walletcards.mobilecards.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.reflect.full.createInstance

context(ViewModel)
inline fun <reified STATE : Any> Flow<STATE>.stateInViewModelScope(
    started: SharingStarted = SharingStarted.WhileSubscribed(5000L),
    initialValue: STATE = STATE::class.createInstance()
): StateFlow<STATE> = stateIn(
    scope = viewModelScope,
    started = started,
    initialValue = initialValue
)