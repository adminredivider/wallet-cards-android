package io.walletcards.android.presentation.detail_card

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.walletcards.android.databinding.FragmentCreditDetailBinding
import io.walletcards.android.presentation.main.MainScreenAction
import org.koin.androidx.viewmodel.ext.android.viewModel
import io.walletcards.android.R
import io.walletcards.android.databinding.DeleteConfirmationDialogLayoutBinding
import io.walletcards.android.databinding.PopupLayoutBinding
import io.walletcards.android.domain.model.Card
import io.walletcards.android.presentation.main.MainViewModel
import timber.log.Timber

class DetailCardFragment : Fragment(), OnUpdateCardClickListener {
    private var _binding: FragmentCreditDetailBinding? = null
    private val binding get() = _binding!!
    private val cardDetailAdapter = CardDetailAdapter(this)
    private var itemPosition = -1
    private val mainViewModel: MainViewModel by viewModel()
    private var firstPositionCardId :Int? = null

    companion object {
        private const val MAX_BRIGHTNESS = 1F
    }

    private var previousBrightness = MAX_BRIGHTNESS
    private var cardDesignId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreditDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.let {
            val attributes = it.window.attributes
            previousBrightness = attributes.screenBrightness
            attributes.screenBrightness = MAX_BRIGHTNESS
            it.window.attributes = attributes
        }

        firstPositionCardId = arguments?.getString("cardId")?.toIntOrNull()
        Timber.tag("firstPositionCardId").e("$firstPositionCardId")
        mainViewModel.action(MainScreenAction.LoadCardData)
        setupFieldAction()
        initViewPager()
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("cardId")
            ?.observe(viewLifecycleOwner) { cardId ->
                // Handle the received cardId
                if (cardId != null) {
                    cardDesignId = cardId
                  //  updateCardDesignUi(cardId)
                }

                Timber.tag("FragmentA").d("Received cardId: $cardId")
                navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("cardId")
            }

        setupObservers()
    }

    private fun updateCardDesignUi(newDesignId: Int) {
        val updatedList = cardDetailAdapter.differ.currentList.toMutableList()
        val updatedCard = updatedList[itemPosition].copy(dummyCardId = newDesignId)
        updatedList[itemPosition] = updatedCard
        cardDetailAdapter.submitList(updatedList)
    }

    private fun setupFieldAction() {
//        binding.btnDone.setOnClickListener {
//            findNavController().navigateUp()
//        }

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.actionMore.setOnClickListener {
            openMoreActionDialog(requireActivity())
        }


//        binding.btnRemoveCard.setOnClickListener {
//            mainViewModel.action(
//                MainScreenAction.RemoveCard(
//                    cardDetailAdapter.differ.currentList[binding.viewPager.currentItem]
//                )
//            )
//            findNavController().navigateUp()
//        }

//        binding.btnShareCard.setOnClickListener {
//            val currentCreditCard =
//                cardDetailAdapter.differ.currentList[binding.viewPager.currentItem]
//            val dataForSharing = "${currentCreditCard.cardHolderName}\n" +
//                    "${currentCreditCard.cardNumber}\n${currentCreditCard.cardExpiry} - ${currentCreditCard.cvv}"
//            shareText(dataForSharing)
//        }
    }

    private fun initViewPager() {
        binding.viewPager.adapter = cardDetailAdapter
        binding.springDotsIndicator.attachTo(binding.viewPager)
    }

    private fun setupObservers() {
        mainViewModel.cardLiveData.observe(viewLifecycleOwner) { cardList ->
            Timber.tag("cardLiveData")
            val updateList = cardList.toMutableList()
            // Move the matching cardId to the first position
            firstPositionCardId?.let { id ->
                val matchingIndex = updateList.indexOfFirst { card -> card.id == id }
                if (matchingIndex != -1) {
                    val matchingCard = updateList.removeAt(matchingIndex)
                    updateList.add(0, matchingCard) // Move to the first position
                }
            }

            if (cardDesignId != null) {
                val updatedCard = updateList[itemPosition].copy(dummyCardId = cardDesignId)
                updateList[itemPosition] = updatedCard
            }

            cardDetailAdapter.submitList(updateList)
        }
    }


    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        requireContext().startActivity(Intent.createChooser(intent, "Share via"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.let {
            val attributes = it.window.attributes
            attributes.screenBrightness = previousBrightness
            it.window.attributes = attributes
        }
        _binding = null
    }

    fun openMoreActionDialog(context: Context) {
        val popupLayoutBinding = PopupLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogTheme)
            .setView(popupLayoutBinding.root)
            .setCancelable(true) // Prevent dismissing by clicking outside
            .create()

        popupLayoutBinding.deleteTxtBtn.setOnClickListener {
            deleteConfirmationDialog(context)
            dialog.dismiss()
        }

        popupLayoutBinding.shareTxtBtn.setOnClickListener {
            val currentCreditCard =
                cardDetailAdapter.differ.currentList[binding.viewPager.currentItem]
            val dataForSharing = "${currentCreditCard.cardHolderName}\n" +
                    "${currentCreditCard.cardNumber}\n${currentCreditCard.cardExpiry} - ${currentCreditCard.cvv}"
            shareText(dataForSharing)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun deleteConfirmationDialog(context: Context) {
        val deleteConfirmationBinding =
            DeleteConfirmationDialogLayoutBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogTheme)
            .setView(deleteConfirmationBinding.root)
            .setCancelable(false) // Prevent dismissing by clicking outside
            .create()

//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        deleteConfirmationBinding.deleteYes.setOnClickListener {
            mainViewModel.action(
                MainScreenAction.RemoveCard(
                    cardDetailAdapter.differ.currentList[binding.viewPager.currentItem]
                )
            )
            findNavController().navigateUp()
            dialog.dismiss()
        }

        deleteConfirmationBinding.deleteNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onUpdate(card: Card) {
//        val card1 =
//            card.copy(cardDesignBackgroundId = if (cardDesignId != null) cardDesignId else card.cardDesignBackgroundId)
        Timber.tag("onUpdateCard").e("$card")
        mainViewModel.action(
            MainScreenAction.UpdateCreditCardAction(card)
        )
        findNavController().navigateUp()
    }

    override fun onUpdateCardDesign(bindingAdapterPosition: Int, currentCardId: Int) {
        itemPosition = bindingAdapterPosition
        findNavController().navigate(R.id.action_detailCardFragment_to_cardDesignFragment)
    }


}


