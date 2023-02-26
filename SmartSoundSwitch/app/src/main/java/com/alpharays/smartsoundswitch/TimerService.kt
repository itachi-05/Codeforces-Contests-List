package com.alpharays.smartsoundswitch

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class TimerService : Service() {
    private lateinit var timer: CountDownTimer

    override fun onCreate() {
        super.onCreate()
        startTimer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val remainingTime = "$minutes:$seconds"
                val intent = Intent("TIMER_UPDATE")
                intent.putExtra("time_left", remainingTime)
                sendBroadcast(intent)
                Log.i("timerText", "Time Left: $remainingTime")
            }

            override fun onFinish() {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                Log.i("timerText", "Done")
                stopSelf()
            }
        }
        timer.start()
    }
}
