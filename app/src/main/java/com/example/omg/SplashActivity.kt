package com.example.omg

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var lottieAnimation: LottieAnimationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
            // Usuario con sesión → Verificar si tiene servicios
            checkUserServicesAndNavigate(currentUser.uid)
        } else {
            // No hay sesión → Ir al Login
            navigateToLogin()
        }
    }

    // Verificar si el usuario tiene servicios seleccionados
    private fun checkUserServicesAndNavigate(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteServices = document.get("favoriteServices") as? List<*>

                    if (favoriteServices.isNullOrEmpty()) {
                        // No tiene servicios → Ir a selección
                        navigateToServiceSelection()
                    } else {
                        // Ya tiene servicios → Ir al Home
                        navigateToHome()
                    }
                } else {
                    // Usuario no existe en Firestore → Ir a selección
                    navigateToServiceSelection()
                }
            }
            .addOnFailureListener { e ->
                Log.e("SplashActivity", "Error al verificar servicios: ${e.message}")
                // En caso de error, ir al Home
                navigateToHome()
            }
    }

    // Navegar al Login
    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Navegar a selección de servicios
    private fun navigateToServiceSelection() {
        val intent = Intent(this, ServiceSelectionActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Navegar al Home
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