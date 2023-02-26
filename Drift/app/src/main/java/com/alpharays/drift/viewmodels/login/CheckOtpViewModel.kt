package com.alpharays.drift.viewmodels.login

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class CheckOtpViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var auth: FirebaseAuth
    private val _response = MutableLiveData<Boolean>()
    val response: LiveData<Boolean> get() = _response
    private lateinit var sharedPreferences: SharedPreferences

    fun verifyOtp(view: View, context: Context, otp: String, typedOtp: String) =
        viewModelScope.launch {
            auth = FirebaseAuth.getInstance()
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typedOtp)
            signInWithPhoneAuthCredential(view, context, credential)
        }

    private fun signInWithPhoneAuthCredential(
        view: View,
        context: Context,
        credential: PhoneAuthCredential
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    // Navigate to Home Page
                    _response.postValue(true)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Log.d(
                        ContentValues.TAG,
                        "signInWithPhoneAuthCredential: ${task.exception.toString()}"
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.i("enter valid",": verification code")
//                        Snackbar.make(view, "Enter valid Verification Code", Snackbar.LENGTH_SHORT)
//                            .show()
                    }
                    // Update UI
                    _response.postValue(false)
                }
            }
    }
}