package com.tbnt.ruby.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.tbnt.ruby.R

class ExpandableLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var titleView: View
    private lateinit var contentLayout: ViewGroup
    private lateinit var arrow: View

    private var expanded = false

    private var animating = false

    private var animDuration = 200L

    private val expandAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = animDuration
        addUpdateListener {
            val progress = it.animatedValue as Float
            val wrapContentHeight = contentLayout.measureWrapContentHeight()
            contentLayout.updateLayoutParams {
                height = (wrapContentHeight * progress).toInt()
            }

            arrow.rotation = progress * 180
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animating = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animating = false
            }
        })
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        val parentLayout = getChildAt(0) as ViewGroup

        titleView = parentLayout.getChildAt(0)
        contentLayout = parentLayout.getChildAt(1) as ViewGroup
        arrow = parentLayout.findViewById(R.id.arrowImageView)

        if (expanded) {
            arrow.rotation = 180f
        } else {
            contentLayout.updateLayoutParams {
                height = 0
            }
            arrow.rotation = 0f

        }

        titleView.setOnClickListener {
            when {
                animating -> {
                    expandAnimator.reverse()
                    expanded = !expanded
                }
                expanded -> {
                    expandAnimator.reverse()
                    expanded = false
                }
                else -> {
                    expandAnimator.start()
                    expanded = true
                }
            }
        }
    }
}

fun ViewGroup.measureWrapContentHeight(): Int {
    this.measure(
        View.MeasureSpec
            .makeMeasureSpec((this.parent as View).measuredWidth, View.MeasureSpec.EXACTLY),
        View.MeasureSpec
            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    return measuredHeight
}