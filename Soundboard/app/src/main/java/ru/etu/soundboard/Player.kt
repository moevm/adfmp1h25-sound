package ru.etu.soundboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Player : AppCompatActivity(),SideButton.SideButtonListener,SideImageButton.SideButtonListener {
    var songName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songName = intent.getStringExtra("name")
        setContentView(R.layout.player)
        val textView: TextView = findViewById(R.id.textView) as TextView
        textView.text = songName

        val buttonAboutDevs = findViewById<SideButton>(R.id.pageAboutDevs)
        val buttonConfigureSounds = findViewById<SideButton>(R.id.pageConfigureSounds)
        val buttonMyTracks = findViewById<SideButton>(R.id.pageMyTracks)
        val buttonHelp = findViewById<SideButton>(R.id.pageHelp)
        val buttonMain = findViewById<SideButton>(R.id.pageSoundboard)

        buttonAboutDevs.addListener(this)
        buttonConfigureSounds.addListener(this)
        buttonMyTracks.addListener(this)
        buttonHelp.addListener(this)
        buttonMain.addListener(this)

        val buttonBack = findViewById<SideImageButton>(R.id.backButton)
        buttonBack.addListener(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    override fun onButtonDown(button: SideButton) {
        Log.d("MainActivity", "Button down: ${button.id}")
        when (button.id) {
            R.id.pageAboutDevs -> {
                Log.d("MainActivity", "About Devs button pressed")
                val intent = Intent(this, AboutDevs::class.java)
                startActivity(intent)
            }
            R.id.pageSoundboard -> {
                Log.d("MainActivity", "About Devs button pressed")
                val intent = Intent(this, MainActivity::class.java)
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

    override fun onButtonDown(button: SideImageButton) {
        Log.d("MainActivity", "Button down: ${button.id}")
        when (button.id) {
            R.id.backButton -> {
                val intent = Intent(this, MyTracks::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onButtonUp(button: SideImageButton) {
        // Логика при отпускании кнопки (если нужна)
    }
}