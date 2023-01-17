package com.alpharays.newcalendar.models.crudviewmodel

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

class ReadTaskViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>().apply { value = true }
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())
    private val data = MutableLiveData<List<UserTask>>()
    private val datesList = MutableLiveData<List<String>>()

    fun getDataFromFirebase(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(firebaseCall(date))
            datesList.postValue(firebaseDatesCall())
        }
    }

    private suspend fun firebaseCall(date: String): List<UserTask> {
        return try {
            val snapshot = databaseReference.child(date).get().await()
            val dataList = mutableListOf<UserTask>()
            snapshot.children.forEach {
                val data = it.getValue(UserTask::class.java)
                dataList.add(data!!)
            }
            dataList
        } catch (e: Exception) {
            Log.i("Error firebase", e.toString())
            emptyList()
        }
    }

    fun getData1(): LiveData<List<UserTask>> {
        return data
    }

    private suspend fun firebaseDatesCall(): List<String>? {
        return try {
            val dbRef = databaseReference.get().await()
            val dataList = mutableListOf<String>()
            for (data in dbRef.children) {
                dataList.add(data.key.toString())
            }
            dataList
        } catch (e: Exception) {
            Log.i("Error Dates Firebase", e.toString())
            emptyList()
        }
    }

    fun getData2(): LiveData<List<String>> {
        return datesList
    }

}
