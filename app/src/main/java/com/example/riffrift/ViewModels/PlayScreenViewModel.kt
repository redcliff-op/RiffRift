package com.example.riffrift.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.riffrift.Retrofit.Data

class PlayScreenViewModel : ViewModel() {
    var track by mutableStateOf<Data?>(null)
    var playButton by mutableStateOf(true)
}