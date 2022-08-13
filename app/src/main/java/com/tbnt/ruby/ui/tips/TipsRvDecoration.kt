package com.tbnt.ruby.ui.tips

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.toPx


class TipsRvDecoration : RecyclerView.ItemDecoration() {

    private val space8: Int = 8.toPx.toInt()
    private val space16dp: Int = 16.toPx.toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        when (parent.adapter?.getItemViewType(position) ?: TIPS_TITLE) {
            TIPS_TITLE -> outRect.set(
                space16dp,
                if (position > 0) space16dp else 0,
                space16dp,
                0
            )
            TIPS_CONTENT -> outRect.set(space16dp, 0, space16dp, space8)
        }

    }
}