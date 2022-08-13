package com.tbnt.ruby.ui.common

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.AudioBtnLayoutBinding
import com.tbnt.ruby.toPx

class AudionBtn(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding: AudioBtnLayoutBinding

    init {
        background = AppCompatResources.getDrawable(context, R.drawable.audio_btn_layout)
        binding = AudioBtnLayoutBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setText(text: String) {
        binding.btnTitle.text = text
    }

    fun setTextColor(color: String) {
        if (color.isEmpty()) return
        binding.btnTitle.setTextColor(color.toColorInt())
    }

    fun setStrokeColor(color: String) {
        if (color.isEmpty()) return
        if (color.isEmpty()) return
        (background.mutate() as? GradientDrawable)?.setStroke(2.toPx.toInt(), color.toColorInt())
    }

    fun setBgColor(color: String) {
        if (color.isEmpty()) return
        (background.mutate() as? GradientDrawable)?.setColor(color.toColorInt())
    }

}