package com.tbnt.ruby.ui.auidiobooks

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.toPx


class AudioRvDecoration : RecyclerView.ItemDecoration() {

    private val space8: Int = 8.toPx.toInt()
    private val space70: Int = 80.toPx.toInt()
    private var isSubscribed = false //TODO add logic

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        outRect.set(
            space8,
            space8,
            space8,
            if (!isSubscribed && (position == itemCount - 1 || position == itemCount - 2)) space70 else space8
        )
    }
}