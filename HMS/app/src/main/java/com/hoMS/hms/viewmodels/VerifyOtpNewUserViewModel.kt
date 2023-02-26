package com.hoMS.hms.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.data.LoginSuccess
import com.hoMS.hms.data.VerifyOtpRequest
import com.hoMS.hms.data.interfaces.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOtpNewUserViewModel : ViewModel() {
    private val _response = MutableLiveData<Boolean>()
    val response: LiveData<Boolean> get() = _response

    fun verifyOtp(view: View, verifyOtpRequest: VerifyOtpRequest) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                UserService.userInstance.verifyOtp(verifyOtpRequest)
                    .enqueue(object : Callback<LoginSuccess> {
                        override fun onResponse(
                            call: Call<LoginSuccess>,
                            response: Response<LoginSuccess>
                        ) {
                            if (response.isSuccessful) {
                                val resBody = response.body()
                                if (resBody?.status == "approved") _response.postValue(true)
                                else {
                                    _response.postValue(false)
                                    Snackbar.make(view, "Incorrect Otp", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                                Log.i("Response:VerifyOtp", response.body().toString())
                            } else {
                                _response.postValue(false)
                                Snackbar.make(view, "Incorrect Otp", Snackbar.LENGTH_SHORT).show()
                                Log.i("ResponseNotSuccessful:VerifyOtp", response.body().toString())
                            }
                        }

                        override fun onFailure(call: Call<LoginSuccess>, t: Throwable) {
                            _response.postValue(false)
                            Snackbar.make(view, "Incorrect Otp", Snackbar.LENGTH_SHORT).show()
                            Log.i("FAILURE:VerifyOtp", t.message.toString())
                        }
                    })
            }
        }
}