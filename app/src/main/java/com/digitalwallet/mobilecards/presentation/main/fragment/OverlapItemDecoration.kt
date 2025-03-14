package com.digitalwallet.mobilecards.presentation.main.fragment

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.digitalwallet.mobilecards.presentation.util.dpToPx

class OverlapItemDecoration(context: Context, overlapMarginDp: Int) :
    RecyclerView.ItemDecoration() {
    private val overlapMarginPx = dpToPx(overlapMarginDp, context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            outRect.top = -overlapMarginPx
        }
    }
}
