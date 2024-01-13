package com.example.riffrift.BottomNavBar

import android.media.Image
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavBarItem (
    val label:String,
    val selected: ImageVector,
    val unselected: ImageVector,
    val screen: String
)