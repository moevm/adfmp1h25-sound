package ru.etu.soundboard

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.etu.soundboard.Adapter.FileManager
import java.io.File

class SoundConfiguration : AppCompatActivity() {
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    var presets = manager.getConf()
    var cur_set = presets?.set1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_configuration)

        mPrefs = getPreferences(MODE_PRIVATE)

        val key11 = findViewById<ImageButton>(R.id.key_1_1)
        key11.setOnClickListener { cur_set?.let { it1 -> it1.key11 = handleKey(key11, it1.key11) } }
        val key12 = findViewById<ImageButton>(R.id.key_1_2)
        key12.setOnClickListener { cur_set?.let { it1 -> it1.key12 = handleKey(key12, it1.key12) } }
        val key13 = findViewById<ImageButton>(R.id.key_1_3)
        key13.setOnClickListener { cur_set?.let { it1 -> it1.key13 = handleKey(key13, it1.key13) } }
        val key14 = findViewById<ImageButton>(R.id.key_1_4)
        key14.setOnClickListener { cur_set?.let { it1 -> it1.key14 = handleKey(key14, it1.key14) } }
        val key15 = findViewById<ImageButton>(R.id.key_1_5)
        key15.setOnClickListener { cur_set?.let { it1 -> it1.key15 = handleKey(key15, it1.key15) } }
        val key21 = findViewById<ImageButton>(R.id.key_2_1)
        key21.setOnClickListener { cur_set?.let { it1 -> it1.key21 = handleKey(key21, it1.key21) } }
        val key22 = findViewById<ImageButton>(R.id.key_2_2)
        key22.setOnClickListener { cur_set?.let { it1 -> it1.key22 = handleKey(key22, it1.key22) } }
        val key23 = findViewById<ImageButton>(R.id.key_2_3)
        key23.setOnClickListener { cur_set?.let { it1 -> it1.key23 = handleKey(key23, it1.key23) } }
        val key24 = findViewById<ImageButton>(R.id.key_2_4)
        key24.setOnClickListener { cur_set?.let { it1 -> it1.key24 = handleKey(key24, it1.key24) } }
        val key25 = findViewById<ImageButton>(R.id.key_2_5)
        key25.setOnClickListener { cur_set?.let { it1 -> it1.key25 = handleKey(key25, it1.key25) } }
        val key31 = findViewById<ImageButton>(R.id.key_3_1)
        key31.setOnClickListener { cur_set?.let { it1 -> it1.key31 = handleKey(key31, it1.key31) } }
        val key32 = findViewById<ImageButton>(R.id.key_3_2)
        key32.setOnClickListener { cur_set?.let { it1 -> it1.key32 = handleKey(key32, it1.key32) } }
        val key33 = findViewById<ImageButton>(R.id.key_3_3)
        key33.setOnClickListener { cur_set?.let { it1 -> it1.key33 = handleKey(key33, it1.key33) } }
        val key34 = findViewById<ImageButton>(R.id.key_3_4)
        key34.setOnClickListener { cur_set?.let { it1 -> it1.key34 = handleKey(key34, it1.key34) } }
        val key35 = findViewById<ImageButton>(R.id.key_3_5)
        key35.setOnClickListener { cur_set?.let { it1 -> it1.key35 = handleKey(key35, it1.key35) } }
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

        val pickButton = findViewById<ImageButton>(R.id.pickerButton)
        pickButton.setOnClickListener { showFilePicker() }

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

    private fun handleKey(key: ImageButton, content: String): String{
        if(content != ""){
            key.setImageResource(R.drawable.vec_plus)
            return ""
        } else {
            key.setImageResource(R.drawable.vec_trash_big)
            return "test"
        }
    }

    private fun showFilePicker(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/wav"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
        } catch (exception: Exception) {
            Toast.makeText(this, "Something gone wrong...", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Kotlin")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null){
            val uri: Uri? = data.data
            val path = uri?.path.toString()
            println(path)
        }
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