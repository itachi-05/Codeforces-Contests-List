package com.hoMS.hms.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hoMS.hms.R
import com.hoMS.hms.data.LoginSuccess
import com.hoMS.hms.data.interfaces.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotNewUserViewModel : ViewModel() {
    fun notNewUser(view: View, context: Context, hMap: HashMap<String, String>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            UserService.userInstance.sendOtpNewUser(hMap)
                .enqueue(object : Callback<LoginSuccess> {
                    override fun onResponse(
                        call: Call<LoginSuccess>,
                        response: Response<LoginSuccess>
                    ) {
                        if (response.isSuccessful) {
                            val resBody1 = response.body()
                            if (resBody1?.to == null) {
                                val snackbar = Snackbar.make(view,"User already registered",Snackbar.LENGTH_SHORT)
                                snackbar.setTextColor(ContextCompat.getColor(context, R.color.snackBarTextColor));
                                snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.snackBarBgColor))
                                snackbar.show()
                            } else {
                                val snackbar = Snackbar.make(view,"OTP Sent",Snackbar.LENGTH_SHORT)
                                snackbar.setTextColor(ContextCompat.getColor(context, R.color.snackBarTextColor));
                                snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.snackBarBgColor))
                                snackbar.show()
                            }
                            Log.i("resBody1", resBody1.toString())
                        } else {
                            Log.i("failureResponse:NotNewUserVM", response.raw().toString())
                            val snackbar = Snackbar.make(view,"Too Many Requests...",Snackbar.LENGTH_SHORT)
                            snackbar.setTextColor(ContextCompat.getColor(context, R.color.snackBarTextColor));
                            snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.snackBarBgColor))
                            snackbar.show()
                        }
                    }

                    override fun onFailure(call: Call<LoginSuccess>, t: Throwable) {
                        val snackbar = Snackbar.make(view,"Server not Running",Snackbar.LENGTH_SHORT)
                        snackbar.setTextColor(ContextCompat.getColor(context, R.color.snackBarTextColor));
                        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.snackBarBgColor))
                        snackbar.show()
                        Log.i("FAILURE1:NotNewUserVM", t.message.toString())
                    }
                })
        }
    }
}