package com.digitalwallet.mobilecards.presentation.detail_card

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.digitalwallet.mobilecards.R
import com.digitalwallet.mobilecards.databinding.ItemCardDetailBinding
import com.digitalwallet.mobilecards.domain.model.Card
import com.digitalwallet.mobilecards.domain.model.CardType
import com.digitalwallet.mobilecards.presentation.util.detectCardType
import com.digitalwallet.mobilecards.presentation.util.maskCardNumber
import timber.log.Timber

class CardDetailAdapter(private val onUpdateCardClickListener: OnUpdateCardClickListener) :
    RecyclerView.Adapter<CardDetailAdapter.CardDetailViewHolder>() {
    private var isCardEditable = false
    private var cardDesignId: Int? = null
    private var cardList = arrayListOf<Card>()

    inner class CardDetailViewHolder(private val binding: ItemCardDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val editCardButton: MaterialButton = binding.contentLayout.editCard as MaterialButton
        var isCvvVisible = false

        init {
            binding.apply {
                contentLayout.apply {
                    editCard.visibility = View.VISIBLE
                    if (!isCardEditable) {
                        cardNameCopyToClipboard.visibility = View.VISIBLE
                        editCardholderNameCopyToClipboard.visibility = View.VISIBLE
                        cardNumberCopyToClipboard.visibility = View.VISIBLE
                        tvValidCvvLayout.eyeToggle.visibility = View.VISIBLE
                        edtCardName.isEnabled = false
                        edtCardNumber.isEnabled = false
                        tvValidCvvLayout.edtDate.isEnabled = false
                        tvValidCvvLayout.edtSecurityCode.isEnabled = false
                        edtCardholderName.isEnabled = false
                        updateCardData.visibility = View.GONE
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(card: Card) {
            with(binding) {
//                tvCardName.text = card.bankName
//                tvCardNumber.text = card.cardNumber
//                //  tvCardDate.text = "Exp. Date ${card.cardExpiry}"
//                tvCardPlaceholder.text = card.cardHolderName
//                // cardView.setCardBackgroundColor(Color.parseColor(card.cardColor))
                //brush.visibility = View.GONE
                brush.visibility = if (isCardEditable) View.VISIBLE else View.GONE
                brush.setOnClickListener {
                    onUpdateCardClickListener.onUpdateCardDesign(bindingAdapterPosition, card.id)
                }

                Timber.tag("cardDesignSS").e("$card")
                cardDesignImage.setImageResource(card.dummyCardId ?: card.cardDesignBackgroundId!!)
                contentLayout.apply {
                    tvValidCvvLayout.edtSecurityCode.inputType =
                        if (isCardEditable) {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }

                    edtCardName.setText(card.bankName)
                    edtCardholderName.setText(card.cardHolderName.toString())
                    edtCardNumber.setText(card.cardNumber)
                    tvValidCvvLayout.edtSecurityCode.setText(card.cvv)
                    tvValidCvvLayout.edtDate.setText(card.cardExpiry)
                    tvCardName.text = card.bankName
                    tvCardNumber.text = maskCardNumber(card.cardNumber)

                    val fields = listOf(
                        edtCardName,
                        edtCardNumber,
                        tvValidCvvLayout.edtDate,
                        tvValidCvvLayout.edtSecurityCode,
                        edtCardholderName
                    )

                    fields.forEach { it.isEnabled = isCardEditable }
                    editCard.visibility =
                        if (!isCardEditable) View.VISIBLE else View.GONE
                    cardNameCopyToClipboard.visibility =
                        if (isCardEditable) View.GONE else View.VISIBLE
                    editCardholderNameCopyToClipboard.visibility =
                        if (isCardEditable) View.GONE else View.VISIBLE
                    cardNumberCopyToClipboard.visibility =
                        if (isCardEditable) View.GONE else View.VISIBLE
                    tvValidCvvLayout.eyeToggle.visibility =
                        if (isCardEditable) View.GONE else View.VISIBLE
                    updateCardData.visibility = if (isCardEditable) View.VISIBLE else View.GONE
                }

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
                    CardType.UNIONPAY -> cardLogo.setImageResource(R.drawable.paymethod_unionpay)
                    CardType.OTHER -> cardLogo.setImageResource(R.drawable.paymethod_unidentified)
                }
//                val spannableText = SpannableString(cardNumberDefaultText)
//                spannableText.setSpan(UnderlineSpan(), 0, cardNumberDefaultText.length, 0)
//
//                edtCardNumber.setText(spannableText)
//                edtDate.setText(card.cardExpiry)
//                edtSecurityCode.setText(card.cvv)
//                edtCardholderName.setText(card.cardHolderName)

//                edtCardNumber.setOnTouchListener { v, event ->
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        val clipboard =
//                            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                        val clip = ClipData.newPlainText("card number", card.cardNumber)
//                        clipboard.setPrimaryClip(clip)
//
//                        Toast.makeText(
//                            root.context,
//                            "Card number copied to clipboard",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    true
//                }
//
//                layoutCardNumber.setOnClickListener {
//                    val clipboard =
//                        root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val clip = ClipData.newPlainText("card number", card.cardNumber)
//                    clipboard.setPrimaryClip(clip)
//
//                    Toast.makeText(
//                        root.context,
//                        "Card number copied to clipboard",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }

                contentLayout.apply {
                    cardNameCopyToClipboard.setOnClickListener {
                        val clipboard =
                            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("card name", edtCardName.text.toString())
                        clipboard.setPrimaryClip(clip)

                        showSnackBar(binding.root, "card name copied to clipboard")
                    }

                    editCardholderNameCopyToClipboard.setOnClickListener {
                        val clipboard =
                            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "card holder name",
                            edtCardholderName.text.toString()
                        )
                        clipboard.setPrimaryClip(clip)
                        showSnackBar(binding.root, "card holder name copied to clipboard")
                    }

                    cardNumberCopyToClipboard.setOnClickListener {
                        val clipboard =
                            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip =
                            ClipData.newPlainText("card number", edtCardNumber.text.toString())
                        clipboard.setPrimaryClip(clip)

                        showSnackBar(binding.root, "card number copied to clipboard")
                    }

                    tvValidCvvLayout.eyeToggle.setOnClickListener {
                        isCvvVisible = !isCvvVisible
                        // Toggle CVV visibility
                        if (isCvvVisible) {
                            tvValidCvvLayout.edtSecurityCode.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            tvValidCvvLayout.eyeToggle.setImageResource(R.drawable.icon_visible)
                        } else {
                            tvValidCvvLayout.edtSecurityCode.inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            tvValidCvvLayout.eyeToggle.setImageResource(R.drawable.icons_hide)
                        }

                        tvValidCvvLayout.edtSecurityCode.setSelection(
                            tvValidCvvLayout.edtSecurityCode.text?.length ?: 0
                        )
                    }

                    editCardButton.setOnClickListener {
                        isCardEditable = !isCardEditable
                        notifyItemChanged(bindingAdapterPosition)
                    }

                    updateCardData.setOnClickListener {
                        val card = card.copy(
                            cardHolderName = edtCardholderName.text.toString(),
                            cardNumber = edtCardNumber.text.toString(),
                            cardExpiry = tvValidCvvLayout.edtDate.text.toString(),
                            cvv = tvValidCvvLayout.edtSecurityCode.text.toString(),
                            bankName = edtCardName.text.toString(),
                            cardType = detectCardType(edtCardNumber.text.toString()),
                            cardDesignBackgroundId = card.dummyCardId ?: card.cardDesignBackgroundId
                        )

                        Timber.tag("updateCardData").e("$card")
                        onUpdateCardClickListener.onUpdate(card)
                        isCardEditable = !isCardEditable
                        notifyItemChanged(bindingAdapterPosition)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDetailViewHolder {
        return CardDetailViewHolder(
            ItemCardDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CardDetailViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            val isSame = oldItem.cardDesignBackgroundId == newItem.cardDesignBackgroundId
            Timber.tag("areItemsTheSame").e("$isSame")
            return isSame // Unique ID for better tracking
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            val isContentSame = oldItem == newItem
            Timber.tag("areContentsTheSame").e("$isContentSame")
            return isContentSame
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
//
//    override fun getItemId(position: Int): Long {
//        return differ.currentList[position].id.toLong()
//    }
//
//
//    override fun getItemViewType(position: Int): Int {
//        return position
//    }


    fun showSnackBar(rootView: View, message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    fun updateSelectedCardDesign(cardDesignId: Int?) {
        this.cardDesignId = cardDesignId
        notifyDataSetChanged()
    }

    fun submitList(mList: List<Card>) {
        differ.submitList(mList)
        notifyDataSetChanged()
    }

}

interface OnUpdateCardClickListener {
    fun onUpdate(card: Card)
    fun onUpdateCardDesign(bindingAdapterPosition: Int, currentCardId: Int)
}