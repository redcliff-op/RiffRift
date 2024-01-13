package com.example.riffrift.ViewModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.riffrift.BottomNavBar.BottomNavBarItem

class BottomNavBarViewModel : ViewModel() {
    var selected by mutableIntStateOf(0)
    fun initialiseBottomNavBar(): List<BottomNavBarItem>{
        return listOf(
            BottomNavBarItem("Stream",Icons.Filled.Search,Icons.Outlined.Search,"Stream"),
            BottomNavBarItem("Local",Icons.Filled.List,Icons.Outlined.List,"Local"),
            BottomNavBarItem("Settings",Icons.Filled.Settings,Icons.Outlined.Settings,"Settings")
        )
    }
}