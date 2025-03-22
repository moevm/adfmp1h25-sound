package ru.etu.soundboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.etu.soundboard.Adapter.SongsAdapter
import ru.etu.soundboard.Model.SongModel
import java.time.LocalDate

class MyTracks : AppCompatActivity(), SideButton.SideButtonListener {
    private lateinit var linearLayoutManager: LinearLayoutManager
    var audioList: ArrayList<SongModel> = ArrayList()
    lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.my_tracks)
        listView = findViewById(R.id.recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager
        audioList.addAll(getAudioFiles())

        val adapter = SongsAdapter(audioList, this)
        listView.adapter = adapter
        listView.setHasFixedSize(true)

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

    fun getAudioFiles(): ArrayList<SongModel> {
        val list: ArrayList<SongModel> = ArrayList()
            var a = SongModel("cool song " , LocalDate.parse("2018-12-12"), 5.5)
            var a2 = SongModel("cool song 2" , LocalDate.parse("2018-12-12"), 5.5)
            list.add(a)
            list.add(a2)
        list.sortBy { it.name }
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
}

