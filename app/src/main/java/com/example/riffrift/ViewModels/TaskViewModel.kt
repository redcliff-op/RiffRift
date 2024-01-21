package com.example.riffrift.ViewModels

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.riffrift.BottomNavBar.BottomNavBarItem
import com.example.riffrift.Retrofit.Data
import com.example.riffrift.ViewModel.RetrofitViewModel

class TaskViewModel (val retrofitViewModel: RetrofitViewModel) : ViewModel() {
    var track by mutableStateOf<Data?>(null)
    var isPlaying by mutableStateOf(false)
    var mediaPlayer by mutableStateOf(android.media.MediaPlayer())
    var onLoop by mutableStateOf(false)
    var progress by mutableStateOf(0f)
    var duration by mutableStateOf(0)
    var currentPosition by mutableStateOf(0)
    var pitchBlackTheme by mutableStateOf(false)
    var selected by mutableIntStateOf(0)
    var isOnPlayScreen by mutableStateOf(false)
    var currentTrackIndex by mutableStateOf(0)
    var isSignedIn by mutableStateOf(false)

    fun initialiseBottomNavBar(): List<BottomNavBarItem> {
        return listOf(
            BottomNavBarItem("Search", Icons.Filled.Search, Icons.Outlined.Search,"Stream"),
            BottomNavBarItem("Liked", Icons.Filled.List, Icons.Outlined.List,"Liked"),
            BottomNavBarItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings,"Settings")
        )
    }
    fun formatTime(totalSeconds: Int?): String {
        val minutes = totalSeconds?.div(60)
        val seconds = totalSeconds?.rem(60)
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

    fun nextTrack(){
        if(!((currentTrackIndex == (retrofitViewModel.trackData.value?.size ?: 1)-1))){
            mediaPlayer.reset()
            track = retrofitViewModel.trackData.value?.get(++currentTrackIndex)
            loadPlayer()
        }
    }

    fun previousTrack(){
        if(!(currentTrackIndex==0)){
            mediaPlayer.reset()
            track = retrofitViewModel.trackData.value?.get(--currentTrackIndex)
            loadPlayer()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun launchEQ(context: Context){
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context,"No Default Equalizer app on the Device",Toast.LENGTH_SHORT).show()
        }
    }
}