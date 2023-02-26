package com.hoMS.hms.data.interfaces

import com.hoMS.hms.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "http://192.168.1.38:9120/api/"

//
interface UserInterface {
    @POST("mobile/sendotp")
    fun sendOtp(@Body phoneNumber: String): Call<LoginSuccess>

    @POST("mobile/verifyotp")
    fun verifyOtp(@Body verifyOtpRequest : VerifyOtpRequest): Call<LoginSuccess>

    @POST("mobile/register")
    fun registerUser(@Body registerUser: RegisterUser): Call<RegisterUserReceived>

    @POST("mobile/checkMobile")
    fun sendOtpNewUser(@Body phoneNumber: HashMap<String, String>): Call<LoginSuccess>

    @POST("mobile/signin")
    fun sendOtpToLoginUser(@Body loginDetails: LoginDetails): Call<LoginSuccess>

}

object UserService{
    val userInstance: UserInterface
    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        userInstance = retrofit.create(UserInterface::class.java)
    }
}