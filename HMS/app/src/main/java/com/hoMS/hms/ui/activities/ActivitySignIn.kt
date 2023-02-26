package com.hoMS.hms.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.R
import com.hoMS.hms.data.LoginDetails
import com.hoMS.hms.data.VerifyOtpRequest
import com.hoMS.hms.databinding.ActivitySignInBinding
import com.hoMS.hms.viewmodels.LoginUserViewModel
import com.hoMS.hms.viewmodels.VerifyOtpNewUserViewModel


class ActivitySignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPref2: SharedPreferences
    private lateinit var sharedPref3: SharedPreferences
    private lateinit var userPhoneNumber: String
    private lateinit var userStatus: String
    private lateinit var verifyOtpNewUserViewModel: VerifyOtpNewUserViewModel
    private lateinit var loginUserViewModel: LoginUserViewModel
    private lateinit var savedLoginPhoneNumber: String
    private var otp = ""
    private var pwd = ""
    private var userNumber = ""
    private lateinit var snackbar: Snackbar

    override fun onStart() {
        super.onStart()
        Log.i("userStatus", userStatus)
        if (userStatus == "code#200") {
            val intent = Intent(this, ActivityMain::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        sharedPref = getSharedPreferences("sharingLoginDataUsingSP#01", MODE_PRIVATE)
        userPhoneNumber = sharedPref.getString(
            "userPhoneNumber",
            "001"
        ).toString()
        if (userPhoneNumber != "001") {
            binding.loginUserNumber.setText(userPhoneNumber)
        }

        sharedPref2 = getSharedPreferences("sharingLoginDataUsingSP#02", MODE_PRIVATE)
        userStatus = sharedPref2.getString("user_status", "404").toString()

//        TESTCODE : #02
//        binding.signInUserBtn.setOnClickListener {
//            startActivity(Intent(this,ActivityMain::class.java))
//        }

        binding.loginSendOtpBtn.setOnClickListener {
            if (binding.loginUserNumber.text?.length != 10) {
                snackbar = Snackbar.make(binding.root, "Invalid Number", Snackbar.LENGTH_SHORT)
                snackbar.setTextColor(ContextCompat.getColor(this, R.color.snackBarTextColor));
                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.snackBarBgColor));
                snackbar.show()
            } else {
                userNumber = "+91${binding.loginUserNumber.text.toString()}"
                checkingUser()
            }
        }


        // Logging In the user
        binding.signInUserBtn.setOnClickListener {
            if (binding.loginUserNumber.text?.length != 10) {
                val snackbar = Snackbar.make(binding.root, "Invalid Number", Snackbar.LENGTH_SHORT)
                snackbar.setTextColor(ContextCompat.getColor(this, R.color.snackBarTextColor));
                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.snackBarBgColor))
                snackbar.show()
            } else {
                val verifyOtpRequest = VerifyOtpRequest(userNumber, otp)
                verifyOtpNewUserViewModel =
                    ViewModelProvider(this)[VerifyOtpNewUserViewModel::class.java]
                verifyOtpNewUserViewModel.response.observe(this, Observer { validOtp ->
                    if (validOtp != null) {
                        if (validOtp == true) {
                            sharedPref3 =
                                getSharedPreferences("sharingLoginDataUsingSP#02", MODE_PRIVATE)
                            val editor = sharedPref3.edit()
                            editor.putString("code#02", "true")
                            editor.putString("userLoggedInNumber", userNumber)
                            editor.putString("user_status", "code#200")
                            editor.apply()
                            startActivity(Intent(this, ActivityMain::class.java))
                        }
                    }
                })
                verifyOtpNewUserViewModel.verifyOtp(binding.root, verifyOtpRequest)
            }
        }

    }

    private fun checkingUser() {
        binding.loginProgressBar.visibility = View.VISIBLE
        savedLoginPhoneNumber = "+91${binding.loginUserNumber.text.toString()}"
        val loginDetails = LoginDetails(pwd, savedLoginPhoneNumber)
        loginUserViewModel = ViewModelProvider(this)[LoginUserViewModel::class.java]
        loginUserViewModel.loggingInUser(binding.root, loginDetails)
        Handler(Looper.getMainLooper()).postDelayed({
            // Code to be executed after 3 seconds
            binding.loginProgressBar.visibility = View.GONE
        }, 2000)
    }

    private fun init() {
        textWatcherFun()
        movingToSignInPage()
        movingToForgotPasswordPage()
        binding.backToRegister.setOnClickListener {
            val intent = Intent(this@ActivitySignIn, ActivityRegister::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }

    private fun textWatcherFun() {
        binding.userPwdOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
                pwd = s.toString() // Get the updated text as a String.
                // Do something with the updated text, such as validate it or update a ViewModel.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        })
        binding.loginUserOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.
                otp = s.toString() // Get the updated text as a String.
                // Do something with the updated text, such as validate it or update a ViewModel.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        })

        binding.loginUserNumber.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }
        binding.userPwdOtp.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }
        binding.loginUserOtp.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }

    }

    private fun movingToSignInPage() {
        val ss = SpannableString("Not Registered? Register here")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@ActivitySignIn, ActivityRegister::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 25, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), 25, 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.notRegistered.text = ss
        binding.notRegistered.movementMethod = LinkMovementMethod.getInstance()
        binding.notRegistered.highlightColor = Color.TRANSPARENT
    }

    private fun movingToForgotPasswordPage() {
        val ss = SpannableString("Forgot password?")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@ActivitySignIn, ActivityForgotPassword::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.forgotPassword.text = ss
        binding.forgotPassword.movementMethod = LinkMovementMethod.getInstance()
        binding.forgotPassword.highlightColor = Color.TRANSPARENT
    }
}