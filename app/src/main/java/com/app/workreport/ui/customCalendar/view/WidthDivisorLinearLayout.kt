package com.app.workreport.ui.customCalendar.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isGone
class WidthDivisorLinearLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        super(context, attrs, defStyle)

    var widthDivisor: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        @Suppress("NAME_SHADOWING")
        val heightMeasureSpec = if (widthDivisor > 0) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.makeMeasureSpec(width / widthDivisor, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        check(children.none { it.isGone }) {
            "Use `View.INVISIBLE` to hide any unneeded day content instead of `View.GONE`"
        }
    }
}