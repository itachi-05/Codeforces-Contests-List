package com.alpharays.foodie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alpharays.foodie.databinding.ActivityHomeBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAI()
    }

    private fun openAI() {
        val client = OkHttpClient()

        val apiKey = "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"
        val prompt = "The text prompt that you want to generate a response for"

        val mediaType: MediaType = "application/json".toMediaTypeOrNull()!!
        val body: RequestBody = RequestBody.create(
            mediaType,
            "{\"prompt\":\"$prompt\",\"max_tokens\":50,\"temperature\":0.7,\"n\":1,\"stop\":[\"\n\"]}"
        )

        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/engines/davinci-codex/completions")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response: Response = client.newCall(request).execute()
            val responseData: String = response.body!!.string()
            Log.i("checkingResponseData", responseData)
            // Parse the response data and display it in your application
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}