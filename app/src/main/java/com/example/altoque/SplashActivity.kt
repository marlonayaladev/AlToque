package com.example.altoque


import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.altoque.RegisterMethodActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var lottieAnimation: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        setContentView(R.layout.activity_splash)

        lottieAnimation = findViewById(R.id.lottie_splash)

        lottieAnimation.apply {
            setAnimation("splash_animation.json")
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            speed = 0.5f
            playAnimation()
        }

        lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                navigateToMain()
            }

            override fun onAnimationCancel(animation: Animator) {
                navigateToMain()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun navigateToMain() {
        // 1. Cambia MainActivity::class.java por RegisterMethodActivity::class.java
        val intent = Intent(this, RegisterMethodActivity::class.java)

        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {}
}