package io.walletcards.android.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.view.marginEnd
import com.google.android.flexbox.FlexboxLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.pdf417.PDF417Writer
import io.walletcards.android.R
import io.walletcards.android.domain.model.CardType
import io.walletcards.android.domain.model.Fields
import io.walletcards.android.domain.model.PassFile
import io.walletcards.android.domain.model.PassStructureDictionary
import timber.log.Timber
import java.util.Calendar

//fun detectCardType(cardNumber: String): CardType {
//    val sanitizedNumber = cardNumber.replace("\\s".toRegex(), "")
//
//    return when {
//        sanitizedNumber.startsWith("34") || sanitizedNumber.startsWith("37") -> CardType.AMERICAN_EXPRESS
//        sanitizedNumber.startsWith("36") -> CardType.DINERS_CLUB
//        sanitizedNumber.startsWith("6011") || sanitizedNumber.matches("^65[0-9]{2}".toRegex()) -> CardType.DISCOVER
//        sanitizedNumber.startsWith("35") -> CardType.JCB
//        sanitizedNumber.matches("^(5[1-5][0-9]{14})$".toRegex()) -> CardType.MASTER_CARD
//        sanitizedNumber.matches("^(5018|5020|5038|6304|6759|6761|6762|6763)".toRegex()) -> CardType.MAESTRO
//        sanitizedNumber.startsWith("60") || sanitizedNumber.startsWith("6521") || sanitizedNumber.startsWith(
//            "6522"
//        ) -> CardType.RUPAY
//
//        sanitizedNumber.matches("^4[0-9]{12}(?:[0-9]{3})?$".toRegex()) -> CardType.VISA
//        sanitizedNumber.matches("^50[0-9]{4}".toRegex()) -> CardType.ELO
//        else -> CardType.OTHER
//    }
//}


fun detectCardType(cardNumber: String): CardType {
    val sanitizedNumber = cardNumber.replace("\\s".toRegex(), "") // Remove spaces

    return when {
        sanitizedNumber.length in listOf(
            13,
            16,
            19
        ) && sanitizedNumber.startsWith("4") -> CardType.VISA

        sanitizedNumber.length == 16 && (sanitizedNumber.startsWith("5") || sanitizedNumber.startsWith(
            "2"
        )) -> CardType.MASTER_CARD

        sanitizedNumber.length == 15 && (sanitizedNumber.startsWith("34") || sanitizedNumber.startsWith(
            "37"
        )) -> CardType.AMERICAN_EXPRESS

        sanitizedNumber.length == 16 &&
                (sanitizedNumber.startsWith("6011") ||
                        sanitizedNumber.startsWith("65") ||
                        sanitizedNumber.substring(0, 3).toIntOrNull() in 644..649 ||
                        sanitizedNumber.substring(0, 6)
                            .toIntOrNull() in 622126..622925) -> CardType.DISCOVER

        sanitizedNumber.length in 16..19 && sanitizedNumber.startsWith("62") -> CardType.UNIONPAY
        sanitizedNumber.length in 16..19 && sanitizedNumber.substring(0, 4)
            .toIntOrNull() in 3528..3589 -> CardType.JCB

        else -> CardType.OTHER
    }
}


fun isValidExpiryDate(expiryDate: String): Boolean {
    try {
        val parts = expiryDate.split("/")
        val month = parts[0].toInt()
        val year = "20${parts[1]}".toInt()

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        return year > currentYear || (year == currentYear && month >= currentMonth)
    } catch (e: Exception) {
        return false
    }
}

fun maskCardNumber(cardNumber: String): String {
    val maskedPart = cardNumber.take(14).replace(Regex("\\d"), "*")
    val lastFourDigits = cardNumber.takeLast(4)

    return "$maskedPart $lastFourDigits"
}

fun dpToPx(dp: Int, context: Context): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

fun buildUrlWithQueryParameter(url: String): String {
    return if (url.endsWith(".pkpass")) {
        url
    } else {
        "$url&download=true"
    }
}

fun getCardTypeString(passFile: PassFile): String {
    return when {
        passFile.pass.eventTicket != null -> "TICKET"
        passFile.pass.boardingPass != null -> "BOARDING"
        passFile.pass.coupon != null -> "COUPON"
        passFile.pass.generic != null -> "GENERIC"
        passFile.pass.storeCard != null -> "STORECARD"
        else -> "GENERIC"
    }
}

