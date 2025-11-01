package com.example.omg

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnChangePhoto: ImageButton
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvProvider: TextView
    private lateinit var btnEditServices: View
    private lateinit var btnLogout: View

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            if (imageUri != null) {
                uploadProfilePhoto()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        initViews()
        loadUserData()
        setupListeners()
    }

    private fun initViews() {
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvProvider = findViewById(R.id.tvProvider)
        btnEditServices = findViewById(R.id.btnEditServices)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Usuario"
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val provider = document.getString("provider") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""

                    tvName.text = name.ifEmpty { "Usuario" }
                    tvEmail.text = email.ifEmpty { "Sin correo" }
                    tvPhone.text = phone.ifEmpty { "Sin teléfono" }
                    tvProvider.text = when (provider) {
                        "google" -> "Google"
                        "phone" -> "Teléfono"
                        else -> "Desconocido"
                    }

                    // Cargar foto
                    if (photoUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_user)
                            .into(ivProfilePhoto)
                    }
                }
            }
    }

    private fun setupListeners() {
        btnChangePhoto.setOnClickListener {
            selectImage()
        }

        btnEditServices.setOnClickListener {
            // Ir a editar servicios
            val intent = Intent(this, EditServicesActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun uploadProfilePhoto() {
        val userId = auth.currentUser?.uid ?: return
        val uri = imageUri ?: return

        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show()

        val storageRef = storage.reference.child("profile_photos/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    updatePhotoUrl(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al subir foto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updatePhotoUrl(photoUrl: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .update("photoUrl", photoUrl)
            .addOnSuccessListener {
                Glide.with(this)
                    .load(photoUrl)
                    .into(ivProfilePhoto)
                Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}