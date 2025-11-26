package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var btnContinuePhone: MaterialButton
    private lateinit var btnContinueGoogle: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configurar Google Sign-In
        setupGoogleSignIn()

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun setupGoogleSignIn() {
        // Configurar opciones de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Registrar el launcher para el resultado de Google Sign-In
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error: ${e.message}")
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initViews() {
        btnContinuePhone = findViewById(R.id.btnContinuePhone)
        btnContinueGoogle = findViewById(R.id.btnContinueGoogle)
    }

    private fun setupListeners() {
        btnContinuePhone.setOnClickListener {
            handlePhoneLogin()
        }

        btnContinueGoogle.setOnClickListener {
            handleGoogleLogin()
        }
    }

    private fun handlePhoneLogin() {
        val intent = Intent(this, PhoneLoginActivity::class.java)
        startActivity(intent)
    }

    private fun handleGoogleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account == null) {
            Toast.makeText(this, "Error: Cuenta de Google no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        // 🆕 Verificar si el usuario ya existe en Firestore
                        checkIfUserExistsAndNavigate(firebaseUser.uid, account)
                    }

                } else {
                    Toast.makeText(
                        this,
                        "Error en la autenticación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // 🆕 Nueva función para verificar si es primera vez
    private fun checkIfUserExistsAndNavigate(uid: String, account: GoogleSignInAccount) {
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
                        name = account.displayName ?: "",
                        email = account.email ?: "",
                        phone = "",
                        photoUrl = account.photoUrl?.toString() ?: "",
                        provider = "google",
                        createdAt = System.currentTimeMillis(),
                        lastLogin = System.currentTimeMillis(),
                        favoriteServices = emptyList()
                    )

                    saveNewUserAndNavigate(user)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al verificar usuario: ${e.message}")
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
                Toast.makeText(this, "¡Bienvenido ${user.name}!", Toast.LENGTH_SHORT).show()
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
        finish()
    }

    // 🆕 Navegar al Home
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}