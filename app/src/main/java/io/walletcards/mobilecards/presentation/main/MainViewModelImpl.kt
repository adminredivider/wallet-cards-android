package io.walletcards.mobilecards.presentation.main

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.walletcards.mobilecards.data.local.preferences.Preferences
import io.walletcards.mobilecards.data.local.preferences.Preferences.Key.IS_TUTORIAL_WATCHED
import io.walletcards.mobilecards.domain.model.Card
import io.walletcards.mobilecards.domain.model.PassGroup
import io.walletcards.mobilecards.domain.model.PassFile
import io.walletcards.mobilecards.domain.model.PassType
import io.walletcards.mobilecards.domain.repository.CardRepository
import io.walletcards.mobilecards.presentation.passbook.PassFileIO
import io.walletcards.mobilecards.presentation.util.buildUrlWithQueryParameter
import io.walletcards.mobilecards.presentation.util.getCardTypeString
import io.walletcards.mobilecards.presentation.util.stateInViewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber


@KoinViewModel(binds = [MainViewModel::class])
class MainViewModelImpl(
    private val preferences: Preferences,
    private val cardRepository: CardRepository,
    private val passFileIO: PassFileIO,
) : MainViewModel() {
    override val cardLiveData = MutableLiveData<List<Card>>()
    override val groupedPassesLiveData = MutableLiveData<List<PassGroup>>()
    private val passGroup = mutableListOf<PassGroup>()

    private val _state = MutableStateFlow(MainScreenState())
    override val state: StateFlow<MainScreenState> = _state
        .onStart {
            checkTutorialWatch()
            getCreditCards()
            getPassbookList()
        }.stateInViewModelScope()

    override fun action(event: MainScreenAction) {
        when (event) {
            is MainScreenAction.TutorialButtonClicked -> onTutorialButtonClick()
            is MainScreenAction.InfoButtonClicked -> onInfoButtonClick()
            is MainScreenAction.AddCreditCardAction -> addCreditCard(event.card)
            is MainScreenAction.LoadCardData -> getCreditCards()
            is MainScreenAction.RemoveCard -> onCardRemove(event.card)
            is MainScreenAction.ScanPassbookAction -> addScannedPassbook(event.url)
            is MainScreenAction.ImportPassbookAction -> getPassbookList()
            is MainScreenAction.UpdateCreditCardAction -> updateCreditCard(event.card)
            is MainScreenAction.GetCreditCardById -> getCreditCardById(event.id)
        }
    }

    override val getCreditCardById = MutableLiveData<Card?>()

    private fun getCreditCardById(id: Int) {
        launch(success = {
            val card = cardRepository.getCardById(id)
            getCreditCardById.postValue(card)
        }, error = {
            Timber.d(it)
        })
    }

    private fun checkTutorialWatch() {
        if (preferences.readBoolean(IS_TUTORIAL_WATCHED))
            _state.update { it.copy(isTutorialWatched = true) }
        else
            _state.update { it.copy(isTutorialWatched = false) }
    }

    private fun addCreditCard(creditCard: Card) {
        launch(success = {
            cardRepository.addCard(creditCard)
        },
            error = {
                Timber.d(it.message)
            })
    }

    private fun updateCreditCard(creditCard: Card) {
        launch(success = {
            cardRepository.updateCard(creditCard)
        },
            error = {
                Timber.tag("MainViewModel").d(it)
            })
    }

    private fun onCardRemove(creditCard: Card) {
        launch(success = {
            cardRepository.deleteCard(creditCard)
        },
            error = {
                Timber.d(it.message)
            })
    }

    private fun getCreditCards() {
        launch {
            val cardList = cardRepository.getAllCards()
            cardList
                .stateIn(viewModelScope)
                .collect { cardListModel ->
                    cardLiveData.postValue(cardListModel)
                }
        }
    }

    private fun getPassbookList() {
        val passesList = passFileIO.getAllSaved()
        passesList.forEach { passItem ->
            filterCard(passItem)
        }

        groupedPassesLiveData.postValue(passGroup)
    }

    private fun filterCard(pass: PassFile) {
        val passType = PassType.fromString(getCardTypeString(pass))
        val uniqueId = (pass.pass.teamIdentifier ?: "") + (pass.pass.passTypeIdentifier
            ?: "") + getCardTypeString(pass)

        val existingGroup = passGroup.find { it.uniqueId == uniqueId }

        if (existingGroup != null) {
            val existingPass =
                existingGroup.passList.find { it.pass.serialNumber == pass.pass.serialNumber }
            if (existingPass == null) {
                existingGroup.passList.add(pass)
            }
        } else {
            val newPassGroup = PassGroup(
                uniqueId = uniqueId,
                passType = passType,
                passList = mutableListOf(pass)
            )
            passGroup.add(newPassGroup)
        }
    }


    private fun addScannedPassbook(url: String) {
        launch(success = {
            passFileIO.saveFromUrl(
                url = buildUrlWithQueryParameter(url),
                onSuccess = { passFile ->
                    getPassbookList()
                },
                onError = {

                }
            )
        }, error = {
            Timber.d(it)
        })
    }

    //todo: url fetch from firebase
    private fun onTutorialButtonClick() {
        preferences.writeBoolean(IS_TUTORIAL_WATCHED, true)
        openUrl("https://youtu.be/Fe2dQyYJVXE?si=3sMlgHW8M87LumPu")
        _state.update { it.copy(isTutorialWatched = true) }
    }

    private fun onInfoButtonClick() {
        openUrl("https://walletcards.io/?utm_source=inapp&utm_medium=info")
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}