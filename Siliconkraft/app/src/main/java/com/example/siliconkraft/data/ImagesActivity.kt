package com.example.siliconkraft.data

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siliconkraft.R
import com.example.siliconkraft.SplashScreenVM
import com.example.siliconkraft.databinding.ActivityImagesBinding
import com.example.siliconkraft.ui.ActivitySignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ImagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagesBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var mList = mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sharedPref: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private lateinit var builderLogOut: AlertDialog.Builder
    private var displayName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        getImages()
        drawerFunction()
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

    private fun initVars() {
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImagesAdapter(mList)
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages(){
        binding.progressBar.visibility = View.VISIBLE
        firebaseFirestore.collection("images")
            .get().addOnSuccessListener {
                for(i in it){
                    mList.add(i.data["pic"].toString())
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
    }

}