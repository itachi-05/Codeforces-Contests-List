package com.alpharays.newcalendar.models.crudviewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpharays.newcalendar.data.UserTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UpdateTaskViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>().apply { value = true }
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
    private val data = MutableLiveData<List<UserTask>>()

    fun updateDataInFirebase(date: String, context: Context, key: String, task: UserTask) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseUpdateCall(context, date, key, task)
        }
    }

    private suspend fun firebaseUpdateCall(
        context: Context,
        date: String,
        key: String,
        task: UserTask
    ) {
        try {
            val db = databaseReference.child(date).get().await()
            val updates = HashMap<String, Any>()
            updates["taskKey"] = task.taskKey.toString(); updates["taskDate"] =
                task.taskDate.toString(); updates["taskName"] = task.taskName.toString()
            updates["taskStartTime"] = task.taskStartTime.toString(); updates["taskEndTime"] =
                task.taskEndTime.toString(); updates["taskVenue"] = task.taskVenue.toString()
            databaseReference.child(date).child(key).updateChildren(updates)
        } catch (e: Exception) {
            Log.i("Error firebase", e.toString())
        }
    }
}