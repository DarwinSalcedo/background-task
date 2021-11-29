package com.download

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val PROGRESS_COMPLETED_VALUE = 100.0
        const val PROGRESS_INIT_VALUE = 0.0
        private const val START_ANGLE_POINT = 0.0f
        private const val CIRCLE_ANGLE_COMPLETED = 360.0f
    }

    private var bgColor: Int = Color.WHITE
    private var textColor: Int = Color.BLACK

    @Volatile
    private var progress: Double = PROGRESS_INIT_VALUE

    private var valueAnimator: ValueAnimator
    private var hasError: Boolean = false
    private var oval: RectF = RectF()


    private var buttonState: ButtonState by Delegates.observable(ButtonState.Init) { _, _, _ -> }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        reDraw()
        if (hasError || progress == PROGRESS_COMPLETED_VALUE) hasCompletedDownload()
    }

    private fun reDraw() {
        invalidate()
        requestLayout()
    }

    private fun hasCompletedDownload() {
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        progress = PROGRESS_INIT_VALUE
        reDraw()
        hasError = false
    }

    fun hasErrorDownload() {
        hasError = true
    }

    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorAccent)
            )

            // button text color
            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            // clearing all the data associated with attribute
            attr.recycle()
        }
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private val paintCircle: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 10.0f
        color = Color.RED
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading
        animation()
        return true
    }

    private fun animation() {
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)


        var buttonText = resources.getString(R.string.init)
        when (buttonState) {
            ButtonState.Init -> paint.color = context.getColor(R.color.colorPrimary)

            ButtonState.Completed -> paint.color = context.getColor(R.color.colorPrimaryDark)

            ButtonState.Loading -> {
                paint.color = Color.BLACK
                buttonText = resources.getString(R.string.loading)
            }
        }

        canvas.drawRect(
            0f, 0f,
            (width * (progress / 100)).toFloat(), height.toFloat(), paint
        )

        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)

        val size = if (width < height) width.toFloat() else height.toFloat()

        oval.apply {
            left = 20f
            top = 20f
            right = (size - 2 * 20f)
            bottom = (size - 2 * 20f)
        }

        canvas.drawArc(
            oval,
            START_ANGLE_POINT,
            ((progress * CIRCLE_ANGLE_COMPLETED) / PROGRESS_COMPLETED_VALUE).toFloat(),
            false,
            paintCircle
        );
    }
}
