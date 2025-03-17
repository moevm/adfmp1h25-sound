package ru.etu.soundboard


import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ru.etu.soundboard.Model.SongModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Player : AppCompatActivity() {
    var position: Int = -1
    var uri: Uri = Uri.EMPTY
    var list: ArrayList<SongModel> = ArrayList()
    private lateinit var runnable: Runnable
    private lateinit var songName: TextView
    private lateinit var play_pause: ImageButton
    private lateinit var previous: ImageView
    private lateinit var next: ImageView
    private lateinit var share: ImageButton
    private lateinit var back: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var trash: ImageButton
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView

    private var handler = Handler()
    private var mediaPlayer: MediaPlayer? = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player)
        list = getAudioFiles()
        position = intent.getIntExtra("position", -1)
        songName = findViewById(R.id.textView)
        play_pause = findViewById(R.id.play)
        previous = findViewById(R.id.skip_back)
        next = findViewById(R.id.skip_ahead)
        back = findViewById(R.id.backButton)
        trash = findViewById(R.id.delete_track)
        share = findViewById(R.id.share)
        seekBar = findViewById(R.id.seekBar)
        startTime = findViewById(R.id.textView1)
        endTime = findViewById(R.id.textView2)

        uri = list[position].songUri
        setLayout(songName)

        playMedia(uri)

        play_pause.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                play_pause.setBackgroundResource(R.drawable.vec_play_circle)
                mediaPlayer!!.pause()
            } else {
                play_pause.setBackgroundResource(R.drawable.vec_play_pause)
                mediaPlayer!!.start()
            }
        }
        //next & previous Button
        next.setOnClickListener {
            nextPrevious(name = true)
        }
        previous.setOnClickListener {
            nextPrevious(name = false)
        }

        share.setOnClickListener{
            shareMenuOption()
        }



        trash.setOnClickListener{
            if (Environment.isExternalStorageManager()) {
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
                handler.removeCallbacks(runnable)
                finish()
                val intent = Intent(this, MyTracks::class.java)
                startActivity(intent)
                deleteFileByPath(uri.toString())
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${applicationContext.packageName}")
            startActivity(intent)
        }
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    val x = progress / 1000
                    mediaPlayer!!.seekTo(x)

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(seekBar!!.progress)
                    startTime.text = seekBar.progress.toString()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(seekBar!!.progress)
                }
            }

        })

        val buttonMain = findViewById<Button>(R.id.pageSoundboard)
        buttonMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonTracks = findViewById<Button>(R.id.pageMyTracks)
        buttonTracks.setOnClickListener {
            val intent = Intent(this, MyTracks::class.java)
            startActivity(intent)
        }

        val buttonConf = findViewById<Button>(R.id.pageConfigureSounds)
        buttonConf.setOnClickListener {
            val intent = Intent(this, SoundConfiguration::class.java)
            startActivity(intent)
        }

        val buttonDevs = findViewById<Button>(R.id.pageAboutDevs)
        buttonDevs.setOnClickListener {
            val intent = Intent(this, AboutDevs::class.java)
            startActivity(intent)
        }

        val buttonBack = findViewById<ImageButton>(R.id.backButton)
        buttonBack.setOnClickListener {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            handler.removeCallbacks(runnable)
            finish()
            val intent = Intent(this, MyTracks::class.java)
            startActivity(intent)
        }

        val buttonHelp = findViewById<Button>(R.id.pageHelp)
        buttonHelp.setOnClickListener {
            val intent = Intent(this, Help::class.java)
            startActivity(intent)
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    private fun deleteFileByPath(filePath: String) {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }

    private fun checkPosition(value: Int, increment: Boolean): Int {
        var return_value = 0

        if (increment) {
            if (value == list.size - 1) {
                return_value = 0
            } else {
                    return_value = ++position
                }
        } else {
            if (value == 0) {
                return_value = list.size - 1
            } else {
                    return_value = --position
            }
        }

        return return_value
    }


    private fun shareMenuOption() {
        val shareIntent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.type = "audio/*"
            this.putExtra(Intent.EXTRA_STREAM, list[position].songUri)
        }

        startActivity(Intent.createChooser(shareIntent, "Share this file using :"))
    }

    private fun setLayout(song: TextView) {
        song.text = list[position].name
    }


    fun getAudioFiles(): ArrayList<SongModel> {
        val list: ArrayList<SongModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val proj = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, //For Image
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATE_MODIFIED,
        )

        //Cursor is an interface where with the help of which we can access & write data
        // according to the requirement

        val audioCursor: Cursor? = contentResolver.query(
            uri,
            proj,
            null,
            null,
            null
        )

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    val songName: String =
                        audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    val artistName: String =
                        audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val url: String =
                        audioCursor.getString(audioCursor.getColumnIndex((MediaStore.Audio.Media.DATA)))
                    val duration =
                        audioCursor.getLong(audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val title =
                        audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val date =
                        audioCursor.getLong(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))

                    val path = Uri.parse(url)

                    val songModel = SongModel(path, songName, artistName, convertDurationToReadable(duration), path, convertTimestampToDate(date))

                    list.add(songModel)


                } while (audioCursor.moveToNext())
            }
        }

        // a List has sortby method in which we can sort a list of object with respect to a
        // parameter of the object, in this case it is sorted w.r.t. name
        list.sortBy { it.date }
        audioCursor?.close()

        return list
    }

    fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    fun convertDurationToReadable(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun timeFormat(duration: Int): String {
        val minutes = (duration % (1000 * 60 * 60) / (1000 * 60))
        val seconds = (duration % (1000 * 60 * 60) % (1000 * 60) / 1000)

        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun playMedia(uri: Uri) {
        setLayout(songName)
        try {
            if (mediaPlayer != null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(this@Player, uri)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    prepare()
                }
                mediaPlayer!!.start()
                adjustSeekBar(seekBar)
            }
        } catch (e: Exception) {
            Log.i("exception_PlayMedia", e.toString())
        }
    }

    private fun adjustSeekBar(seekBar: SeekBar) {
        seekBar.max = mediaPlayer!!.duration
        runnable = Runnable {
            seekBar.progress = mediaPlayer!!.currentPosition
            startTime.text = timeFormat(mediaPlayer!!.currentPosition)
            endTime.text = timeFormat(mediaPlayer!!.duration)

            mediaPlayer!!.setOnCompletionListener {
                nextPrevious(true)
            }

            handler.postDelayed(runnable, 300)

        }

        handler.postDelayed(runnable, 300)
    }

    private fun nextPrevious(name: Boolean) {
        if (name) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()

            position = checkPosition(position, true)
            playMedia(list[position].songUri)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()

            position = checkPosition(position, false)
            playMedia(list[position].songUri)
        }
    }
}