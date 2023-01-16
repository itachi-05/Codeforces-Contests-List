package com.example.siliconkraft.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import com.example.siliconkraft.R
import com.example.siliconkraft.SplashScreenVM
import com.example.siliconkraft.data.ImagesActivity
import com.example.siliconkraft.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess


class ActivityHome : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sharedPref: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private val viewModel: SplashScreenVM by viewModels()
    private lateinit var builderLogOut: AlertDialog.Builder
    private var displayName: String = ""
    private lateinit var fstore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        storageRef = storageRef.child(auth.currentUser?.uid.toString())
        binding.postButton.setOnClickListener {
            resultLauncher.launch("image/*")
        }
        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(this, ImagesActivity::class.java))
        }
    }

    private fun initVars() {
        onBackPress()
        auth = FirebaseAuth.getInstance()
        drawerFunction()
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        fstore = FirebaseFirestore.getInstance()
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finishAffinity()
                    exitProcess(0)
                }
            }
        })
    }

    private fun drawerFunction() {
        sharedPref = getSharedPreferences("sharingUSER", MODE_PRIVATE)
        displayName = sharedPref.getString("displayName", "User").toString()
        val drawerLayout = binding.drawerLayout
        binding.navBarButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            val navView = binding.navView
            findViewById<TextView>(R.id.navHeaderUserName).text = displayName
            toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.isDrawerSlideAnimationEnabled = true
            toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.Log_out -> {
                        confirmingLogOut()
                    }
                    R.id.About -> {
                        Toast.makeText(this, "Soon", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }

    private fun confirmingLogOut() {
        builderLogOut = AlertDialog.Builder(this)
        builderLogOut.setTitle(getString(R.string.alert_message))
            .setMessage("Do you wish to Log Out?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->   // dialogInterface, it
                logout()
            }
            .setNegativeButton("No") { dialogInterface, _ ->     // dialogInterface, it
                dialogInterface.cancel()
            }
            .show()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, ActivitySignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
        startActivity(intent)
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        storageRef = storageRef.child("img")
        uploadImage()
//        binding.imgView.setImageURI(it)
    }


    private fun uploadImage() {
        imageUri?.let {
            // compressing image
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
            val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()
            storageRef.putBytes(reducedImage).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()

                        fstore.collection("images").add(map)
                            .addOnCompleteListener { fireStoreTask ->

                                if (fireStoreTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Uploaded Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        this,
                                        fireStoreTask.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
//                                binding.progressBar.visibility = View.GONE
                                binding.imgView.setImageResource(R.drawable.google)

                            }
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
//                    binding.progressBar.visibility = View.GONE
                    binding.imgView.setImageResource(R.drawable.google)
                }
            }
        }
    }
}