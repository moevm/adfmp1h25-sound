package ru.etu.soundboard

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.WindowInsets.Side
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ru.etu.soundboard.Adapter.FileManager
import java.io.File

class SoundConfiguration : AppCompatActivity(), SideButton.SideButtonListener,SideImageButton.SideButtonListener {
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    var presets = manager.getConf()
    var cur_set = presets?.set1
    var cur_key = ""
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()

        if(uri != null) {
            uri.path?.let { Log.d("MainActivity", it) }
//            uri.path
//            val cursor = contentResolver.query(uri, null, null, null, null)
//            val nameIndex = cursor?.getColumnIndex(MediaStore.Audio.Media.DATA);
//            cursor?.moveToFirst()
//            val fileName = nameIndex?.let { cursor.getString(it) }
//            if (fileName != null) {
//                Log.d("MainActivity", fileName)
//            }
//            contentResolver.apply {
//                query(uri, null, null, null, null)?.use { cursor ->
//                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    cursor.moveToFirst()
//                    cursor.getString(nameIndex)
//                }?.let { fileName ->
//                    Log.d("MainActivity", fileName)
//                }
//            }
        }
        Log.d("MainActivity", File(path).name)
        if(uri != null){
            setButton!!.setImageResource(R.drawable.vec_trash_big)
            when (cur_key){
                "key11" -> cur_set!!.key11 = path
                "key12" -> cur_set!!.key12 = path
                "key13" -> cur_set!!.key13 = path
                "key14" -> cur_set!!.key14 = path
                "key15" -> cur_set!!.key15 = path
                "key21" -> cur_set!!.key21 = path
                "key22" -> cur_set!!.key22 = path
                "key23" -> cur_set!!.key23 = path
                "key24" -> cur_set!!.key24 = path
                "key25" -> cur_set!!.key25 = path
                "key31" -> cur_set!!.key31 = path
                "key32" -> cur_set!!.key32 = path
                "key33" -> cur_set!!.key33 = path
                "key34" -> cur_set!!.key34 = path
                "key35" -> cur_set!!.key35 = path
            }
        }
        cur_key = ""
    }
    var setButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_configuration)

        mPrefs = getPreferences(MODE_PRIVATE)

        val key11 = findViewById<ImageButton>(R.id.key_1_1)
        key11.setOnClickListener {
            if(cur_set!!.key11 != ""){ key11.setImageResource(R.drawable.vec_plus); cur_set!!.key11 = ""
            } else { setButton = key11; cur_key = "key11"; getContent.launch("audio/*") } }
        val key12 = findViewById<ImageButton>(R.id.key_1_2)
        key12.setOnClickListener {
            if(cur_set!!.key12 != ""){ key12.setImageResource(R.drawable.vec_plus); cur_set!!.key12 = ""
            } else { setButton = key12; cur_key = "key12"; getContent.launch("audio/*") } }
        val key13 = findViewById<ImageButton>(R.id.key_1_3)
        key13.setOnClickListener {
            if(cur_set!!.key13 != ""){ key13.setImageResource(R.drawable.vec_plus); cur_set!!.key13 = ""
            } else { setButton = key13; cur_key = "key13"; getContent.launch("audio/*") } }
        val key14 = findViewById<ImageButton>(R.id.key_1_4)
        key14.setOnClickListener {
            if(cur_set!!.key14 != ""){ key14.setImageResource(R.drawable.vec_plus); cur_set!!.key14 = ""
            } else { setButton = key14; cur_key = "key14"; getContent.launch("audio/*") } }
        val key15 = findViewById<ImageButton>(R.id.key_1_5)
        key15.setOnClickListener {
            if(cur_set!!.key15 != ""){ key15.setImageResource(R.drawable.vec_plus); cur_set!!.key15 = ""
            } else { setButton = key15; cur_key = "key15"; getContent.launch("audio/*") } }
        val key21 = findViewById<ImageButton>(R.id.key_2_1)
        key21.setOnClickListener {
            if(cur_set!!.key21 != ""){ key21.setImageResource(R.drawable.vec_plus); cur_set!!.key21 = ""
            } else { setButton = key21; cur_key = "key21"; getContent.launch("audio/*") } }
        val key22 = findViewById<ImageButton>(R.id.key_2_2)
        key22.setOnClickListener {
            if(cur_set!!.key22 != ""){ key22.setImageResource(R.drawable.vec_plus); cur_set!!.key22 = ""
            } else { setButton = key22; cur_key = "key22"; getContent.launch("audio/*") } }
        val key23 = findViewById<ImageButton>(R.id.key_2_3)
        key23.setOnClickListener {
            if(cur_set!!.key23 != ""){ key23.setImageResource(R.drawable.vec_plus); cur_set!!.key23 = ""
            } else { setButton = key23; cur_key = "key23"; getContent.launch("audio/*") } }
        val key24 = findViewById<ImageButton>(R.id.key_2_4)
        key24.setOnClickListener {
            if(cur_set!!.key24 != ""){ key24.setImageResource(R.drawable.vec_plus); cur_set!!.key24 = ""
            } else { setButton = key24; cur_key = "key24"; getContent.launch("audio/*") } }
        val key25 = findViewById<ImageButton>(R.id.key_2_5)
        key25.setOnClickListener {
            if(cur_set!!.key25 != ""){ key25.setImageResource(R.drawable.vec_plus); cur_set!!.key25 = ""
            } else { setButton = key25; cur_key = "key25"; getContent.launch("audio/*") } }
        val key31 = findViewById<ImageButton>(R.id.key_3_1)
        key31.setOnClickListener {
            if(cur_set!!.key31 != ""){ key31.setImageResource(R.drawable.vec_plus); cur_set!!.key31 = ""
            } else { setButton = key31; cur_key = "key31"; getContent.launch("audio/*") } }
        val key32 = findViewById<ImageButton>(R.id.key_3_2)
        key32.setOnClickListener {
            if(cur_set!!.key32 != ""){ key32.setImageResource(R.drawable.vec_plus); cur_set!!.key32 = ""
            } else { setButton = key32; cur_key = "key32"; getContent.launch("audio/*") } }
        val key33 = findViewById<ImageButton>(R.id.key_3_3)
        key33.setOnClickListener {
            if(cur_set!!.key33 != ""){ key33.setImageResource(R.drawable.vec_plus); cur_set!!.key33 = ""
            } else { setButton = key33; cur_key = "key33"; getContent.launch("audio/*") } }
        val key34 = findViewById<ImageButton>(R.id.key_3_4)
        key34.setOnClickListener {
            if(cur_set!!.key34 != ""){ key34.setImageResource(R.drawable.vec_plus); cur_set!!.key34 = ""
            } else { setButton = key34; cur_key = "key34"; getContent.launch("audio/*") } }
        val key35 = findViewById<ImageButton>(R.id.key_3_5)
        key35.setOnClickListener {
            if(cur_set!!.key35 != ""){ key35.setImageResource(R.drawable.vec_plus); cur_set!!.key35 = ""
            } else { setButton = key35; cur_key = "key35"; getContent.launch("audio/*") } }
        swapImages()

        val set1 = findViewById<SideImageButton>(R.id.set1)
        set1.addListener(this)
        val set2 = findViewById<SideImageButton>(R.id.set2)
        set2.addListener(this)
        val set3 = findViewById<SideImageButton>(R.id.set3)
        set3.addListener(this)

        val saveButton = findViewById<SideImageButton>(R.id.saveButton)
        saveButton.addListener(this)

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

    override fun onButtonDown(button: SideImageButton) {
        Log.d("MainActivity", "Button down: ${button.id}")
        when (button.id) {
            R.id.set1 -> {
                cur_set = presets?.set1
                swapImages()
            }
            R.id.set2 -> {
                cur_set = presets?.set2
                swapImages()
            }
            R.id.set3 -> {
                cur_set = presets?.set3
                swapImages()
            }
            R.id.saveButton -> {
                Log.d("MainActivity", "About Devs button pressed")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onButtonUp(button: SideImageButton) {
        // Логика при отпускании кнопки (если нужна)
    }
}