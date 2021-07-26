package com.hfad.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes

class PieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var wholeMs = 0L
    private var currentMs = 0L
    private var color = 0
    private var style = FILL
    private val paint = Paint()

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )
            color = styledAttrs.getColor(R.styleable.CustomView_custom_color, Color.RED)
            style = styledAttrs.getInt(R.styleable.CustomView_custom_style, FILL)
            styledAttrs.recycle()
        }

        paint.color = color
        paint.style = Paint.Style.FILL

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val sweepAngle = if (currentMs == 0L) 0f
            else (1 - currentMs.toFloat() / wholeMs) * 360

        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            270f,
            sweepAngle,
            true,
            paint
        )
    }


    fun setCurrent(current: Long) {
        currentMs = current
        invalidate()
    }

    fun setWholeMs(wholeMs: Long) {
        this.wholeMs = wholeMs
    }

    fun changeColor(color: Int) {
        paint.color = color
    }

    private companion object {

        private const val FILL = 0
    }



}