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
import ru.etu.soundboard.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = getPreferences(MODE_PRIVATE)

        val manager = FileManager
        var presets = manager.getConf()

        if(presets == null) {
            val conf = readConf()
            if (conf == null) {
                println("Gere")
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
        }
//        println(presets?.keys?.key11)

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

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        /*val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_about_devs
            )
        )
       // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
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
}
