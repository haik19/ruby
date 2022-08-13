package com.tbnt.ruby.ui.myaudiobooks

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.toPx

class MyAudioBooksRvDecoration : RecyclerView.ItemDecoration() {

    private val space16dp: Int = 16.toPx.toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(space16dp, 0, space16dp, space16dp)
    }
}