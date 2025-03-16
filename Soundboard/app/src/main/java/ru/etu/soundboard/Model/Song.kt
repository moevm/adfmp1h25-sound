package ru.etu.soundboard.Model

import android.net.Uri
import java.time.LocalDate
import java.util.Date

data class SongModel(
    var image: Uri,
    var name: String,
    var artist: String,
    var data: String,
    var duration: String,
    var songUri: Uri,
    var date: String
) {
    //A constructor is made with selective parameters so that whenever we need can make an
    // object of the song even if we have very few parameters.
    constructor(image: Uri, name: String, artist: String, duration: String, songUri: Uri, date: String) : this(
        image = image,
        name = name,
        artist = artist,
        data = "",
        duration = duration,
        songUri = songUri,
        date = date
    ) { }
}