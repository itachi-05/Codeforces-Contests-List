package com.alpharays.newcalendar.models.crudviewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpharays.newcalendar.data.UserTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CreateTaskViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())

    fun postDataToFirebase(context: Context, date: String, tasks: UserTask) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseCall(context, date, tasks)
            firebaseAddTaskKey(context, date)
        }
    }

    private suspend fun firebaseCall(context: Context, date: String, tasks: UserTask) {
        try {
            databaseReference.child(date).push().setValue(tasks)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                    } else {
                        val exception = task.exception
                        // handle error
                        Log.i("ERROR: 110", exception.toString())
                    }
                }
        } catch (e: Exception) {
            // handle error
            Log.i("ERROR: 111", "Inside catch block $e")
        }
    }

    private suspend fun firebaseAddTaskKey(context: Context, date: String) {
        try {
            val db = databaseReference.child(date).get().await()
            for (data in db.children) {
                val key = data.key
                val updates = HashMap<String, Any>()
                updates["taskKey"] = key.toString()
                databaseReference.child(date).child(key.toString()).updateChildren(updates)
            }
        } catch (e: Exception) {
            // handle error
            Log.i("ERROR: 112", "Inside catch block $e")
        }
    }

}
