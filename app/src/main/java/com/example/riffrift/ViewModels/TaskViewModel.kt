package com.example.riffrift.ViewModels

import android.os.Handler
import android.os.Looper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.riffrift.BottomNavBar.BottomNavBarItem
import com.example.riffrift.Retrofit.Data

class TaskViewModel : ViewModel() {
    var track by mutableStateOf<Data?>(null)
    var isPlaying by mutableStateOf(false)
    var mediaPlayer by mutableStateOf(android.media.MediaPlayer())
    var onLoop by mutableStateOf(false)
    var progress by mutableStateOf(0f)
    var duration by mutableStateOf(0)
    var currentPosition by mutableStateOf(0)

    fun initialiseBottomNavBar(): List<BottomNavBarItem> {
        return listOf(
            BottomNavBarItem("Stream", Icons.Filled.Search, Icons.Outlined.Search,"Stream"),
            BottomNavBarItem("Local", Icons.Filled.List, Icons.Outlined.List,"Local"),
            BottomNavBarItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings,"Settings")
        )
    }
    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun loadPlayer() {
        mediaPlayer.setDataSource(track?.preview ?: "")
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            mediaPlayer.isLooping = onLoop
            isPlaying = true
            duration = mediaPlayer.duration
            handler.post(updateProgressRunnable)
        }
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            progress = 0f
            handler.post(updateProgressRunnable)
        }
    }

    fun playPause() {
        if (!isPlaying) {
            mediaPlayer.start()
            mediaPlayer.isLooping = onLoop
        } else {
            mediaPlayer.pause()
            mediaPlayer.isLooping = onLoop
        }
        isPlaying = !isPlaying
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            currentPosition = mediaPlayer.currentPosition
            progress = currentPosition.toFloat() / duration.toFloat()
            handler.postDelayed(this, 100)
        }
        fun seekTo(position: Int) {
            mediaPlayer.seekTo(position)
        }
    }
}