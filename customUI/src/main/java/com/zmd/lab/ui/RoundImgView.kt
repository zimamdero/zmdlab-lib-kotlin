package com.zmd.lab.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class RoundImgView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    init {
        var ar = 0f

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RoundImgView,
            0, 0).apply {
            try {
                ar = getDimension(R.styleable.RoundImgView_cornerRadius, 0f)
            } finally {
                recycle()
            }
        }

        val r = ar
        val radi = floatArrayOf(r, r, r, r, r, r, r, r)
        val shape = RoundRectShape(radi, null, radi)
        background = ShapeDrawable(shape)
        clipToOutline = true
    }
}