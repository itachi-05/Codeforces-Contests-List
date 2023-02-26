package com.alpharays.drift.viewmodels.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Display.Mode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SendingOtpViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response
    private lateinit var sharedPreferences: SharedPreferences

    fun sendingOtp(view: View, context: Context, number: String) = viewModelScope.launch {
        auth = FirebaseAuth.getInstance()
        withContext(Dispatchers.IO) {
            Log.i("A3","A3")
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                        signInWithPhoneAuthCredential(context,credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            if (e is FirebaseAuthInvalidCredentialsException) {
                                Log.i("failedOtpVerification", e.toString())
                                Snackbar.make(
                                    view,
                                    "Invalid Number, try again",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                _response.postValue("010")
                            } else if (e is FirebaseTooManyRequestsException) {
                                Log.i("TooManyRequests", "SMS quota for the project exceeded")
                                Snackbar.make(
                                    view,
                                    "Error: Contact Developer",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                _response.postValue("010")
                            }
                            Log.i("A4","A4")
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            sharedPreferences = context.getSharedPreferences(
                                "sharingLoginOTPUsingSP#01",
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val editor = sharedPreferences.edit()
                            editor.putString("user_otp", verificationId)
                            editor.apply()
                            Snackbar.make(view, "Success, Otp Sent", Snackbar.LENGTH_SHORT).show()
                            Log.i("verificationID1",verificationId)
                            _response.postValue(verificationId)
                            Log.i("A5","A5")
                        }
                    }
                )
                .build()
            try {
                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                Log.e("MyApp", "Error occurred during phone verification: ${e.message}")
            }
        }
    }
}