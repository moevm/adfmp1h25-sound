package ru.etu.soundboard

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.etu.soundboard.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cOne_sound: MediaPlayer
    private lateinit var dOne_sound: MediaPlayer

    private lateinit var btnKey_1_1: TextView
    private lateinit var btnKey_1_2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun soundPlayKey(sound : MediaPlayer) {
        sound.start()
    }

}
