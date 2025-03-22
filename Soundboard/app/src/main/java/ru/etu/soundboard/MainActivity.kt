package ru.etu.soundboard

import android.content.Context
import android.content.Intent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import ru.etu.soundboard.Adapter.FileManager
import ru.etu.soundboard.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(),
    TriggerPad.SoundPadTriggerListener,SideButton.SideButtonListener,SideImageButton.SideButtonListener
{

    private lateinit var binding: ActivityMainBinding
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    private var allPresets = manager.getConf()
    private var cur_set = allPresets?.drums

    private val TAG = "MainActivity"

    private var mAudioMgr: AudioManager? = null

    private var mSoundPlayer = SoundPlayer()

    private val mUseDeviceChangeFallback = false
    private val mSwitchTimerMs = 500L

    private var mDevicesInitialized = false

    private var mDeviceListener: DeviceListener = DeviceListener()


    init {
        // Load the library containing the a native code including the JNI  functions
        System.loadLibrary("soundboard")
    }

    inner class DeviceListener: AudioDeviceCallback() {
        private fun logDevices(label: String, devices: Array<AudioDeviceInfo> ) {
            Log.i(TAG, label + " " + devices.size)
            for(device in devices) {
                Log.i(TAG, "  " + device.getProductName().toString()
                        + " type:" + device.getType()
                        + " source:" + device.isSource()
                        + " sink:" + device.isSink())
            }
        }

        override fun onAudioDevicesAdded(addedDevices: Array<AudioDeviceInfo> ) {
            // Note: This will get called when the callback is installed.
            if (mDevicesInitialized) {
                logDevices("onAudioDevicesAdded", addedDevices)
                // This is not the initial callback, so devices have changed
                Toast.makeText(applicationContext, "Added Device", Toast.LENGTH_LONG).show()
                resetOutput()
            }
            mDevicesInitialized = true
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<AudioDeviceInfo> ) {
            logDevices("onAudioDevicesRemoved", removedDevices)
            Toast.makeText(applicationContext, "Removed Device", Toast.LENGTH_LONG).show()
            resetOutput()
        }

        private fun resetOutput() {
            Log.i(TAG, "resetOutput() time:" + LocalDateTime.now() + " native reset:" + mSoundPlayer.getOutputReset())
            if (mSoundPlayer.getOutputReset()) {
                // the (native) stream has been reset by the onErrorAfterClose() callback
                mSoundPlayer.clearOutputReset()
            } else {
                // give the (native) stream a chance to close it.
                val timer = Timer("stream restart timer time:" + LocalDateTime.now(),
                    false)
                // schedule a single event
                timer.schedule(mSwitchTimerMs) {
                    if (!mSoundPlayer.getOutputReset()) {
                        // still didn't get reset, so lets do it ourselves
                        Log.i(TAG, "restartStream() time:" + LocalDateTime.now())
                        mSoundPlayer.restartStream()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = getPreferences(MODE_PRIVATE)

        val manager = FileManager
        allPresets = manager.getConf()

        if(allPresets == null) {
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
            allPresets = manager.getConf()
        } else {
            manager.getConf()?.let { saveConf(it) }
        }
        cur_set = allPresets?.drums
        Log.d("MainActivity", allPresets.toString())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Инициализация боковых кнопок
        val buttonAboutDevs = findViewById<SideButton>(R.id.pageAboutDevs)
        val buttonConfigureSounds = findViewById<SideButton>(R.id.pageConfigureSounds)
        val buttonMyTracks = findViewById<SideButton>(R.id.pageMyTracks)
        val buttonHelp = findViewById<SideButton>(R.id.pageHelp)

        // Инициализация кнопок верхней и правой панелей
        val buttonDrums = findViewById<SideImageButton>(R.id.btnDrums)
        val buttonKeys = findViewById<SideImageButton>(R.id.btnKeys)
        val buttonSet1 = findViewById<SideImageButton>(R.id.btnSet1)
        val buttonSet2 = findViewById<SideImageButton>(R.id.btnSet2)
        val buttonSet3 = findViewById<SideImageButton>(R.id.btnSet3)
        val buttonDelete = findViewById<SideImageButton>(R.id.btnDelete)

        // Добавление обработчиков
        buttonAboutDevs.addListener(this)
        buttonConfigureSounds.addListener(this)
        buttonMyTracks.addListener(this)
        buttonHelp.addListener(this)

        buttonDrums.addListener(this)
        buttonKeys.addListener(this)
        buttonSet1.addListener(this)
        buttonSet2.addListener(this)
        buttonSet3.addListener(this)
        buttonDelete.addListener(this)

        mAudioMgr = getSystemService(Context.AUDIO_SERVICE) as AudioManager


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }


    override fun onStart() {
        super.onStart()

        mSoundPlayer.unloadWavAssets()

        mSoundPlayer.setupAudioStream()

        mSoundPlayer.loadWavAssets(getAssets(), cur_set!!)

        mSoundPlayer.startAudioStream()

        if (mUseDeviceChangeFallback) {
            mAudioMgr!!.registerAudioDeviceCallback(mDeviceListener, null)
        }
    }

    override fun onResume() {
        super.onResume()

        // UI
        setContentView(R.layout.activity_main)

        // "Kick" drum
        // Инициализация боковых кнопок
        val buttonAboutDevs = findViewById<SideButton>(R.id.pageAboutDevs)
        val buttonConfigureSounds = findViewById<SideButton>(R.id.pageConfigureSounds)
        val buttonMyTracks = findViewById<SideButton>(R.id.pageMyTracks)
        val buttonHelp = findViewById<SideButton>(R.id.pageHelp)

        // Инициализация кнопок верхней и правой панелей
        val buttonDrums = findViewById<SideImageButton>(R.id.btnDrums)
        val buttonKeys = findViewById<SideImageButton>(R.id.btnKeys)
        val buttonSet1 = findViewById<SideImageButton>(R.id.btnSet1)
        val buttonSet2 = findViewById<SideImageButton>(R.id.btnSet2)
        val buttonSet3 = findViewById<SideImageButton>(R.id.btnSet3)
        val buttonDelete = findViewById<SideImageButton>(R.id.btnDelete)

        buttonDrums.addListener(this)
        buttonKeys.addListener(this)
        buttonSet1.addListener(this)
        buttonSet2.addListener(this)
        buttonSet3.addListener(this)
        buttonDelete.addListener(this)

        // Добавление обработчиков
        buttonAboutDevs.addListener(this)
        buttonConfigureSounds.addListener(this)
        buttonMyTracks.addListener(this)
        buttonHelp.addListener(this)

        findViewById<TriggerPad>(R.id.key_1_1).addListener(this)
        findViewById<TriggerPad>(R.id.key_1_2).addListener(this)
        findViewById<TriggerPad>(R.id.key_1_3).addListener(this)
        findViewById<TriggerPad>(R.id.key_1_4).addListener(this)
        findViewById<TriggerPad>(R.id.key_1_5).addListener(this)

        findViewById<TriggerPad>(R.id.key_2_1).addListener(this)
        findViewById<TriggerPad>(R.id.key_2_2).addListener(this)
        findViewById<TriggerPad>(R.id.key_2_3).addListener(this)
        findViewById<TriggerPad>(R.id.key_2_4).addListener(this)
        findViewById<TriggerPad>(R.id.key_2_5).addListener(this)

        findViewById<TriggerPad>(R.id.key_3_1).addListener(this)
        findViewById<TriggerPad>(R.id.key_3_2).addListener(this)
        findViewById<TriggerPad>(R.id.key_3_3).addListener(this)
        findViewById<TriggerPad>(R.id.key_3_4).addListener(this)
        findViewById<TriggerPad>(R.id.key_3_5).addListener(this)

    }

    override fun onStop() {
        if (mUseDeviceChangeFallback) {
            mAudioMgr!!.unregisterAudioDeviceCallback(mDeviceListener)
        }

        mSoundPlayer.teardownAudioStream()

        mSoundPlayer.unloadWavAssets()

        super.onStop()
    }

    //
    // DrumPad.DrumPadTriggerListener
    //


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

    override fun triggerDown(pad: TriggerPad) {
        // trigger the sound based on the pad
        Log.d("kek", "d")
        when (pad.id) {
            R.id.key_1_1 -> if(cur_set!!.key11 != "") mSoundPlayer.trigger(SoundPlayer.KEY11)
            R.id.key_1_2 -> if(cur_set!!.key12 != "") mSoundPlayer.trigger(SoundPlayer.KEY12)
            R.id.key_1_3 -> if(cur_set!!.key13 != "") mSoundPlayer.trigger(SoundPlayer.KEY13)
            R.id.key_1_4 -> if(cur_set!!.key14 != "") mSoundPlayer.trigger(SoundPlayer.KEY14)
            R.id.key_1_5 -> if(cur_set!!.key15 != "") mSoundPlayer.trigger(SoundPlayer.KEY15)

            R.id.key_2_1 -> if(cur_set!!.key21 != "") mSoundPlayer.trigger(SoundPlayer.KEY21)
            R.id.key_2_2 -> if(cur_set!!.key22 != "") mSoundPlayer.trigger(SoundPlayer.KEY22)
            R.id.key_2_3 -> if(cur_set!!.key23 != "") mSoundPlayer.trigger(SoundPlayer.KEY23)
            R.id.key_2_4 -> if(cur_set!!.key24 != "") mSoundPlayer.trigger(SoundPlayer.KEY24)
            R.id.key_2_5 -> if(cur_set!!.key25 != "") mSoundPlayer.trigger(SoundPlayer.KEY25)

            R.id.key_3_1 -> if(cur_set!!.key31 != "") mSoundPlayer.trigger(SoundPlayer.KEY31)
            R.id.key_3_2 -> if(cur_set!!.key32 != "") mSoundPlayer.trigger(SoundPlayer.KEY32)
            R.id.key_3_3 -> if(cur_set!!.key33 != "") mSoundPlayer.trigger(SoundPlayer.KEY33)
            R.id.key_3_4 -> if(cur_set!!.key34 != "") mSoundPlayer.trigger(SoundPlayer.KEY34)
            R.id.key_3_5 -> if(cur_set!!.key35 != "") mSoundPlayer.trigger(SoundPlayer.KEY35)
        }
    }

    override fun triggerUp(pad: TriggerPad) {

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
            R.id.btnDrums -> {
                cur_set = allPresets?.drums
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssets(getAssets(), cur_set!!)
            }
            R.id.btnKeys -> {
                cur_set = this.allPresets?.keys
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssets(getAssets(), cur_set!!)
            }
            R.id.btnSet1 -> {
                cur_set = this.allPresets?.set1
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnSet2 -> {
                cur_set = this.allPresets?.set2
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnSet3 -> {
                cur_set = this.allPresets?.set3
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnDelete -> {

            }
        }
    }

    override fun onButtonUp(button: SideImageButton) {
        // Логика при отпускании кнопки (если нужна)
    }
}