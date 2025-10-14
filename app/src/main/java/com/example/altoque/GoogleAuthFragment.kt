package com.example.altoque

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

// Importaciones de Google Sign-In y Firebase Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore // Para guardar datos

class GoogleAuthFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Código de resultado para la actividad de Google Sign-In
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_google_auth, container, false)
        auth = FirebaseAuth.getInstance()

        // 1. Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Usa tu ID de cliente web (definido en strings.xml por Firebase)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso) // Usar requireActivity()

        // 2. Enlazar botón y configurar listener
        val btnGoogle = view.findViewById<Button>(R.id.btn_google_sign_in)
        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    // Llama al Intent de Google para iniciar la UI de inicio de sesión
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Maneja la respuesta del Intent de Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtener la cuenta de Google (incluye el ID token)
                val account = task.getResult(ApiException::class.java)!!
                // Usar el token para autenticar con Firebase
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Fallo en Google Sign In (ej: el usuario canceló o hay un error de configuración)
                Toast.makeText(context, "Error de Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Usa el token de Google para iniciar sesión en Firebase
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Éxito: Usuario autenticado en Firebase
                    val user = auth.currentUser
                    saveUserData(user?.uid, user?.email) // Guarda en la BD

                    // Navegar a la pantalla principal llamando a la función de la Activity padre
                    (activity as? LoginActivity)?.navigateToMain()

                } else {
                    // Fallo en la autenticación con Firebase
                    Toast.makeText(context, "Fallo al autenticar con Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Guarda el usuario en Firestore (Base de Datos)
    private fun saveUserData(uid: String?, email: String?) {
        if (uid == null) return

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("usuarios").document(uid).set(user)
            .addOnSuccessListener { /* Log: Usuario guardado */ }
            .addOnFailureListener { /* Log: Error al guardar */ }
    }
}