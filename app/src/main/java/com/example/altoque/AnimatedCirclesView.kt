package com.example.altoque

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

class AnimatedCirclesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFF00BCD4.toInt()
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 40f
    }

    private var animationProgress = 0f
    private val animationDuration = 2500L
    private var animationFinished = false

    private data class Circle(
        var x: Float,
        var y: Float,
        var alpha: Float = 1f,
        var text: String = ""
    )
    private val circles = mutableListOf<Circle>()

    private var circleRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    init {
        post {
            centerX = width / 2f
            centerY = height / 2f
            circleRadius = width * 0.15f
            startAnimation()
        }
    }

    private fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = animationDuration
        animator.interpolator = DecelerateInterpolator(1.5f)

        animator.addUpdateListener { animation ->
            animationProgress = animation.animatedValue as Float
            updateCircles()
            invalidate()
        }

        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                animationFinished = true
                invalidate()
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })

        animator.start()
    }

    private fun updateCircles() {
        circles.clear()
        val spacing = circleRadius * 2.3f

        when {
            animationProgress < 0.2f -> {
                val progress = animationProgress / 0.2f
                circles.add(Circle(centerX, centerY, alpha = progress))
            }
            animationProgress < 0.4f -> {
                val progress = (animationProgress - 0.2f) / 0.2f
                circles.add(Circle(centerX, centerY))
                circles.add(Circle(centerX, centerY - spacing * progress, alpha = progress))
                circles.add(Circle(centerX, centerY + spacing * progress, alpha = progress))
            }
            animationProgress < 0.6f -> {
                val progress = (animationProgress - 0.4f) / 0.2f
                circles.add(Circle(centerX, centerY))
                circles.add(Circle(centerX, centerY - spacing))
                circles.add(Circle(centerX, centerY + spacing))
                circles.add(Circle(centerX - spacing * progress, centerY, alpha = progress))
                circles.add(Circle(centerX + spacing * progress, centerY, alpha = progress))
            }
            animationProgress < 0.8f -> {
                val progress = (animationProgress - 0.6f) / 0.2f
                circles.add(Circle(centerX, centerY - spacing))
                circles.add(Circle(centerX - spacing, centerY))
                circles.add(Circle(centerX, centerY, alpha = 1f - progress * 0.3f))
                circles.add(Circle(centerX + spacing, centerY))
                circles.add(Circle(centerX, centerY + spacing))
            }
            else -> {
                circles.add(Circle(centerX, centerY - spacing))
                circles.add(Circle(centerX - spacing, centerY))
                circles.add(Circle(centerX, centerY, text = "¿Qué servicio\nnecesitas?"))
                circles.add(Circle(centerX + spacing, centerY))
                circles.add(Circle(centerX, centerY + spacing))
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (circle in circles) {
            paint.alpha = (circle.alpha * 255).toInt()
            canvas.drawCircle(circle.x, circle.y, circleRadius, paint)

            if (circle.text.isNotEmpty() && animationFinished) {
                val lines = circle.text.split("\n")
                var yOffset = circle.y - (lines.size - 1) * 20f
                for (line in lines) {
                    canvas.drawText(line, circle.x, yOffset, textPaint)
                    yOffset += 40f
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!animationFinished) return false

        if (event.action == MotionEvent.ACTION_DOWN) {
            for ((index, circle) in circles.withIndex()) {
                val dx = event.x - circle.x
                val dy = event.y - circle.y
                val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                if (distance <= circleRadius) {
                    when (index) {
                        0 -> Toast.makeText(context, "Botón Superior", Toast.LENGTH_SHORT).show()
                        1 -> Toast.makeText(context, "Botón Izquierdo", Toast.LENGTH_SHORT).show()
                        2 -> Toast.makeText(context, "Botón Central", Toast.LENGTH_SHORT).show()
                        3 -> Toast.makeText(context, "Botón Derecho", Toast.LENGTH_SHORT).show()
                        4 -> Toast.makeText(context, "Botón Inferior", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}