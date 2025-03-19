package ru.etu.soundboard

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import ru.etu.soundboard.Adapter.FileManager
import android.media.MediaPlayer
import android.widget.TextView
import ru.etu.soundboard.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mPrefs: SharedPreferences? = null

    private lateinit var cOne_sound: MediaPlayer
    private lateinit var dOne_sound: MediaPlayer

    private lateinit var btnKey_1_1: TextView
    private lateinit var btnKey_1_2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = getPreferences(MODE_PRIVATE)

        val manager = FileManager
        var presets = manager.getConf()

        if(presets == null) {
            val conf = readConf()
            if (conf == null) {
                val data: String = manager.readFile(baseContext)
                val gson = Gson()
                val obj: FileManager.Configuration =
                    gson.fromJson(data, FileManager.Configuration::class.java)
                manager.setConf(obj)
                saveConf(obj)
            }
            else {
                manager.setConf(conf)
            }
            presets = manager.getConf()
        } else {
            manager.getConf()?.let { saveConf(it) }
        }
//        manager.setPreference(mPrefs)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonDevs = findViewById<Button>(R.id.pageAboutDevs)
        buttonDevs.setOnClickListener {
            val intent = Intent(this, AboutDevs::class.java)
            startActivity(intent)
        }

        val buttonConf = findViewById<Button>(R.id.pageConfigureSounds)
        buttonConf.setOnClickListener {
            val intent = Intent(this, SoundConfiguration::class.java)
            startActivity(intent)
        }

        val buttonMyTracks = findViewById<Button>(R.id.pageMyTracks)
        buttonMyTracks.setOnClickListener {
            val intent = Intent(this, MyTracks::class.java)
            startActivity(intent)
        }
        
        val buttonHelp = findViewById<Button>(R.id.pageHelp)
        buttonHelp.setOnClickListener {
            val intent = Intent(this, Help::class.java)
            startActivity(intent)
        }


        btnKey_1_1 = findViewById(R.id.key_1_1)
        btnKey_1_2 = findViewById(R.id.key_1_2)

        cOne_sound = MediaPlayer.create(this, R.raw.c1)
        dOne_sound = MediaPlayer.create(this, R.raw.d1)

        btnKey_1_1.setOnClickListener {
            soundPlayKey(cOne_sound)
        }

        btnKey_1_2.setOnClickListener {
            soundPlayKey(dOne_sound)
        }
        

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    private fun saveConf(conf: FileManager.Configuration) {
        val prefs = mPrefs?:return
        val prefsEditor: Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(conf)
        prefsEditor.putString("Configuration", json)
        prefsEditor.apply()
    }

    private fun readConf() : FileManager.Configuration? {
        val prefs = mPrefs?:return null
        val gson = Gson()
        val json = prefs.getString("Configuration", "")
        if (json == ""){return null}
        val obj: FileManager.Configuration = gson.fromJson(json, FileManager.Configuration::class.java)
        return obj
    }

    private fun soundPlayKey(sound : MediaPlayer) {
        sound.start()
    }
}
