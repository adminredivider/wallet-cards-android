package io.walletcards.mobilecards.presentation.detail_passbook

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.walletcards.mobilecards.R
import io.walletcards.mobilecards.databinding.ItemBoardingVpBinding
import io.walletcards.mobilecards.databinding.ItemCouponVpBinding
import io.walletcards.mobilecards.databinding.ItemGenericVpBinding
import io.walletcards.mobilecards.databinding.ItemStorecardVpBinding
import io.walletcards.mobilecards.databinding.ItemTicketVpBinding
import io.walletcards.mobilecards.domain.model.PassGroup
import io.walletcards.mobilecards.domain.model.PassFile
import io.walletcards.mobilecards.domain.model.PassType
import io.walletcards.mobilecards.presentation.util.bindFields
import io.walletcards.mobilecards.presentation.util.generateBarcode
import io.walletcards.mobilecards.presentation.util.getBarcodeHeight
import io.walletcards.mobilecards.presentation.util.getBarcodeType
import io.walletcards.mobilecards.presentation.util.getBarcodeWidth
import io.walletcards.mobilecards.presentation.util.parseRgbColor

class PassBookVMAdapter(
    private val action: (uniqueId: String, adapterPos: Int) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<PassGroup>() {
        override fun areItemsTheSame(
            oldItem: PassGroup,
            newItem: PassGroup,
        ): Boolean {
            return oldItem.uniqueId == newItem.uniqueId && oldItem.passList.size == newItem.passList.size
        }

        override fun areContentsTheSame(
            oldItem: PassGroup,
            newItem: PassGroup,
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return differ.currentList.sumOf {
            minOf(it.passList.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var currentPosition = position
        for (group in differ.currentList) {
            val passCount = minOf(group.passList.size)
            if (currentPosition < passCount) {
                return when (group.passType) {
                    PassType.BOARDING -> 0
                    PassType.TICKET -> 1
                    PassType.COUPON -> 2
                    PassType.GENERIC -> 3
                    PassType.STORECARD -> 4
                }
            }
            currentPosition -= passCount
        }
        throw IllegalStateException("Invalid position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> BoardingViewHolder(
                ItemBoardingVpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            1 -> TicketViewHolder(
                ItemTicketVpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            2 -> CouponViewHolder(
                ItemCouponVpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            3 -> GenericViewHolder(
                ItemGenericVpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            4 -> StoreCardViewHolder(
                ItemStorecardVpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currentPosition = position
        for (group in differ.currentList) {
            val passCount = minOf(group.passList.size)
            if (currentPosition < passCount) {
                group.passList.reverse()
                val pass = group.passList[group.passList.size - passCount + currentPosition]
                when (holder) {
                    is BoardingViewHolder -> {
                        holder.bindBoardingPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "", position)
                        }
                    }

                    is TicketViewHolder -> {
                        holder.bindTicketPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "", position)
                        }
                    }
                    is CouponViewHolder -> {
                        holder.bindCouponPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "", position)
                        }
                    }
                    is GenericViewHolder -> {
                        holder.bindGenericPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "", position)
                        }
                    }
                    is StoreCardViewHolder -> {
                        holder.bindStoreCardPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "", position)
                        }
                    }
                }
                return
            }
            currentPosition -= passCount
        }
        throw IllegalStateException("Invalid position $position")
    }

    inner class BoardingViewHolder(private val binding: ItemBoardingVpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindBoardingPass(holder: BoardingViewHolder, pass: PassFile?) {
            pass ?: return

            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()
            binding.passSecondaryFields.removeAllViews()

            val backgroundColor =
                Color.parseColor(parseRgbColor(pass.pass.backgroundColor ?: "rgb(0,0,0)"))
            val labelColor = Color.parseColor(parseRgbColor(pass.pass.labelColor ?: "rgb(0,0,0)"))
            val valueColor =
                Color.parseColor(parseRgbColor(pass.pass.foregroundColor ?: "rgb(0,0,0)"))

            binding.ticketView.setBackgroundColor(backgroundColor)
            binding.passLogoText.setTextColor(valueColor)
            binding.passLogoText.text = pass.pass.logoText
            Glide.with(binding.root.context)
                .load(pass.logo?.image3x ?: pass.logo?.image2x ?: pass.logo?.image)
                .into(binding.passLogo)
            pass.pass.boardingPass?.headerFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passHeaderFields,
                    holder.itemView.rootView.context
                )
            }

            val iconResId = when (pass.pass.boardingPass?.transitType) {
                "PKTransitTypeAir" -> {
                    R.drawable.plane
                }

                "PKTransitTypeGeneric" -> {
                    R.drawable.ic_arrow_forward
                }

                "PKTransitTypeBoat" -> {
                    R.drawable.ic_boat
                }

                "PKTransitTypeBus" -> {
                    R.drawable.ic_bus
                }

                "PKTransitTypeTrain" -> {
                    R.drawable.ic_train
                }

                else -> {
                    R.drawable.ic_arrow_forward
                }
            }
            pass.pass.boardingPass?.primaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    16f,
                    valueColor,
                    48f,
                    binding.passPrimaryFields,
                    holder.itemView.rootView.context,
                    iconResId
                )
            }

            pass.pass.boardingPass?.auxiliaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passAuxiliaryFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.boardingPass?.secondaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passSecondaryFields,
                    holder.itemView.rootView.context
                )
            }

            val barcodeType = getBarcodeType(pass.pass.barcode?.format)
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }
        }
    }

    inner class TicketViewHolder(private val binding: ItemTicketVpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTicketPass(holder: TicketViewHolder, pass: PassFile?) {
            pass ?: return

            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()
            binding.passSecondaryFields.removeAllViews()

            val backgroundColor =
                Color.parseColor(parseRgbColor(pass.pass.backgroundColor ?: "rgb(0,0,0)"))
            val labelColor = Color.parseColor(parseRgbColor(pass.pass.labelColor ?: "rgb(0,0,0)"))
            val valueColor =
                Color.parseColor(parseRgbColor(pass.pass.foregroundColor ?: "rgb(0,0,0)"))

            binding.ticketView.setBackgroundColor(backgroundColor)
            binding.passLogoText.setTextColor(valueColor)
            binding.passLogoText.text = pass.pass.logoText
            Glide.with(binding.root.context)
                .load(pass.logo?.image3x ?: pass.logo?.image2x ?: pass.logo?.image)
                .into(binding.passLogo)

            Glide.with(binding.root.context)
                .load(pass.thumbnail?.image3x ?: pass.thumbnail?.image2x ?: pass.thumbnail?.image)
                .into(binding.passThumbnail)

            pass.pass.eventTicket?.headerFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passHeaderFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.eventTicket?.primaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    16f,
                    valueColor,
                    28f,
                    binding.passPrimaryFields,
                    holder.itemView.rootView.context,
                )
            }

            pass.pass.eventTicket?.auxiliaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passAuxiliaryFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.eventTicket?.secondaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passSecondaryFields,
                    holder.itemView.rootView.context
                )
            }

            val barcodeType = getBarcodeType(pass.pass.barcode?.format)
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }
        }
    }

    inner class CouponViewHolder(private val binding: ItemCouponVpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindCouponPass(holder: CouponViewHolder, pass: PassFile?) {
            pass ?: return

            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()

            val backgroundColor =
                Color.parseColor(parseRgbColor(pass.pass.backgroundColor ?: "rgb(0,0,0)"))
            val labelColor = Color.parseColor(parseRgbColor(pass.pass.labelColor ?: "rgb(0,0,0)"))
            val valueColor =
                Color.parseColor(parseRgbColor(pass.pass.foregroundColor ?: "rgb(0,0,0)"))

            binding.ticketView.setBackgroundColor(backgroundColor)
            binding.passLogoText.setTextColor(valueColor)
            Glide.with(binding.root.context)
                .load(pass.logo?.image3x ?: pass.logo?.image2x ?: pass.logo?.image)
                .into(binding.passLogo)

            Glide.with(binding.root.context)
                .load(pass.strip?.image3x ?: pass.strip?.image2x ?: pass.strip?.image)
                .centerInside()
                .into(binding.passStrip)

            pass.pass.coupon?.headerFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passHeaderFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.coupon?.primaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    22f,
                    valueColor,
                    72f,
                    binding.passPrimaryFields,
                    holder.itemView.rootView.context
                )
            }

            val combinedFields = (pass.pass.coupon?.auxiliaryFields ?: emptyList()) +
                    (pass.pass.coupon?.secondaryFields ?: emptyList())

            bindFields(
                combinedFields,
                labelColor,
                14f,
                valueColor,
                20f,
                binding.passAuxiliaryFields,
                holder.itemView.rootView.context
            )

            holder.itemView.findViewById<ImageView>(R.id.pass_logo)?.let { logoImageView ->

            }

            val barcodeType = getBarcodeType(pass.pass.barcode?.format)
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }
        }
    }

    inner class GenericViewHolder(private val binding: ItemGenericVpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindGenericPass(holder: GenericViewHolder, pass: PassFile?) {
            pass ?: return

            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()
            binding.passSecondaryFields.removeAllViews()

            val backgroundColor =
                Color.parseColor(parseRgbColor(pass.pass.backgroundColor ?: "rgb(0,0,0)"))
            val labelColor = Color.parseColor(parseRgbColor(pass.pass.labelColor ?: "rgb(0,0,0)"))
            val valueColor =
                Color.parseColor(parseRgbColor(pass.pass.foregroundColor ?: "rgb(0,0,0)"))

            binding.ticketView.setBackgroundColor(backgroundColor)
            binding.passLogoText.setTextColor(valueColor)
            binding.passLogoText.text = pass.pass.logoText
            Glide.with(binding.root.context)
                .load(pass.logo?.image3x ?: pass.logo?.image2x ?: pass.logo?.image)
                .into(binding.passLogo)

            Glide.with(binding.root.context)
                .load(pass.thumbnail?.image3x ?: pass.thumbnail?.image2x ?: pass.thumbnail?.image)
                .into(binding.passThumbnail)

            pass.pass.generic?.headerFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passHeaderFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.generic?.primaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    16f,
                    valueColor,
                    28f,
                    binding.passPrimaryFields,
                    holder.itemView.rootView.context,
                )
            }

            pass.pass.generic?.auxiliaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passAuxiliaryFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.generic?.secondaryFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passSecondaryFields,
                    holder.itemView.rootView.context
                )
            }

            val barcodeType = getBarcodeType(pass.pass.barcode?.format)
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }
        }
    }

    inner class StoreCardViewHolder(private val binding: ItemStorecardVpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindStoreCardPass(holder: StoreCardViewHolder, pass: PassFile?) {
            pass ?: return
            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()
            binding.passSecondaryFields.removeAllViews()

            val backgroundColor =
                Color.parseColor(parseRgbColor(pass.pass.backgroundColor ?: "rgb(0,0,0)"))
            val labelColor = Color.parseColor(parseRgbColor(pass.pass.labelColor ?: "rgb(0,0,0)"))
            val valueColor =
                Color.parseColor(parseRgbColor(pass.pass.foregroundColor ?: "rgb(0,0,0)"))

            binding.ticketView.setBackgroundColor(backgroundColor)
            binding.passLogoText.setTextColor(valueColor)
            binding.passLogoText.text = pass.pass.logoText
            Glide.with(binding.root.context)
                .load(pass.logo?.image3x ?: pass.logo?.image2x ?: pass.logo?.image)
                .into(binding.passLogo)

            Glide.with(binding.root.context)
                .load(pass.strip?.image3x ?: pass.strip?.image2x ?: pass.strip?.image)
                .into(binding.passStrip)

            pass.pass.storeCard?.headerFields?.let { fields ->
                bindFields(
                    fields,
                    labelColor,
                    14f,
                    valueColor,
                    18f,
                    binding.passHeaderFields,
                    holder.itemView.rootView.context
                )
            }

            pass.pass.storeCard?.primaryFields?.let { fields ->
                val formattedFields = fields.map { field ->
                    val currencySymbol = when (field.currencyCode) {
                        "USD" -> "$"
                        "EUR" -> "€"
                        else -> ""
                    }
                    val value = field.value ?: ""
                    val decimalPart =
                        if (field.currencyCode != null && !value.contains(".")) ",00" else ""

                    field.copy(value = "$currencySymbol$value$decimalPart")
                }

                bindFields(
                    formattedFields,
                    labelColor,
                    16f,
                    valueColor,
                    58f,
                    binding.passPrimaryFields,
                    holder.itemView.rootView.context
                )
            }

            val combinedFields = (pass.pass.storeCard?.auxiliaryFields ?: emptyList()) +
                    (pass.pass.storeCard?.secondaryFields ?: emptyList())

            bindFields(
                combinedFields,
                labelColor,
                16f,
                valueColor,
                26f,
                binding.passAuxiliaryFields,
                holder.itemView.rootView.context
            )

            val barcodeType = getBarcodeType(pass.pass.barcode?.format)
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }
        }
    }
}

