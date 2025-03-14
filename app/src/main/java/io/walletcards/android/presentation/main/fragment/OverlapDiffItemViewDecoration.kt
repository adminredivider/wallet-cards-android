package io.walletcards.android.presentation.main.fragment

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OverlapDiffItemViewDecoration(
    context: Context,
    private val overlapMarginDp: Int = 20,
    private val largeMarginDp: Int = 65
) : RecyclerView.ItemDecoration() {

    private val overlapMarginPx = dpToPx(overlapMarginDp, context)
    private val largeMarginPx = dpToPx(largeMarginDp, context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val adapter = parent.adapter ?: return
        val currentViewType = adapter.getItemViewType(position)

        val previousViewType = if (position > 0) adapter.getItemViewType(position - 1) else null
        val itemHeight = view.height

        if (position != 0) {
            outRect.top = if (currentViewType == previousViewType) {
                (overlapMarginPx - itemHeight)
            } else {
                (largeMarginPx - itemHeight)
            }
        }
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
