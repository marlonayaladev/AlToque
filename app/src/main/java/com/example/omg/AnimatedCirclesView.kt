package com.example.omg

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        textSize = 32f
    }

    private var animationProgress = 0f
    private val animationDuration = 2500L
    private var animationFinished = false

    private data class Circle(
        var x: Float,
        var y: Float,
        var alpha: Float = 1f,
        var text: String = "",
        var iconResId: Int? = null
    )
    private val circles = mutableListOf<Circle>()

    private var circleRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private var favoriteServices = listOf<String>()

    init {
        post {
            centerX = width / 2f
            centerY = height / 2f
            circleRadius = width * 0.15f
            startAnimation()
        }
    }

    fun setFavoriteServices(services: List<String>) {
        favoriteServices = services
        invalidate()
    }

    private fun getIconForService(service: String): Int? {
        return when (service) {
            "Gasfitería"     -> R.drawable.ic_gasfiteria
            "Electricidad"   -> R.drawable.ic_electricidad
            "Carpintería"    -> R.drawable.ic_pintura
            "Pintura"        -> R.drawable.ic_pintura
            "Limpieza"       -> R.drawable.ic_limpieza
            "Jardinería"     -> R.drawable.ic_jardineria
            "Cerrajería"     -> R.drawable.ic_cerrajeria
            "Mecánica"       -> R.drawable.ic_mecanica
            "Técnico"    -> R.drawable.ic_informatica
            "Manicure"       -> R.drawable.ic_manicurista
            else -> null // si no hay ícono definido
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
                val service1 = if (favoriteServices.size > 0) favoriteServices[0] else "Servicio 1"
                val service2 = if (favoriteServices.size > 1) favoriteServices[1] else "Servicio 2"
                val service3 = if (favoriteServices.size > 2) favoriteServices[2] else "Servicio 3"
                val service4 = if (favoriteServices.size > 3) favoriteServices[3] else "Servicio 4"

                circles.add(Circle(centerX, centerY - spacing, text = service1, iconResId = getIconForService(service1)))
                circles.add(Circle(centerX - spacing, centerY, text = service2, iconResId = getIconForService(service2)))
                // foto central de buscar servicios
                circles.add(Circle(centerX, centerY, iconResId = R.drawable.ic_buscar_servicios))
                circles.add(Circle(centerX + spacing, centerY, text = service3, iconResId = getIconForService(service3)))
                circles.add(Circle(centerX, centerY + spacing, text = service4, iconResId = getIconForService(service4)))
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (circle in circles) {
            paint.alpha = (circle.alpha * 255).toInt()
            canvas.drawCircle(circle.x, circle.y, circleRadius, paint)

            circle.iconResId?.let { resId ->
                val bitmap = BitmapFactory.decodeResource(resources, resId)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (circleRadius * 2.1f).toInt(), (circleRadius * 2.1f).toInt(), true)
                canvas.drawBitmap(scaledBitmap, circle.x - scaledBitmap.width / 2f, circle.y - scaledBitmap.height / 2f, null)
            }

            if (circle.text.isNotEmpty() && animationFinished && circle.iconResId == null) {
                val lines = circle.text.split("\n")
                var yOffset = circle.y - (lines.size - 1) * 18f


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
                    val serviceName = when (index) {
                        0 -> if (favoriteServices.size > 0) favoriteServices[0] else "Servicio"
                        1 -> if (favoriteServices.size > 1) favoriteServices[1] else "Servicio"
                        2 -> {
                            Toast.makeText(context, "Ver todos los servicios", Toast.LENGTH_SHORT).show()
                            return true
                        }
                        3 -> if (favoriteServices.size > 2) favoriteServices[2] else "Servicio"
                        4 -> if (favoriteServices.size > 3) favoriteServices[3] else "Servicio"
                        else -> "Servicio"
                    }

                    val intent = Intent(context, LocationConfirmationActivity::class.java).apply {
                        putExtra("SERVICE_NAME", serviceName)
                    }
                    context.startActivity(intent)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}