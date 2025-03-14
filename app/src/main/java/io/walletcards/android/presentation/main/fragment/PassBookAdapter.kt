package io.walletcards.android.presentation.main.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import io.walletcards.android.R
import io.walletcards.android.databinding.ItemBoardingBinding
import io.walletcards.android.databinding.ItemCouponBinding
import io.walletcards.android.databinding.ItemGenericBinding
import io.walletcards.android.databinding.ItemStorecardBinding
import io.walletcards.android.databinding.ItemTicketBinding
import io.walletcards.android.domain.model.PassFile
import io.walletcards.android.domain.model.PassGroup
import io.walletcards.android.domain.model.PassType
import io.walletcards.android.presentation.util.bindFields
import io.walletcards.android.presentation.util.generateBarcode
import io.walletcards.android.presentation.util.getBarcodeHeight
import io.walletcards.android.presentation.util.getBarcodeType
import io.walletcards.android.presentation.util.getBarcodeWidth
import io.walletcards.android.presentation.util.parseRgbColor
import timber.log.Timber

class PassBookAdapter(
    private val action: (uniqueId: String) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<PassGroup>() {
        override fun areItemsTheSame(
            oldItem: PassGroup,
            newItem: PassGroup,
        ): Boolean {
            return oldItem.passList.size == newItem.passList.size && oldItem.passList.first().pass == newItem.passList.first().pass
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
            minOf(it.passList.size, 3)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var currentPosition = position
        for (group in differ.currentList) {
            val passCount = minOf(group.passList.size, 3)
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
                ItemBoardingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            1 -> TicketViewHolder(
                ItemTicketBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            2 -> CouponViewHolder(
                ItemCouponBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            3 -> GenericViewHolder(
                ItemGenericBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            4 -> StoreCardViewHolder(
                ItemStorecardBinding.inflate(
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
            val passCount = minOf(group.passList.size, 3)
            if (currentPosition < passCount) {
                val pass = group.passList[group.passList.size - passCount + currentPosition]
                when (holder) {
                    is BoardingViewHolder -> {
                        holder.bindBoardingPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "")
                        }
                    }

                    is TicketViewHolder -> {
                        holder.bindTicketPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "")
                        }
                    }

                    is CouponViewHolder -> {
                        holder.bindCouponPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "")
                        }
                    }

                    is GenericViewHolder -> {
                        holder.bindGenericPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "")
                        }
                    }

                    is StoreCardViewHolder -> {
                        holder.bindStoreCardPass(holder, pass)
                        holder.itemView.setOnClickListener {
                            action.invoke(group.uniqueId ?: "")
                        }
                    }
                }
                return
            }
            currentPosition -= passCount
        }
        throw IllegalStateException("Invalid position $position")
    }

    inner class BoardingViewHolder(private val binding: ItemBoardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindBoardingPass(holder: BoardingViewHolder, pass: PassFile?) {
            pass ?: return
            binding.passHeaderFields.removeAllViews()
            binding.passPrimaryFields.removeAllViews()
            binding.passAuxiliaryFields.removeAllViews()
            binding.passSecondaryFields.removeAllViews()

            Timber.tag("BoardingViewHolder").e("pass: $pass")
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
            val primaryField =
                pass.pass.boardingPass?.headerFields?.toMutableList()  // Convert to MutableList
            val field = pass.pass.boardingPass?.auxiliaryFields?.find { it.key == "date" }

            if (field != null) {
                primaryField?.add(0, field)  // Add the extracted field
                pass.pass.boardingPass.headerFields = primaryField  // Assign the modified list back
            }

            primaryField?.let { fields ->
                Timber.tag("BoardingViewHolder").e("headerFields: $fields")
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

            Timber.tag("BoardingViewHolder")
                .e("transitType: ${pass.pass.boardingPass?.transitType}")
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
                Timber.tag("BoardingViewHolder").e("primaryFields: $fields")
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


            val auxiliaryFields = pass.pass.boardingPass?.auxiliaryFields
                ?.filter { it.key != "date" } // Filter out "date" if needed
                ?.sortedBy {
                    when (it.key) {
                        "flightNumber" -> 0  // Move Flight Number to the first position
                        "boardingTime" -> 2  // Move Depart to the last position
                        else -> 1  // Keep everything else in between
                    }
                }

            auxiliaryFields?.let { fields ->
                Timber.tag("BoardingViewHolder").e("auxiliaryFields: $fields")
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
                Timber.tag("BoardingViewHolder").e("secondaryFields: $fields")
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
            Timber.tag("BoardingViewHolder").e("barcodeType: $barcodeType")
            val width = getBarcodeWidth(pass.pass.barcode?.format, binding.root.context)
            val height = getBarcodeHeight(pass.pass.barcode?.format, binding.root.context)

            val barcodeBitmap =
                generateBarcode(pass.pass.barcode?.message ?: "", barcodeType, width, height)
            if (barcodeBitmap != null) {
                binding.qrCode.setImageBitmap(barcodeBitmap)
            }

//            generateBarcodeWithPadding(
//                width = width,
//                height = height,
//                context = holder.itemView.rootView.context,
//                format = barcodeType,
//                data = pass.pass.barcode?.message ?: "",
//                paddingDp = 20,
//            )
        }
    }

    inner class TicketViewHolder(private val binding: ItemTicketBinding) :
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
                Timber.tag("headerFields").e("bindTicketPass: $fields")
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
                Timber.tag("primaryFields").e("bindTicketPass: $fields")
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
                Timber.tag("primaryFields").e("bindTicketPass: $fields")

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

    inner class CouponViewHolder(private val binding: ItemCouponBinding) :
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
            binding.passLogoText.text = pass.pass.logoText
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

    inner class GenericViewHolder(private val binding: ItemGenericBinding) :
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
                Timber.tag("GenericViewHolder").e("bindGenericPass: $fields")
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

    inner class StoreCardViewHolder(private val binding: ItemStorecardBinding) :
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


    fun generateBarcodeWithPadding(
        data: String,
        format: BarcodeFormat,
        width: Int,
        height: Int,
        paddingDp: Int,
        context: Context
    ): Bitmap? {
        val barcodeBitmap = generateBarcode(data, format, width, height) ?: return null
        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, paddingDp.toFloat(),
            context.resources.displayMetrics
        ).toInt()

        val paddedWidth = width + 2 * paddingPx
        val paddedHeight = height + 2 * paddingPx

        val paddedBitmap = Bitmap.createBitmap(paddedWidth, paddedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        canvas.drawColor(Color.WHITE) // Optional: Background color

        val left = paddingPx.toFloat()
        val top = paddingPx.toFloat()
        canvas.drawBitmap(barcodeBitmap, left, top, null)
        return paddedBitmap
    }

}

