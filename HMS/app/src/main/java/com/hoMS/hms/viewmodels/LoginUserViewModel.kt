package com.hoMS.hms.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.data.LoginDetails
import com.hoMS.hms.data.LoginSuccess
import com.hoMS.hms.data.interfaces.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginUserViewModel : ViewModel() {
    private val _otpResponse = MutableLiveData<Boolean>()
    val otpResponse: LiveData<Boolean> get() = _otpResponse

    fun loggingInUser(view: View, loginDetails: LoginDetails) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            UserService.userInstance.sendOtpToLoginUser(loginDetails)
                .enqueue(object : Callback<LoginSuccess> {
                    override fun onResponse(
                        call: Call<LoginSuccess>,
                        response: Response<LoginSuccess>
                    ) {
                        Log.i("CheckingResponse",response.body().toString())
                        if (response.isSuccessful) {
                            _otpResponse.postValue(true)
                            Snackbar.make(view,"Success, OTP Sent",Snackbar.LENGTH_SHORT).show()
                            Log.i("resBody1", response.body().toString())
                        } else {
                            _otpResponse.postValue(false)
                            Log.i("failureResponse:NotNewUserVM", response.raw().toString())
                            Snackbar.make(view,"Invalid number/password",Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginSuccess>, t: Throwable) {
                        _otpResponse.postValue(false)
                        Snackbar.make(view,"Server Error",Snackbar.LENGTH_SHORT).show()
                        Log.i("FAILURE1:NotNewUserVM", t.message.toString())
                    }
                })
        }
    }
}