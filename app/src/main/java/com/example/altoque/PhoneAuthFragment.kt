package com.example.altoque

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

// ***************************************************************
// IMPORTACIONES CLAVE (Corregimos la sintaxis de las importaciones separándolas)
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
// ***************************************************************

class PhoneAuthFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Variables
    private lateinit var etPhoneNumber: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var btnSendCode: Button
    private lateinit var btnVerifyCode: Button

    // Firebase Variables
    private var verificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone_auth, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 1. Asignar IDs de la vista (Asumiendo que el XML corregido se aplicó)
        etPhoneNumber = view.findViewById(R.id.et_phone_number)
        btnSendCode = view.findViewById(R.id.btn_send_code)
        etVerificationCode = view.findViewById(R.id.et_verification_code)
        btnVerifyCode = view.findViewById(R.id.btn_verify_code)

        // 2. Configurar Listeners
        btnSendCode.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            if (phoneNumber.isEmpty() || !phoneNumber.startsWith("+")) {
                Toast.makeText(context, "Ingrese un número válido con prefijo (+código).", Toast.LENGTH_LONG).show()
            } else {
                startPhoneNumberVerification(phoneNumber)
            }
        }

        btnVerifyCode.setOnClickListener {
            val code = etVerificationCode.text.toString().trim()
            if (code.isNotEmpty() && verificationId != null) {
                verifyCodeAndSignIn(code)
            } else {
                Toast.makeText(context, "Ingrese el código.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Toast.makeText(context, "Verificación fallida: ${exception.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            verificationId = id
            Toast.makeText(context, "Código enviado. Ingréselo abajo.", Toast.LENGTH_LONG).show()

            // Transición de UI
            etPhoneNumber.visibility = View.GONE
            btnSendCode.visibility = View.GONE
        }
    }

    private fun verifyCodeAndSignIn(code: String) {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserData(user?.uid, user?.phoneNumber)
                    // Asumo que tienes una función para navegar a la actividad principal
                    (activity as? LoginActivity)?.navigateToMain()
                } else {
                    Toast.makeText(context, "Código incorrecto o expirado.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(uid: String?, phoneNumber: String?) {
        if (uid == null) return
        val user = hashMapOf(
            "uid" to uid,
            "phoneNumber" to phoneNumber,
            "createdAt" to System.currentTimeMillis()
        )
        // Asumo que usas la colección 'usuarios'
        db.collection("usuarios").document(uid).set(user)
    }
}
