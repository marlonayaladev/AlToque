package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvCountryCode: TextView
    private lateinit var etPhone: EditText
    private lateinit var btnSendCode: MaterialButton

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvCountryCode = findViewById(R.id.tvCountryCode)
        etPhone = findViewById(R.id.etPhone)
        btnSendCode = findViewById(R.id.btnSendCode)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSendCode.setOnClickListener {
            val phoneNumber = etPhone.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Ingresa tu número de teléfono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phoneNumber.length < 9) {
                Toast.makeText(this, "Número de teléfono inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVerificationCode(phoneNumber)
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        // Construir número completo con código de país
        val countryCode = tvCountryCode.text.toString()
        val fullPhoneNumber = "$countryCode$phoneNumber"

        Toast.makeText(this, "Enviando código a $fullPhoneNumber", Toast.LENGTH_SHORT).show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(fullPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-verificación exitosa (números de prueba)
            Toast.makeText(
                this@PhoneLoginActivity,
                "Número de prueba detectado",
                Toast.LENGTH_SHORT
            ).show()

            // Para números de prueba, navegar manualmente
            verificationId?.let {
                val intent = Intent(this@PhoneLoginActivity, VerifyCodeActivity::class.java)
                intent.putExtra("verificationId", it)
                intent.putExtra("phoneNumber", etPhone.text.toString())
                startActivity(intent)
            } ?: run {
                // Si ya tiene credenciales, hacer login directo
                signInWithPhoneAuthCredential(credential)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(
                this@PhoneLoginActivity,
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // Código enviado exitosamente
            this@PhoneLoginActivity.verificationId = verificationId

            Toast.makeText(
                this@PhoneLoginActivity,
                "Código enviado",
                Toast.LENGTH_SHORT
            ).show()

            // Navegar a la pantalla de verificación
            val intent = Intent(this@PhoneLoginActivity, VerifyCodeActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("phoneNumber", etPhone.text.toString())
            startActivity(intent)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
                    // TODO: Navegar al Home
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error en la autenticación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}