package com.alpharays.smartsoundswitch

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import com.alpharays.smartsoundswitch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timerText: TextView
    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val remainingTime = intent.getStringExtra("time_left")
            timerText.text = "Time Left: $remainingTime"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerText = findViewById(R.id.timerText)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        } else {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentRingerMode = audioManager.ringerMode
            if (currentRingerMode != AudioManager.RINGER_MODE_SILENT) {
                val serviceIntent = Intent(this, TimerService::class.java)
                startService(serviceIntent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("TIMER_UPDATE")
        registerReceiver(timerUpdateReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(timerUpdateReceiver)
    }
}