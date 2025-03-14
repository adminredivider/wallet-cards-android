package io.walletcards.android.presentation.add_card

import android.content.Context
import android.view.View
import com.google.android.flexbox.FlexboxLayoutManager

class CustomFlexboxLayoutManager(context: Context) : FlexboxLayoutManager(context) {

    private val maxPerRow = 6

    override fun getFlexItemAt(index: Int): View? {
        val item = super.getFlexItemAt(index)
        val params = item?.layoutParams as? LayoutParams
        params?.isWrapBefore = index % maxPerRow == 0
        return item
    }
}