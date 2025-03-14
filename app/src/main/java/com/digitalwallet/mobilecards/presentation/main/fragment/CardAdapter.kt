package com.digitalwallet.mobilecards.presentation.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalwallet.mobilecards.R
import com.digitalwallet.mobilecards.databinding.CreditCardItemBinding
import com.digitalwallet.mobilecards.domain.model.Card
import com.digitalwallet.mobilecards.domain.model.CardType
import com.digitalwallet.mobilecards.presentation.util.maskCardNumber

class CardAdapter(private val onMainCardClickListener: OnMainCardClickListener) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    inner class CardViewHolder(private val binding: CreditCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            with(binding) {
                tvCardHolder.text = card.bankName
                tvCardNumber.text = maskCardNumber(card.cardNumber)
                card.cardDesignBackgroundId?.let { backgroundImage.setImageResource(it) }
               // cardView.setCardBackgroundColor(Color.parseColor(card.cardColor))

                when (card.cardType) {
                    CardType.AMERICAN_EXPRESS -> cardLogo.setImageResource(R.drawable.paymethod_amex)
                    CardType.DINERS_CLUB -> cardLogo.setImageResource(R.drawable.diners_club)
                    CardType.DISCOVER -> cardLogo.setImageResource(R.drawable.paymethod_discover)
                    CardType.JCB -> cardLogo.setImageResource(R.drawable.paymethod_jcb)
                    CardType.MASTER_CARD -> cardLogo.setImageResource(R.drawable.paymethod_mastercard)
                    CardType.MAESTRO -> cardLogo.setImageResource(R.drawable.maestro)
                    CardType.RUPAY -> cardLogo.setImageResource(R.drawable.rupay)
                    CardType.VISA -> cardLogo.setImageResource(R.drawable.paymethod_visa)
                    CardType.ELO -> cardLogo.setImageResource(R.drawable.elo)
                    CardType.OTHER -> cardLogo.setImageResource(R.drawable.paymethod_unidentified)
                    CardType.UNIONPAY -> {
                        cardLogo.setImageResource(R.drawable.paymethod_unionpay)
                    }
                }

                binding.cardView.setOnClickListener {
                    onMainCardClickListener.onCardClick(card.id)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            CreditCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(
            oldItem: Card,
            newItem: Card,
        ): Boolean {
            return oldItem.cardNumber == newItem.cardNumber
        }

        override fun areContentsTheSame(
            oldItem: Card,
            newItem: Card,
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}

interface OnMainCardClickListener{
    fun onCardClick(cardId: Int)
}