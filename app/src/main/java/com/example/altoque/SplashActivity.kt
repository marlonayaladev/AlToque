package com.example.altoque

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // <-- 1. AÑADE ESTE IMPORT
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 2. AÑADE ESTA LÍNEA ANTES DE TODO LO DEMÁS
        // Conecta tu actividad a la splash screen del sistema para una transición suave.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            // Llama a nuestro Composable que muestra la animación
            SplashScreen {
                // Esto se ejecuta cuando la animación termina
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Cierra la SplashActivity para que no se pueda volver a ella
            }
        }
    }
}

@Composable
fun SplashScreen(onAnimationFinish: () -> Unit) {
    // Carga la animación desde res/raw. Cambia 'splash_animation' por el nombre de tu archivo si es diferente.
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))

    // Controla el progreso de la animación
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // La animación se reproduce solo una vez
    )

    // Escucha cuando la animación termina (progress llega a 1.0f)
    LaunchedEffect(progress) {
        if (progress == 1.0f) {
            onAnimationFinish()
        }
    }

    // Muestra la animación en el centro de la pantalla
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            // AÑADE ESTE MODIFIER PARA QUE LA ANIMACIÓN LLENE LA CAJA
            modifier = Modifier.fillMaxSize()
        )
    }
}