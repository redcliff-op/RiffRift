package com.example.riffrift

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import com.example.riffrift.Retrofit.Data
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.riffrift.Repository.Repository
import com.example.riffrift.Retrofit.RetrofitInstance
import com.example.riffrift.ViewModel.RetrofitViewModel
import com.example.riffrift.ViewModels.BottomNavBarViewModel
import com.example.riffrift.ViewModels.SettingsViewModel
import com.example.riffrift.ViewModels.StreamScreenViewModel
import com.example.riffrift.ui.theme.RiffRiftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),Color.Transparent.toArgb()
            )
        )
        val repository = Repository(RetrofitInstance)
        val retrofitViewModel = RetrofitViewModel(repository)
        val settingsViewModel = SettingsViewModel()
        setContent {
            RiffRiftTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ){
                    if(!settingsViewModel.pitchBlackTheme){
                        Image(
                            painter = painterResource(id = R.drawable.bgfinal),
                            contentDescription = null,
                            modifier = Modifier.size(2000.dp),
                            contentScale = ContentScale.Crop
                        )
                    }else{
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Black
                        ){}
                    }
                    BottomNavBar(
                        retrofitViewModel = retrofitViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavBar(
    retrofitViewModel: RetrofitViewModel,
    bottomNavBarViewModel: BottomNavBarViewModel = BottomNavBarViewModel(),
    streamScreenViewModel: StreamScreenViewModel = StreamScreenViewModel(),
    settingsViewModel: SettingsViewModel
){
    val bottomNavBarList = bottomNavBarViewModel.initialiseBottomNavBar()
    val navController = rememberNavController()
    Scaffold (
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent
            ){
                bottomNavBarList.forEachIndexed{index, item ->
                    NavigationBarItem(
                        selected = index == bottomNavBarViewModel.selected,
                        onClick = {
                            navController.navigate(item.screen)
                            bottomNavBarViewModel.selected = index
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if(index == bottomNavBarViewModel.selected){
                                        item.selected
                                    }else{
                                        item.unselected
                                    },
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        label = { Text(text = item.label)}
                    )
                }
            }
        }
    ){
        NavHost(navController = navController, startDestination = "Stream"){
            composable(route = "Stream"){
                StreamScreen(
                    retrofitViewModel = retrofitViewModel,
                    streamScreenViewModel = streamScreenViewModel
                )
            }
            composable(route = "Local"){
                Local()
            }
            composable(route = "Settings"){
                Settings(
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }
}

@Composable
fun StreamScreen(
    retrofitViewModel: RetrofitViewModel,
    streamScreenViewModel: StreamScreenViewModel
) {
    LaunchedEffect(retrofitViewModel.query) {
        if (retrofitViewModel.query.isNotEmpty()) {
            retrofitViewModel.fetchData(retrofitViewModel.query)
        }
    }
    val trackData = retrofitViewModel.trackData.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(15.dp))
        OutlinedTextField(
            value = retrofitViewModel.query,
            onValueChange = { newQuery -> retrofitViewModel.query = newQuery },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            label = { Text(text = "Search")},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    retrofitViewModel.query += " "
                }
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.size(10.dp))
        if (trackData != null && trackData.data.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 85.dp)
            ){
                items(trackData.data) { track ->
                    TrackCard(
                        track = track,
                        streamScreenViewModel = streamScreenViewModel
                    )
                }
            }
        } else {
            Text(
                text = "Search a Track, Artist or an Album",
                fontSize = 40.sp,
                color = Color.White,
                lineHeight = 50.sp,
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun Local(

){

}

@Composable
fun Settings(
    settingsViewModel: SettingsViewModel
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Settings",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Pitch Black Dark Theme",
                fontSize = 25.sp,
                color = Color.White
            )
            Switch(
                checked = settingsViewModel.pitchBlackTheme,
                onCheckedChange = {
                    settingsViewModel.pitchBlackTheme = !settingsViewModel.pitchBlackTheme
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackCard(
    track: Data,
    streamScreenViewModel: StreamScreenViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            GlideImage(
                model = track.album.cover,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = streamScreenViewModel.formatTime(track.duration),
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}