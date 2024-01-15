package com.example.riffrift

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.riffrift.Retrofit.Data
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.riffrift.Repository.Repository
import com.example.riffrift.Retrofit.RetrofitInstance
import com.example.riffrift.ViewModel.RetrofitViewModel
import com.example.riffrift.ViewModels.TaskViewModel
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
        val taskViewModel = TaskViewModel(retrofitViewModel)
        setContent {
            RiffRiftTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ){
                    if(!taskViewModel.pitchBlackTheme){
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
                        taskViewModel = taskViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavBar(
    retrofitViewModel: RetrofitViewModel,
    taskViewModel: TaskViewModel
){
    val bottomNavBarList = taskViewModel.initialiseBottomNavBar()
    val navController = rememberNavController()
    Scaffold (
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent
            ){
                bottomNavBarList.forEachIndexed{index, item ->
                    NavigationBarItem(
                        selected = index == taskViewModel.selected,
                        onClick = {
                            navController.navigate(item.screen)
                            taskViewModel.selected = index
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if(index == taskViewModel.selected){
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
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = taskViewModel.track != null && !taskViewModel.isOnPlayScreen,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ){
                FloatingActionButton(
                    onClick = {
                        navController.navigate("play")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(4f),
                    containerColor = Color(0xFF695885),
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ){
                            GlideImage(
                                model = taskViewModel.track!!.album?.cover_medium,
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                taskViewModel.track!!.title_short?.let {
                                    Text(
                                        text = it,
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                taskViewModel.track!!.artist?.name?.let {
                                    Text(
                                        text = it,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            IconButton(
                                onClick = {
                                    taskViewModel.playPause()
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter =
                                    if(!taskViewModel.isPlaying)
                                        painterResource(id = R.drawable.playbutton)
                                    else
                                        painterResource(id = R.drawable.pausebutton),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ){
        NavHost(navController = navController, startDestination = "Stream"){
            composable(route = "Stream"){
                StreamScreen(
                    retrofitViewModel = retrofitViewModel,
                    taskViewModel = taskViewModel,
                    navController = navController,
                )
            }
            composable(route = "Local"){
                Local()
            }
            composable(route = "Settings"){
                Settings(
                    taskViewModel = taskViewModel,
                )
            }
            composable(route = "Play"){
                PlayScreen(
                    taskViewModel = taskViewModel,
                    navController = navController,
                )
            }
            composable(route = "Details"){
                TrackDetails(
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}

@Composable
fun StreamScreen(
    retrofitViewModel: RetrofitViewModel,
    taskViewModel: TaskViewModel,
    navController : NavController,
) {
    LaunchedEffect(retrofitViewModel.query) {
        if (retrofitViewModel.query.isNotBlank()) {
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
            label = {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold
                )
            },
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
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White
            )
        )
        Spacer(modifier = Modifier.size(10.dp))
        if (trackData != null && trackData.data!!.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 85.dp)
            ){
                itemsIndexed(trackData.data) { index, track ->
                    TrackCard(
                        index = index,
                        track = track,
                        taskViewModel = taskViewModel,
                        navController = navController,
                    )
                }
            }
        } else {
            Row (
                modifier = Modifier.fillMaxSize(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    elevation =  CardDefaults.elevatedCardElevation(100.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "Search a Track, Artist or an Album",
                            fontSize = 30.sp,
                            color = Color.White,
                            lineHeight = 50.sp,
                            modifier = Modifier.padding(30.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Local(

){

}

@Composable
fun Settings(
    taskViewModel: TaskViewModel
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
                checked = taskViewModel.pitchBlackTheme,
                onCheckedChange = {
                    taskViewModel.pitchBlackTheme = !taskViewModel.pitchBlackTheme
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackDetails(
    taskViewModel: TaskViewModel
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 185.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Track Details",
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
        ){
            GlideImage(
                model = taskViewModel.track?.artist?.picture_xl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.Start,
        ){
            Text(
                text = "Artist : ${taskViewModel.track?.artist?.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Album : ${taskViewModel.track?.album?.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Explicit : ${taskViewModel.track?.explicit_lyrics}",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrackCard(
    index: Int,
    track: Data,
    taskViewModel: TaskViewModel,
    navController: NavController,
){
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        onClick = {
            taskViewModel.currentTrackIndex = index
            if(taskViewModel.track==track){
                navController.navigate("Play")
            }else{
                taskViewModel.track = track
                if(!taskViewModel.isPlaying){
                    taskViewModel.mediaPlayer.reset()
                    taskViewModel.loadPlayer()
                }else{
                    taskViewModel.playPause()
                    taskViewModel.mediaPlayer.reset()
                    taskViewModel.loadPlayer()
                }
            }
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            GlideImage(
                model = track.album?.cover,
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
                    text = taskViewModel.formatTime(track.duration),
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                track.title?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    if(track.explicit_lyrics == true){
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 5.dp),
                        ){
                            Text(
                                text = "E",
                                color = Color.White,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                    track.artist?.name?.let {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlayScreen(
    taskViewModel: TaskViewModel,
    navController: NavController,
){
    DisposableEffect(Unit){
        taskViewModel.isOnPlayScreen = true
        onDispose {
            taskViewModel.isOnPlayScreen = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 85.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = {
                    navController.navigate("Stream")
                },
                modifier = Modifier.size(40.dp)
            ){
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Now Playing",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            IconButton(
                onClick = {
                    navController.navigate("Details")
                },
                modifier = Modifier.size(40.dp)
            ){
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            GlideImage(
                model = taskViewModel.track?.album?.cover_xl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
            )
        }
        Row (
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = taskViewModel.track?.title_short ?:"",
                    fontSize = 25.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = taskViewModel.track?.artist?.name ?:"",
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = {
                        taskViewModel.onLoop = !taskViewModel.onLoop
                        taskViewModel.mediaPlayer.isLooping = !taskViewModel.mediaPlayer.isLooping
                    },
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.loopicon),
                        contentDescription = null,
                        tint =
                            if(taskViewModel.onLoop)
                                Color.Cyan
                            else
                                Color.White,
                        modifier = Modifier.fillMaxSize(),

                    )
                }
                Text(
                    text = "Loop",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
        LinearProgressIndicator(
            progress = taskViewModel.progress,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(8.dp)
                .clip(CircleShape),
            color = Color.White
        )
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(
                onClick = {
                    taskViewModel.previousTrack()
                },
                modifier = Modifier.size(100.dp)
            ){
                Icon(
                    painter =
                    painterResource(id = R.drawable.previous),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
            IconButton(
                onClick = {
                    taskViewModel.playPause()
                },
                modifier = Modifier.size(100.dp)
            ){
                Icon(
                    painter =
                    if(!taskViewModel.isPlaying)
                        painterResource(id = R.drawable.playbutton)
                    else
                        painterResource(id = R.drawable.pausebutton),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
            IconButton(
                onClick = {
                    taskViewModel.nextTrack()
                },
                modifier = Modifier.size(100.dp)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
    }
}