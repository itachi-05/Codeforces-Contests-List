package com.alpharays.drift.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alpharays.drift.data.UserDetails
import com.alpharays.drift.databinding.ActivityLoginBinding
import com.alpharays.drift.viewmodels.login.CheckOtpViewModel
import com.alpharays.drift.viewmodels.login.SendingOtpViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var userPhoneNumber = ""
    private lateinit var sharedPref: SharedPreferences
    private var enteredOtp = ""

    // permissions
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    //    private var isRecordPermissionGranted = false
    private var isReadContactsPermissionGranted = false
    private var isWriteContactsPermissionGranted = false
    private var isReadPermissionGranted = false
    private var isDialPermissionGranted = false
    private val permissionList: MutableList<String> = ArrayList()

    private lateinit var sendingOtpViewModel: SendingOtpViewModel
    private lateinit var checkOtpViewModel: CheckOtpViewModel
    private var otpToBeChecked = ""

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        auth = FirebaseAuth.getInstance()
        binding.loginSendOtpBtn.setOnClickListener {
            binding.loginProgressBar.visibility = View.VISIBLE
            if (binding.loginUserNumber.text?.length != 10) {
                Snackbar.make(binding.root, "Invalid Number", Snackbar.LENGTH_SHORT).show()
            } else {
                sendingOtpViewModel = ViewModelProvider(this)[SendingOtpViewModel::class.java]
                sendingOtpViewModel.sendingOtp(
                    binding.root,
                    this,
                    "+91${binding.loginUserNumber.text.toString()}"
                )
                userPhoneNumber = "+91${binding.loginUserNumber.text.toString()}"
            }
            binding.loginProgressBar.visibility = View.GONE
        }

        binding.loginUserBtn.setOnClickListener {
            if (userPhoneNumber == "" || userPhoneNumber.length != 13) {
                Snackbar.make(binding.root, "Enter valid Number", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sharedPref = getSharedPreferences("sharingLoginOTPUsingSP#01", MODE_PRIVATE)
            val verificationId = sharedPref.getString("user_otp", "").toString()
//            Log.i("verificationID12", verificationId)

            if (verificationId.isNotEmpty() && enteredOtp.isNotEmpty()) {
                Log.i("verificationId", verificationId)
                Log.i("enteredOtp", enteredOtp)
                checkOtpViewModel = ViewModelProvider(this)[CheckOtpViewModel::class.java]
                checkOtpViewModel.response.observe(this, Observer {
                    if (it != null) {
                        if (it == true) {
                            database = FirebaseDatabase.getInstance().getReference("Users")
                            val userDetails = UserDetails("", userPhoneNumber, "")
                            addingUserToDatabase(database, userPhoneNumber.substring(3,13), userDetails)
                            Snackbar.make(binding.root, "Validated", Snackbar.LENGTH_SHORT).show()
                            sharedPref =
                                getSharedPreferences("sharingLoginOTPUsingSP#01", MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("user_otp", "")
                            editor.apply()
                            binding.loginUserNumber.setText("")
                            binding.loginUserOtp.setText("")
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Snackbar.make(binding.root, "Invalid Otp", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                })
                checkOtpViewModel.verifyOtp(
                    binding.root,
                    this,
                    verificationId,
                    enteredOtp
                )
            } else {
                Snackbar.make(binding.root, "Invalid Otp", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addingUserToDatabase(
        myDatabase: DatabaseReference,
        myUserPhoneNumber: String,
        userDetails: UserDetails
    ) {
        // adding basic details to database
        myDatabase.child(myUserPhoneNumber).get().addOnSuccessListener {
            if (!it.exists()) {
                myDatabase.child(myUserPhoneNumber).setValue(userDetails).addOnSuccessListener {
                    Log.i("user_this", "Successfully Saved")
                }.addOnFailureListener { itt ->
                    Log.i("user_save_failed", itt.message.toString())
                }
            }
        }.addOnFailureListener {
            Toast.makeText(baseContext, "Try again later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init() {
//        requestingPermissions()
        textWatcherFun()
    }

    private fun textWatcherFun() {
        binding.loginUserOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
                enteredOtp = s.toString() // Get the updated text as a String.
                // Do something with the updated text, such as validate it or update a ViewModel.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        })
        binding.loginUserOtp.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }

    }

    private fun requestingPermissions() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isReadPermissionGranted =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGranted
                isWriteContactsPermissionGranted =
                    permissions[android.Manifest.permission.WRITE_CONTACTS]
                        ?: isWriteContactsPermissionGranted
                isReadContactsPermissionGranted =
                    permissions[android.Manifest.permission.READ_CONTACTS]
                        ?: isReadContactsPermissionGranted
                isDialPermissionGranted =
                    permissions[android.Manifest.permission.CALL_PHONE] ?: isDialPermissionGranted
            }
        requestPermission()
    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isReadContactsPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        isWriteContactsPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        isDialPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED


        if (!isReadPermissionGranted) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isReadContactsPermissionGranted) {
            permissionList.add(android.Manifest.permission.READ_CONTACTS)
        }
        if (!isWriteContactsPermissionGranted) {
            permissionList.add(android.Manifest.permission.WRITE_CONTACTS)
        }
        if (!isDialPermissionGranted) {
            permissionList.add(android.Manifest.permission.CALL_PHONE)
        }

        if (permissionList.isNotEmpty()) {
            permissionLauncher.launch(permissionList.toTypedArray())
        }

    }
}