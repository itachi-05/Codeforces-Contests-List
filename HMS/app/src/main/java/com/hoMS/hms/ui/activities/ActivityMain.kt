package com.hoMS.hms.ui.activities

import android.animation.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hoMS.hms.R
import com.hoMS.hms.databinding.ActivityMainBinding
import com.hoMS.hms.repos.MyAdapter
import com.hoMS.hms.repos.NotificationAdapter
import java.util.*


class ActivityMain : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var myViewPager: ViewPager2
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: ArrayList<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("sharingLoginDataUsingSP#02", MODE_PRIVATE)
        val signInCheck = sharedPref.getString("code#02", "false").toString()

        if (signInCheck == "true") {
            snackbar = Snackbar.make(binding.root, "Welcome", Snackbar.LENGTH_SHORT)
            snackbar.setTextColor(ContextCompat.getColor(this, R.color.snackBarTextColor));
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.snackBarBgColor))
            snackbar.show()
        }

        init()
        init2()

        rotatingNotifications()
    }

    private fun init() {
        // handling pages using swipe -> viewpager
        tabLayout = findViewById(R.id.myTabLayout)
        myViewPager = findViewById(R.id.MyViewPager)
        myViewPager.adapter = MyAdapter(this)
        TabLayoutMediator(tabLayout, myViewPager) { tab, index ->
            tab.text = when (index) {
                0 -> {
                    "Home"
                }
                1 -> {
                    "Appointment"
                }
                2 -> {
                    "Digital Health Card"
                }
                3 -> {
                    "Settings"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    private fun init2() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (myViewPager.currentItem == 0) {
                    moveTaskToBack(true)
                    finish()
                } else {
                    myViewPager.currentItem = 0
                }
            }
        })
    }

    private fun rotatingNotifications() {
        val rotate = ObjectAnimator.ofFloat(binding.userNotifications, "rotation", 0f, 10f)
        rotate.duration = 250
        rotate.repeatCount = 3
        rotate.interpolator = LinearInterpolator()
        rotate.repeatMode = ObjectAnimator.REVERSE

        rotate.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.userNotifications.rotation = 0f
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.play(rotate)

        binding.userNotifications.setOnClickListener {
            showingNotifications()
            animatorSet.start()
        }
    }

    @SuppressLint("CutPasteId")
    private fun showingNotifications() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.notification_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            575,
            1000,
            true
        )

        popupWindow.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
        )

        popupWindow.animationStyle = R.style.PopupAnimation

        // Dummy Data
        popupView.findViewById<RecyclerView>(R.id.userNotificationsRecyclerView).layoutManager =
            LinearLayoutManager(this)

        notificationList = ArrayList()
        val a = "Hello, Ganesh"
        val b = "Hello, Shivang"
        val c = "Hello, Nayeem"
        val d = "Hello, Anvita"

        notificationList.add(a); notificationList.add(b); notificationList.add(c); notificationList.add(
            d
        );

        notificationAdapter = NotificationAdapter(notificationList)
        popupView.findViewById<RecyclerView>(R.id.userNotificationsRecyclerView).adapter =
            notificationAdapter
        popupWindow.showAtLocation(popupView, Gravity.TOP or Gravity.START, 20, 140)

    }
}
