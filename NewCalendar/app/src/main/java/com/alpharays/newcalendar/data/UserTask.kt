package com.alpharays.newcalendar.data

data class UserTask(
    val taskKey: String? = "",
    val taskDate: String? = "",
    var taskName: String? = "",
    val taskStartTime: String? = "",
    val taskEndTime: String? = "",
    val taskVenue: String? = ""
)