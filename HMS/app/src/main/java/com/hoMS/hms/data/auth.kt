package com.hoMS.hms.data


data class LoginSuccess(
    val to: String,
    val status: String,
    val valid: Boolean
)

data class ApiResponse(
    val success: Boolean,
    val data: Any?
)




data class NotInDB(
    val flag: Boolean,
    val message: String
)


data class LoginFailure(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)


data class VerifyOtpRequest(
    val phoneNo: String,
    val otp: String
)

data class OtpRequestReceived(
    val to: String,
    val status: String,
    val valid: String
)


data class RegisterUser(
    val email: String,
    val password: String,
    val number: String,

)

data class RegisterUserReceived(
    val email: String,
    val password: String,
    val number: String,
    val status: Boolean
)


data class LoginDetails(
    val password : String,
    val mobileNo : String
)