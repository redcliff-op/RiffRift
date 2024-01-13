package com.example.riffrift.ViewModels

import androidx.lifecycle.ViewModel

class StreamScreenViewModel : ViewModel() {
    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}