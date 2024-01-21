package com.example.riffrift.ViewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riffrift.Auth.GoogleAuthUiClient
import com.example.riffrift.Retrofit.Data
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FirebaseViewModel(
    googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    var curTrack by mutableStateOf<Data?>(null)
    private val db = FirebaseDatabase.getInstance().getReference(googleAuthUiClient.getSignedInUser()?.userId ?: "")
    var isLikedUI by mutableStateOf(true)

    fun updateLikedStatus(){
        isLikedUI = data.value?.contains(curTrack) == true
    }

    private val _data = MutableLiveData<List<Data>>()
    val data: LiveData<List<Data>> get() = _data

    fun fetchTrackDataFromFirebase(){
        viewModelScope.launch {
            db.child("trackList").get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    _data.value = dataSnapshot.getValue<MutableList<Data>>()
                }
            }.addOnSuccessListener {
                updateLikedStatus()
            }
        }
    }

    fun updateLikedTrack() {
        viewModelScope.launch {
            val currentData = data.value.orEmpty().toMutableList()
            if (currentData.contains(curTrack)) {
                currentData.remove(curTrack)
            } else {
                currentData.add(curTrack!!)
            }
            db.child("trackList").setValue(currentData).addOnSuccessListener {
                fetchTrackDataFromFirebase()
            }
        }
    }
}
