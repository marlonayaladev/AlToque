package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class VerifyCodeActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvPhoneNumber: TextView
    private lateinit var etCode1: EditText
    private lateinit var etCode2: EditText
    private lateinit var etCode3: EditText
    private lateinit var etCode4: EditText
    private lateinit var etCode5: EditText
    private lateinit var etCode6: EditText
    private lateinit var btnVerify: MaterialButton
    private lateinit var tvResendCode: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var verificationId: String? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtener datos del intent
        verificationId = intent.getStringExtra("verificationId")
        phoneNumber = intent.getStringExtra("phoneNumber")

        initViews()
        setupListeners()
        setupCodeInputs()

        // Mostrar número de teléfono
        tvPhoneNumber.text = "+51 $phoneNumber"
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        etCode1 = findViewById(R.id.etCode1)
        etCode2 = findViewById(R.id.etCode2)
        etCode3 = findViewById(R.id.etCode3)
        etCode4 = findViewById(R.id.etCode4)
        etCode5 = findViewById(R.id.etCode5)
        etCode6 = findViewById(R.id.etCode6)
        btnVerify = findViewById(R.id.btnVerify)
        tvResendCode = findViewById(R.id.tvResendCode)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnVerify.setOnClickListener {
            val code = getVerificationCode()
            if (code.length == 6) {
                verifyCode(code)
            } else {
                Toast.makeText(this, "Ingresa el código completo", Toast.LENGTH_SHORT).show()
            }
        }

        tvResendCode.setOnClickListener {
            Toast.makeText(this, "Reenviando código...", Toast.LENGTH_SHORT).show()
            // TODO: Implementar reenvío de código
        }
    }

    private fun setupCodeInputs() {
        val editTexts = listOf(etCode1, etCode2, etCode3, etCode4, etCode5, etCode6)

        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < editTexts.size - 1) {
                        // Mover al siguiente campo
                        editTexts[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // Verificar automáticamente si se completaron todos los dígitos
                    if (index == editTexts.size - 1 && s?.length == 1) {
                        val code = getVerificationCode()
                        if (code.length == 6) {
                            verifyCode(code)
                        }
                    }
                }
            })

            // Manejo del backspace
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && editText.text.isEmpty() && index > 0) {
                    editTexts[index - 1].requestFocus()
                    return@setOnKeyListener true
                }
                false
            }
        }

        // Focus en el primer campo
        etCode1.requestFocus()
    }

    private fun getVerificationCode(): String {
        return etCode1.text.toString() +
                etCode2.text.toString() +
                etCode3.text.toString() +
                etCode4.text.toString() +
                etCode5.text.toString() +
                etCode6.text.toString()
    }

    private fun verifyCode(code: String) {
        if (verificationId == null) {
            Toast.makeText(this, "Error: ID de verificación no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        // 🆕 Verificar si el usuario ya existe
                        checkIfUserExistsAndNavigate(firebaseUser.uid)
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Código incorrecto. Intenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearCode()
                }
            }
    }

    // 🆕 Verificar si es primera vez
    private fun checkIfUserExistsAndNavigate(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Usuario ya existe
                    val favoriteServices = document.get("favoriteServices") as? List<*>

                    // Actualizar lastLogin
                    db.collection("users").document(uid)
                        .update("lastLogin", System.currentTimeMillis())

                    Toast.makeText(this, "¡Verificación exitosa!", Toast.LENGTH_SHORT).show()

                    if (favoriteServices.isNullOrEmpty()) {
                        // No tiene servicios → Ir a selección
                        navigateToServiceSelection()
                    } else {
                        // Ya tiene servicios → Ir al Home
                        navigateToHome()
                    }
                } else {
                    // Usuario nuevo → Crear y ir a selección
                    val user = User(
                        uid = uid,
                        name = "",
                        email = "",
                        phone = auth.currentUser?.phoneNumber ?: "+51$phoneNumber",
                        photoUrl = "",
                        provider = "phone",
                        createdAt = System.currentTimeMillis(),
                        lastLogin = System.currentTimeMillis(),
                        favoriteServices = emptyList()
                    )

                    saveNewUserAndNavigate(user)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error: ${e.message}")
                Toast.makeText(this, "Error. Continuando...", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
    }

    // 🆕 Guardar nuevo usuario
    private fun saveNewUserAndNavigate(user: User) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuario nuevo guardado")
                Toast.makeText(this, "¡Verificación exitosa!", Toast.LENGTH_SHORT).show()
                navigateToServiceSelection()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error: ${e.message}")
                Toast.makeText(this, "Error al guardar. Continuando...", Toast.LENGTH_SHORT).show()
                navigateToServiceSelection()
            }
    }

    // 🆕 Navegar a selección de servicios
    private fun navigateToServiceSelection() {
        val intent = Intent(this, ServiceSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    // 🆕 Navegar al Home
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    private fun clearCode() {
        etCode1.text.clear()
        etCode2.text.clear()
        etCode3.text.clear()
        etCode4.text.clear()
        etCode5.text.clear()
        etCode6.text.clear()
        etCode1.requestFocus()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnVerify.isEnabled = !show
    }
}