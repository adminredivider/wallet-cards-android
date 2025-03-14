package io.walletcards.android.presentation.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.MutableLiveData
import io.walletcards.android.domain.model.Card
import io.walletcards.android.domain.model.PassGroup
import io.walletcards.android.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
