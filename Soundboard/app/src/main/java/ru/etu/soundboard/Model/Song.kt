package ru.etu.soundboard.Model

import android.net.Uri
import java.time.LocalDate
import java.util.Date

data class SongModel(
  //  var image: Uri,
    var name: String,
    var date: LocalDate,
   // var artist: String,
  //  var data: String,
    var duration: Double,
  //  var songUri: Uri
) {

}