package io.walletcards.android.presentation.card_design

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.walletcards.android.R
import io.walletcards.android.databinding.FragmentCardDesignBinding
import io.walletcards.android.domain.model.Card
import io.walletcards.android.domain.model.CardDesignModel
import io.walletcards.android.presentation.main.MainScreenAction
import io.walletcards.android.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.getValue

class CardDesignFragment : Fragment(), OnCardDesignClickListener {
    private lateinit var binding: FragmentCardDesignBinding
    private val mainViewModel: MainViewModel by viewModel()
    private var selectCardDesignId: Int? = null
    private var card :Card? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCardDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.designToolbar.apply {
            actionMore.visibility = View.GONE
            toolbarTitle.text = getString(R.string.select_card_design)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.recyclerView.adapter =
            CardDesignAdapter(requireContext(), CardDesignList.returnCardDesignList(), this)

       // getCardByIdFromRoomDb()
    }

    private fun getCardByIdFromRoomDb() {
        mainViewModel.getCreditCardById.observe(viewLifecycleOwner) {
            Timber.tag("CardDesignFragment").d("getCardByIdFromRoomDb: $it")
            if (it != null) {
                card = it
            }
        }
    }

    override fun onCardDesignClick(cardDesign: CardDesignModel) {
        if (!cardDesign.isFree) {
            findNavController().navigate(R.id.action_cardDesignFragment_to_paywell)
            selectCardDesignId = null
        } else {
            selectCardDesignId = cardDesign.thumbnailResId
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "cardId",
                selectCardDesignId
            )

            Timber.tag("CardDesignFragment").d("oldCardBackgroundId: ${card?.cardDesignBackgroundId}")
            card = card?.copy(cardDesignBackgroundId = selectCardDesignId)
            Timber.tag("CardDesignFragment").d("newCardBackgroundId: ${card?.cardDesignBackgroundId}")
            // here we can update background id when it comes from detail fragment
            if (card!=null){
               // mainViewModel.action(MainScreenAction.UpdateCreditCardAction(card!!))
            }

            findNavController().navigateUp()
        }
    }

}