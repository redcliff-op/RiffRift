package com.example.riffrift.Retrofit

data class Data(
    val album: Album? = null,
    val artist: Artist? = null,
    val duration: Int? = 0,
    val explicit_lyrics: Boolean? = false,
    val preview: String? = "",
    val title: String? = "",
    val title_short: String? = "",

)