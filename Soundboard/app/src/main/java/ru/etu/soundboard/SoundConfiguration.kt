package ru.etu.soundboard

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import ru.etu.soundboard.Adapter.FileManager

class SoundConfiguration : AppCompatActivity() {
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    var presets = manager.getConf()
    var cur_set = presets?.set1
    var set_id = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_configuration)
        val key11 = findViewById<ImageButton>(R.id.key_1_1)
        val key12 = findViewById<ImageButton>(R.id.key_1_2)
        val key13 = findViewById<ImageButton>(R.id.key_1_3)
        val key14 = findViewById<ImageButton>(R.id.key_1_4)
        val key15 = findViewById<ImageButton>(R.id.key_1_5)
        val key21 = findViewById<ImageButton>(R.id.key_2_1)
        val key22 = findViewById<ImageButton>(R.id.key_2_2)
        val key23 = findViewById<ImageButton>(R.id.key_2_3)
        val key24 = findViewById<ImageButton>(R.id.key_2_4)
        val key25 = findViewById<ImageButton>(R.id.key_2_5)
        val key31 = findViewById<ImageButton>(R.id.key_3_1)
        val key32 = findViewById<ImageButton>(R.id.key_3_2)
        val key33 = findViewById<ImageButton>(R.id.key_3_3)
        val key34 = findViewById<ImageButton>(R.id.key_3_4)
        val key35 = findViewById<ImageButton>(R.id.key_3_5)
        swapImages()

        val buttonPlayer = findViewById<Button>(R.id.pageSoundboard)
        buttonPlayer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonDevs = findViewById<Button>(R.id.pageAboutDevs)
        buttonDevs.setOnClickListener {
            val intent = Intent(this, AboutDevs::class.java)
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

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

    }

    private fun swapImages() {
        val key11 = findViewById<ImageButton>(R.id.key_1_1)
        val key12 = findViewById<ImageButton>(R.id.key_1_2)
        val key13 = findViewById<ImageButton>(R.id.key_1_3)
        val key14 = findViewById<ImageButton>(R.id.key_1_4)
        val key15 = findViewById<ImageButton>(R.id.key_1_5)
        val key21 = findViewById<ImageButton>(R.id.key_2_1)
        val key22 = findViewById<ImageButton>(R.id.key_2_2)
        val key23 = findViewById<ImageButton>(R.id.key_2_3)
        val key24 = findViewById<ImageButton>(R.id.key_2_4)
        val key25 = findViewById<ImageButton>(R.id.key_2_5)
        val key31 = findViewById<ImageButton>(R.id.key_3_1)
        val key32 = findViewById<ImageButton>(R.id.key_3_2)
        val key33 = findViewById<ImageButton>(R.id.key_3_3)
        val key34 = findViewById<ImageButton>(R.id.key_3_4)
        val key35 = findViewById<ImageButton>(R.id.key_3_5)
        val set = cur_set?:return
        changeImage(key11, set.key11)
        changeImage(key12, set.key12)
        changeImage(key13, set.key13)
        changeImage(key14, set.key14)
        changeImage(key15, set.key15)
        changeImage(key21, set.key21)
        changeImage(key22, set.key22)
        changeImage(key23, set.key23)
        changeImage(key24, set.key24)
        changeImage(key25, set.key25)
        changeImage(key31, set.key31)
        changeImage(key32, set.key32)
        changeImage(key33, set.key33)
        changeImage(key34, set.key34)
        changeImage(key35, set.key35)
    }

    private fun changeImage(key: ImageButton, path: String){
        if(path == ""){
            key.setImageResource(R.drawable.vec_plus)
        }
        else{
            key.setImageResource(R.drawable.vec_trash_big)
        }
    }

    private fun saveConf(conf: FileManager.Configuration) {
        val prefs = mPrefs?:return
        val prefsEditor: Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(conf)
        prefsEditor.putString("Configuration", json)
        prefsEditor.apply()
    }
}