fun bindFields(
    fields: List<Fields>,
    labelColor: Int,
    labelSize: Float,
    valueColor: Int,
    valueSize: Float,
    linearLayout: FlexboxLayout,
    context: Context,
    iconResId: Int? = null,
    showDivider: Boolean = false,
) {
    Timber.tag("bindFields").e("fields: $fields")
    linearLayout.removeAllViews()

    fields.forEachIndexed { index, field ->
        val fieldContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {


            }

        }

        val labelTextView = TextView(context).apply {
            text = field.label
            setTextColor(labelColor)
            textSize = labelSize
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (index!=fields.lastIndex){
                    marginEnd = dpToPx(16, context)
                }
            }

        }

        val valueTextView = TextView(context).apply {
            text = field.value
            setTextColor(valueColor)
            textSize = valueSize
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (index!=fields.lastIndex){
                    marginEnd = dpToPx(16, context)
                }
            }
            if (field.textAlignment == "PKTextAlignmentRight") {
                gravity = Gravity.END
            }

            val spannable = SpannableString(field.value)
            Linkify.addLinks(spannable, Linkify.WEB_URLS)
            val linkColorSpan = ForegroundColorSpan(Color.parseColor("#3478F6"))

            spannable.getSpans(0, spannable.length, URLSpan::class.java).forEach { urlSpan ->
                val start = spannable.getSpanStart(urlSpan)
                val end = spannable.getSpanEnd(urlSpan)
                spannable.setSpan(linkColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }

        if (iconResId != null && index != 0) {
            val iconImageView = ImageView(context).apply {
                setImageResource(iconResId)
               // setColorFilter(labelColor, PorterDuff.Mode.SRC_IN)
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(60, context),
                    dpToPx(60, context)
                )
            }
            linearLayout.addView(iconImageView)
        }

        fieldContainer.addView(labelTextView)
        fieldContainer.addView(valueTextView)

        linearLayout.addView(fieldContainer)

        if (showDivider && index != fields.lastIndex) {
            val dividerView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(1, context)
                ).apply {
                    topMargin = dpToPx(8, context)
                    bottomMargin = dpToPx(8, context)
                }
                setBackgroundColor(Color.parseColor("#FF9E9E9E"))
            }
            linearLayout.addView(dividerView)
        }

    }
}

fun generateBarcode(data: String, format: BarcodeFormat, width: Int, height: Int): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = if (format == BarcodeFormat.PDF_417) {
            PDF417Writer().encode(data, format, width, height)
        } else {
            MultiFormatWriter().encode(data, format, width, height)
        }

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height

        val bmp = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.RGB_565)
        for (x in 0 until matrixWidth) {
            for (y in 0 until matrixHeight) {
                bmp.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }

        Bitmap.createScaledBitmap(bmp, width, height, true)
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}

fun getBarcodeType(format: String?): BarcodeFormat {
    return when (format) {
        "PKBarcodeFormatQR" -> BarcodeFormat.QR_CODE
        "PKBarcodeFormatPDF417" -> BarcodeFormat.PDF_417
        "PKBarcodeFormatAztec" -> BarcodeFormat.AZTEC
        "PKBarcodeFormatCode128" -> BarcodeFormat.CODE_128
        else -> BarcodeFormat.QR_CODE
    }
}

fun getBarcodeHeight(format: String?, context: Context): Int {
    val screenHeight = context.resources.displayMetrics.heightPixels
    return when (format) {
        "PKBarcodeFormatQR" -> (screenHeight / 5.5).toInt()
        "PKBarcodeFormatPDF417" -> (screenHeight / 13).toInt()
        "PKBarcodeFormatAztec" -> (screenHeight / 5.5).toInt()
        "PKBarcodeFormatCode128" -> (screenHeight / 13).toInt()
        else -> 180
    }
}

fun getBarcodeWidth(format: String?, context: Context): Int {
    val screenHeight = context.resources.displayMetrics.heightPixels
    return when (format) {
        "PKBarcodeFormatQR" -> (screenHeight / 5.5).toInt()
        "PKBarcodeFormatPDF417" -> 280
        "PKBarcodeFormatAztec" -> (screenHeight / 5.5).toInt()
        "PKBarcodeFormatCode128" -> 280
        else -> 180
    }
}

fun parseRgbColor(rgbColor: String): String {
    val cleaned = rgbColor.removePrefix("rgb(").removeSuffix(")")
    val rgbValues = cleaned.split(",").map { it.trim().toInt() }
    return String.format("#%02X%02X%02X", rgbValues[0], rgbValues[1], rgbValues[2])
}



