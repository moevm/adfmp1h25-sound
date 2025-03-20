package ru.etu.soundboard

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ru.etu.soundboard.Adapter.FileManager

class SoundConfiguration : AppCompatActivity() {
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    var presets = manager.getConf()
    var cur_set = presets?.set1
    val getContent1 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key11 = path }
    }
    val getContent2 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key12 = path }
    }
    val getContent3 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key13 = path }
    }
    val getContent4 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key14 = path }
    }
    val getContent5 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key15 = path }
    }
    val getContent6 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key21 = path }
    }
    val getContent7 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key22 = path }
    }
    val getContent8 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key23 = path }
    }
    val getContent9 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key24 = path }
    }
    val getContent10 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key25 = path }
    }
    val getContent11 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key31 = path }
    }
    val getContent12 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key32 = path }
    }
    val getContent13 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key33 = path }
    }
    val getContent14 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key34 = path }
    }
    val getContent15 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val path = uri.toString()
        if(uri != null) { setButton!!.setImageResource(R.drawable.vec_trash_big); cur_set!!.key35 = path }
    }
    var setButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_configuration)

        mPrefs = getPreferences(MODE_PRIVATE)

        val key11 = findViewById<ImageButton>(R.id.key_1_1)
        key11.setOnClickListener {
            if(cur_set!!.key11 != ""){ key11.setImageResource(R.drawable.vec_plus); cur_set!!.key11 = ""
            } else { setButton = key11; getContent1.launch("audio/*") } }
        val key12 = findViewById<ImageButton>(R.id.key_1_2)
        key12.setOnClickListener {
            if(cur_set!!.key12 != ""){ key12.setImageResource(R.drawable.vec_plus); cur_set!!.key12 = ""
            } else { setButton = key12; getContent2.launch("audio/*") } }
        val key13 = findViewById<ImageButton>(R.id.key_1_3)
        key13.setOnClickListener {
            if(cur_set!!.key13 != ""){ key13.setImageResource(R.drawable.vec_plus); cur_set!!.key13 = ""
            } else { setButton = key13; getContent3.launch("audio/*") } }
        val key14 = findViewById<ImageButton>(R.id.key_1_4)
        key14.setOnClickListener {
            if(cur_set!!.key14 != ""){ key14.setImageResource(R.drawable.vec_plus); cur_set!!.key14 = ""
            } else { setButton = key14; getContent4.launch("audio/*") } }
        val key15 = findViewById<ImageButton>(R.id.key_1_5)
        key15.setOnClickListener {
            if(cur_set!!.key15 != ""){ key15.setImageResource(R.drawable.vec_plus); cur_set!!.key15 = ""
            } else { setButton = key15; getContent5.launch("audio/*") } }
        val key21 = findViewById<ImageButton>(R.id.key_2_1)
        key21.setOnClickListener {
            if(cur_set!!.key21 != ""){ key21.setImageResource(R.drawable.vec_plus); cur_set!!.key21 = ""
            } else { setButton = key21; getContent6.launch("audio/*") } }
        val key22 = findViewById<ImageButton>(R.id.key_2_2)
        key22.setOnClickListener {
            if(cur_set!!.key22 != ""){ key22.setImageResource(R.drawable.vec_plus); cur_set!!.key22 = ""
            } else { setButton = key22; getContent7.launch("audio/*") } }
        val key23 = findViewById<ImageButton>(R.id.key_2_3)
        key23.setOnClickListener {
            if(cur_set!!.key23 != ""){ key23.setImageResource(R.drawable.vec_plus); cur_set!!.key23 = ""
            } else { setButton = key23; getContent8.launch("audio/*") } }
        val key24 = findViewById<ImageButton>(R.id.key_2_4)
        key24.setOnClickListener {
            if(cur_set!!.key24 != ""){ key24.setImageResource(R.drawable.vec_plus); cur_set!!.key24 = ""
            } else { setButton = key24; getContent9.launch("audio/*") } }
        val key25 = findViewById<ImageButton>(R.id.key_2_5)
        key25.setOnClickListener {
            if(cur_set!!.key25 != ""){ key25.setImageResource(R.drawable.vec_plus); cur_set!!.key25 = ""
            } else { setButton = key25; getContent10.launch("audio/*") } }
        val key31 = findViewById<ImageButton>(R.id.key_3_1)
        key31.setOnClickListener {
            if(cur_set!!.key31 != ""){ key31.setImageResource(R.drawable.vec_plus); cur_set!!.key31 = ""
            } else { setButton = key31; getContent11.launch("audio/*") } }
        val key32 = findViewById<ImageButton>(R.id.key_3_2)
        key32.setOnClickListener {
            if(cur_set!!.key32 != ""){ key32.setImageResource(R.drawable.vec_plus); cur_set!!.key32 = ""
            } else { setButton = key32; getContent12.launch("audio/*") } }
        val key33 = findViewById<ImageButton>(R.id.key_3_3)
        key33.setOnClickListener {
            if(cur_set!!.key33 != ""){ key33.setImageResource(R.drawable.vec_plus); cur_set!!.key33 = ""
            } else { setButton = key33; getContent13.launch("audio/*") } }
        val key34 = findViewById<ImageButton>(R.id.key_3_4)
        key34.setOnClickListener {
            if(cur_set!!.key34 != ""){ key34.setImageResource(R.drawable.vec_plus); cur_set!!.key34 = ""
            } else { setButton = key34; getContent14.launch("audio/*") } }
        val key35 = findViewById<ImageButton>(R.id.key_3_5)
        key35.setOnClickListener {
            if(cur_set!!.key35 != ""){ key35.setImageResource(R.drawable.vec_plus); cur_set!!.key35 = ""
            } else { setButton = key35; getContent15.launch("audio/*") } }
        swapImages()

        val set1 = findViewById<ImageButton>(R.id.set1)
        set1.setOnClickListener{
            cur_set = presets?.set1
            swapImages()
        }
        val set2 = findViewById<ImageButton>(R.id.set2)
        set2.setOnClickListener{
            cur_set = presets?.set2
            swapImages()
        }
        val set3 = findViewById<ImageButton>(R.id.set3)
        set3.setOnClickListener{
            cur_set = presets?.set3
            swapImages()
        }

        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
}