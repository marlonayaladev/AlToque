package com.example.omg

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var lottieAnimation: LottieAnimationView
    private lateinit var splashLogo: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Pantalla completa sin bordes
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        setContentView(R.layout.activity_splash)

        // Referencias
        lottieAnimation = findViewById(R.id.lottie_splash)
        splashLogo = findViewById(R.id.splash_logo)

        // Ocultar logo al inicio
        splashLogo.alpha = 0f
        splashLogo.visibility = View.VISIBLE


        // Configurar animación Lottie
        lottieAnimation.apply {
            setAnimation("splash_animation.json")
            scaleType = ImageView.ScaleType.CENTER_CROP
            speed = 0.5f
            playAnimation()
        }

        // Fade-in del logo sincronizado con la animación
        lottieAnimation.addAnimatorUpdateListener {
            if (it.animatedFraction > 0.1f) {
                splashLogo.animate().alpha(1f).setDuration(600).start()
            }
        }

        // Navegación al finalizar la animación
        lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                checkUserSession()
            }

            override fun onAnimationCancel(animation: Animator) {
                checkUserSession()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun checkUserSession() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            checkUserServicesAndNavigate(currentUser.uid)
        } else {
            navigateToLogin()
        }
    }

    private fun checkUserServicesAndNavigate(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteServices = document.get("favoriteServices") as? List<*>
                    if (favoriteServices.isNullOrEmpty()) {
                        navigateToServiceSelection()
                    } else {
                        navigateToHome()
                    }
                } else {
                    navigateToServiceSelection()
                }
            }
            .addOnFailureListener { e ->
                Log.e("SplashActivity", "Error al verificar servicios: ${e.message}")
                navigateToHome()
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToServiceSelection() {
        val intent = Intent(this, ServiceSelectionActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // No hacer nada - evitar cerrar el splash
    }
}