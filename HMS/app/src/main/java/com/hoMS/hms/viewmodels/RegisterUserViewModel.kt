package com.hoMS.hms.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.R
import com.hoMS.hms.data.RegisterUser
import com.hoMS.hms.data.RegisterUserReceived
import com.hoMS.hms.data.interfaces.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserViewModel : ViewModel() {
    private val _response = MutableLiveData<Boolean>()

    val response: LiveData<Boolean> get() = _response

    fun registerUser(view: View,context: Context, email: String, password: String, number: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                UserService.userInstance.registerUser(
                    RegisterUser(email, password, number)
                )
                    .enqueue(object : Callback<RegisterUserReceived> {
                        override fun onResponse(
                            call: Call<RegisterUserReceived>,
                            response: Response<RegisterUserReceived>
                        ) {
                            if (response.isSuccessful) {
                                _response.postValue(true)
                                Log.i("true", response.body().toString())
                            } else {
                                _response.postValue(false)
                                Log.i("User already exists", "User already exists")
                            }
                        }

                        override fun onFailure(call: Call<RegisterUserReceived>, t: Throwable) {
                            Log.i("API Failure", t.message.toString())
                        }
                    })
            }
        }
}