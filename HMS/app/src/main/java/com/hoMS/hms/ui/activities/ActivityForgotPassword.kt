package com.hoMS.hms.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.hoMS.hms.R
import com.hoMS.hms.databinding.ActivityForgotPasswordBinding

class ActivityForgotPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var myDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        binding.fPswdContinueBtn.setOnClickListener{
            myDialog = Dialog(this)
            myDialog?.setContentView(R.layout.set_new_password)
            myDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog?.findViewById<ImageView>(R.id.dialogClose)?.setOnClickListener {
                myDialog?.dismiss()
            }
            myDialog?.setCancelable(false)
            myDialog?.setCanceledOnTouchOutside(false)
            myDialog?.show()
        }
        binding.backToLogin.setOnClickListener{
            val intent = Intent(this@ActivityForgotPassword, ActivitySignIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }
    }
}