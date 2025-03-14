package com.digitalwallet.mobilecards.presentation.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.MutableLiveData
import com.digitalwallet.mobilecards.domain.model.Card
import com.digitalwallet.mobilecards.domain.model.PassGroup
import com.digitalwallet.mobilecards.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class MainViewModel : BaseViewModel() {
    abstract val state: StateFlow<MainScreenState>
    abstract val cardLiveData: MutableLiveData<List<Card>>
    abstract val groupedPassesLiveData: MutableLiveData<List<PassGroup>>
    abstract fun action(event: MainScreenAction)
    abstract val getCreditCardById:MutableLiveData<Card?>
}


sealed interface MainScreenAction {
    data object TutorialButtonClicked : MainScreenAction
    data object InfoButtonClicked : MainScreenAction
    data object LoadCardData : MainScreenAction
    data class RemoveCard(val card: Card) : MainScreenAction
    data class AddCreditCardAction(val card: Card) : MainScreenAction
    data class UpdateCreditCardAction(val card: Card) : MainScreenAction
    data class ScanPassbookAction(val url: String) : MainScreenAction
    data object ImportPassbookAction : MainScreenAction
    data class GetCreditCardById(val id: Int) : MainScreenAction
}

@Immutable
data class MainScreenState(
    val isTutorialWatched: Boolean = false,
)
