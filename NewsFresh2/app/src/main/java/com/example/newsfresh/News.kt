package com.example.newsfresh

data class News(
    val id: Int,
    val name: String,
    val phase: String,
    val durationSeconds: Int,
    val startTimeSeconds: Int,
    val relativeTimeSeconds: Int,
    var remTime: Int,
    var checkFlag: Int
)