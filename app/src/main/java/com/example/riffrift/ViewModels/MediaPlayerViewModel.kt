package com.example.riffrift.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MediaPlayerViewModel : ViewModel() {
    var isPlaying by mutableStateOf(false)
}