package com.example.sleeptimer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2

class CircularSeekBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    private var seekBarChangeListener: OnCircularSeekBarChangeListener? = null
    private var maxProgress = 100
    private var progress = 0f
    private var progressColor: Int = 0
    private var seekBarRadius = 0f

    private val DEFAULT_PROGRESS_COLOR = Color.RED
    private val DEFAULT_RADIUS = 100f

    init {
        circlePaint.style = Paint.Style.STROKE
        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeCap = Paint.Cap.ROUND

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, 0, 0)

        try {
            circlePaint.color = typedArray.getColor(R.styleable.CircularSeekBar_seekBarUnselectedColor, 0xFFBDC3C7.toInt())
            circlePaint.strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircularSeekBar_seekBarWidth, 10).toFloat()

            arcPaint.color = typedArray.getColor(R.styleable.CircularSeekBar_seekBarColor, 0xFF3498DB.toInt())
            arcPaint.strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircularSeekBar_seekBarWidth, 10).toFloat()

            maxProgress = typedArray.getInt(R.styleable.CircularSeekBar_seekBarMax, 100)
            progress = typedArray.getFloat(R.styleable.CircularSeekBar_seekBarProgress, 0f)

            val listenerClassName = typedArray.getString(R.styleable.CircularSeekBar_seekBarChangeListener)
            if (listenerClassName != null) {
                try {
                    val listenerClass = Class.forName(listenerClassName)
                    seekBarChangeListener = listenerClass.newInstance() as OnCircularSeekBarChangeListener
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessError) {
                    e.printStackTrace()
                } catch (e: InstantiationError) {
                    e.printStackTrace()
                }
            }

            progressColor = typedArray.getColor(R.styleable.CircularSeekBar_progressColor, DEFAULT_PROGRESS_COLOR)
            seekBarRadius = typedArray.getDimension(R.styleable.CircularSeekBar_seekBarRadius, DEFAULT_RADIUS)
        } finally {
            typedArray.recycle()
        }
    }

    interface OnCircularSeekBarChangeListener {
        fun onProgressChanged(circularSeekBar: CircularSeekBar, progress: Float, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: CircularSeekBar)
        fun onStopTrackingTouch(seekBar: CircularSeekBar)
    }

    fun setOnCircularSeekBarChangeListener(listener: OnCircularSeekBarChangeListener?) {
        seekBarChangeListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2.toFloat()
        val centerY = height / 2.toFloat()
        val radius = width / 2.toFloat() - circlePaint.strokeWidth / 2

        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        val angle = 360 * (progress / maxProgress)
        canvas.drawArc(rect, -90f, angle, false, arcPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener!!.onStartTrackingTouch(this)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                updateOnTouch(x, y)
            }
            MotionEvent.ACTION_UP -> if (seekBarChangeListener != null) {
                seekBarChangeListener!!.onStopTrackingTouch(this)
            }
        }
        return true
    }

    private fun updateOnTouch(x: Float, y: Float) {
        val centerX = width / 2.toFloat()
        val centerY = height / 2.toFloat()

        val angle = atan2((y - centerY).toDouble(), (x - centerX).toDouble()) * (180 / Math.PI).toFloat()
        val calculatedProgress = ((angle + 360) % 360 * maxProgress / 360).toFloat()

        setProgress(calculatedProgress)
    }

    fun setProgress(progress: Float) {
        if (progress < 0) {
            this.progress = 0f
        } else if (progress > maxProgress) {
            this.progress = maxProgress.toFloat()
        } else {
            this.progress = progress
        }

        if (seekBarChangeListener != null) {
            seekBarChangeListener!!.onProgressChanged(this, this.progress, true)
        }

        invalidate()
    }
}
