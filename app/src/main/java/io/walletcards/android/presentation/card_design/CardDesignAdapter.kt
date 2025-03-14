package io.walletcards.android.presentation.card_design

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.walletcards.android.R
import io.walletcards.android.databinding.CardDesignLayoutBinding
import io.walletcards.android.domain.model.CardDesignModel

class CardDesignAdapter(
    private val context: Context,
    private val cardDesigns: List<CardDesignModel>,
    private var cardDesignClickListener: OnCardDesignClickListener
) : RecyclerView.Adapter<CardDesignAdapter.CardDesignViewHolder>() {
    private var selectedPosition: Int = RecyclerView.NO_POSITION
   // private var lastSelectedPosition: Int = -1

    inner class CardDesignViewHolder(private val binding: CardDesignLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cardDesign: CardDesignModel) {
            binding.ivCardDesign.setImageResource(cardDesign.thumbnailResId)
            binding.ivLock.isVisible = !cardDesign.isFree

            // Apply selection background

            if (selectedPosition == bindingAdapterPosition && cardDesign.isFree) {
                binding.designCard.strokeColor = ContextCompat.getColor(context, R.color.gradiant_end_color)
                binding.designCard.strokeWidth = 5
//                binding.designCard.background =
//                    ContextCompat.getDrawable(context, R.drawable.selected_card_design)
            } else {
                binding.designCard.strokeColor =
                    ContextCompat.getColor(context, android.R.color.transparent)
                binding.designCard.strokeWidth = 0
            }

            binding.root.setOnClickListener {
            //    lastSelectedPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
             //   notifyItemChanged(lastSelectedPosition)
                notifyDataSetChanged()
                cardDesignClickListener.onCardDesignClick(cardDesign)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignViewHolder {
        val binding =
            CardDesignLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardDesignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardDesignViewHolder, position: Int) {
        holder.bind(cardDesigns[position])
    }

    override fun getItemCount(): Int = cardDesigns.size
}

interface OnCardDesignClickListener {
    fun onCardDesignClick(cardDesign: CardDesignModel)
}

