package ru.etu.soundboard

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import ru.etu.soundboard.Adapter.SongsAdapter
import ru.etu.soundboard.Model.SongModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import androidx.appcompat.widget.SearchView

class MyTracks : AppCompatActivity(), SideButton.SideButtonListener {
    private lateinit var linearLayoutManager: LinearLayoutManager
    var audioList: ArrayList<SongModel> = ArrayList()
    lateinit var search: SearchView
    lateinit var listView: RecyclerView
    lateinit var adapter: SongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.my_tracks)
        requestPermission()
        listView = findViewById(R.id.recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager
        audioList.addAll(getAudioFiles())

        adapter = SongsAdapter(audioList, this)
        listView.adapter = adapter
        listView.setHasFixedSize(true)
        setSupportActionBar(findViewById(R.id.toolbar))



        val buttonAboutDevs = findViewById<SideButton>(R.id.pageAboutDevs)
        val buttonConfigureSounds = findViewById<SideButton>(R.id.pageConfigureSounds)
        val buttonMyTracks = findViewById<SideButton>(R.id.pageMyTracks)
        val buttonHelp = findViewById<SideButton>(R.id.pageHelp)
        val buttonMain = findViewById<SideButton>(R.id.pageSoundboard)

        // Добавление обработчиков
        buttonAboutDevs.addListener(this)
        buttonConfigureSounds.addListener(this)
        buttonMyTracks.addListener(this)
        buttonHelp.addListener(this)
        buttonMain.addListener(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // Настройка SearchView
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongs(newText.orEmpty())
                return true
            }
        })

        return true
    }

    private fun filterSongs(query: String) {
        val filteredList = if (query.isEmpty()) {
            getAudioFiles() // Если запрос пустой, показываем все песни
        } else {
            audioList.filter {
                it.name.contains(query, ignoreCase = true) // Фильтруем по названию песни
            }
        }
        adapter.updateList(filteredList) // Обновляем список в адаптере
    }


    private fun requestPermission() {
        val permission = mutableListOf<String>()

        if (!hasPermission()) {
            permission.add(android.Manifest.permission.READ_MEDIA_AUDIO)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 8)
        }

    }
    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

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

    override fun onButtonDown(button: SideButton) {
        Log.d("MainActivity", "Button down: ${button.id}")
        when (button.id) {
            R.id.pageAboutDevs -> {
                Log.d("MainActivity", "About Devs button pressed")
                val intent = Intent(this, AboutDevs::class.java)
                startActivity(intent)
            }
            R.id.pageConfigureSounds -> {
                Log.d("MainActivity", "Configure Sounds button pressed")
                val intent = Intent(this, SoundConfiguration::class.java)
                startActivity(intent)
            }
            R.id.pageMyTracks -> {
                Log.d("MainActivity", "My Tracks button pressed")
                val intent = Intent(this, MyTracks::class.java)
                startActivity(intent)
            }
            R.id.pageSoundboard -> {
                Log.d("MainActivity", "About Devs button pressed")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.pageHelp -> {
                Log.d("MainActivity", "Help button pressed")
                val intent = Intent(this, Help::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onButtonUp(button: SideButton) {
        // Логика при отпускании кнопки (если нужна)
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
}

