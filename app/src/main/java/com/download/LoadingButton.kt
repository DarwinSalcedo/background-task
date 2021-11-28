package com.download

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK // default color

    // tells the compiler that the value of a variable
    // must never be cached as its value may change outside
    @Volatile
    private var progress: Double = 0.0
    private var valueAnimator: ValueAnimator

    // observes the state of button
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Init) { p, old, new ->
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate() // redraw the screen
        requestLayout() // when rectangular progress dimension changes
        Log.e("PROGRESS", "updateListener: $progress")
        if (progress == 100.0) hasCompletedDownload()
    }

    // call after downloading is completed
    fun hasCompletedDownload() {
        // cancel the animation when file is downloaded
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        invalidate()
        requestLayout()
        Log.e("PROGRESS", "hasCompletedDownload: $progress")
    }

    // initialize
    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            // properties for downloading progress is defined
            R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)

        // initialize custom attributes of the button
        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {

            // button back-ground color
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

    // set attributes of paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER // button text alignment
        textSize = 55.0f //button text size
        typeface = Typeface.create("", Typeface.BOLD) // button text's font style
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading
        animation()
        return true
    }

    // start the animation when button is clicked
    private fun animation() {
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor
        // draw custom button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // to show rectangular progress on custom button while file is downloading
        var buttonText = resources.getString(R.string.init)
        when (buttonState) {
            ButtonState.Init -> {
                paint.color = Color.RED
                buttonText = resources.getString(R.string.start)
            }
            ButtonState.Completed -> {
                paint.color = Color.GREEN
                buttonText = resources.getString(R.string.download)
            }
            ButtonState.Loading -> {
                paint.color = Color.YELLOW
                buttonText = resources.getString(R.string.loading)
            }
        }

        canvas.drawRect(
            0f, 0f,
            (width * (progress / 100)).toFloat(), height.toFloat(), paint
        )

        // write the text on custom button
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }
}
