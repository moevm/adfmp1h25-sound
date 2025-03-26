package ru.etu.soundboard

import android.content.Context
import android.content.Intent
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.etu.soundboard.Adapter.FileManager
import ru.etu.soundboard.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import java.io.FileInputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity(),
    TriggerPad.SoundPadTriggerListener,SideButton.SideButtonListener,SideImageButton.SideButtonListener
{

    private lateinit var binding: ActivityMainBinding
    private var mPrefs: SharedPreferences? = null
    private val manager = FileManager
    private var allPresets = manager.getConf()
    private var cur_set = allPresets?.drums

    private lateinit var trackRecorder: TrackRecorder
    private lateinit var macRec_1: TrackRecorder
    private lateinit var macRec_2: TrackRecorder
    private lateinit var macRec_3: TrackRecorder
    private lateinit var macRec_4: TrackRecorder
    private lateinit var macRec_5: TrackRecorder

    private val TAG = "MainActivity"
    private var is_custom_preset = false
    private var deleteMode = false
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

        val buttonMacros1 = findViewById<SideImageButton>(R.id.btnMacros1)
        val buttonMacros2 = findViewById<SideImageButton>(R.id.btnMacros2)
        val buttonMacros3 = findViewById<SideImageButton>(R.id.btnMacros3)
        val buttonMacros4 = findViewById<SideImageButton>(R.id.btnMacros4)
        val buttonMacros5 = findViewById<SideImageButton>(R.id.btnMacros5)

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

        buttonMacros1.addListener(this)
        buttonMacros2.addListener(this)
        buttonMacros3.addListener(this)
        buttonMacros4.addListener(this)
        buttonMacros5.addListener(this)

        buttonDrums.setBackgroundResource(R.drawable.btns_menu2)

        trackRecorder = TrackRecorder()
        macRec_1 = TrackRecorder()
        macRec_2 = TrackRecorder()
        macRec_3 = TrackRecorder()
        macRec_4 = TrackRecorder()
        macRec_5 = TrackRecorder()


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
        val start = findViewById<SideImageButton>(R.id.btnRecord)
        val stop = findViewById<SideImageButton>(R.id.btnPlayPause)
        val wav = findViewById<SideImageButton>(R.id.btnStop)
        val buttonDrums = findViewById<SideImageButton>(R.id.btnDrums)
        val buttonKeys = findViewById<SideImageButton>(R.id.btnKeys)
        val buttonSet1 = findViewById<SideImageButton>(R.id.btnSet1)
        val buttonSet2 = findViewById<SideImageButton>(R.id.btnSet2)
        val buttonSet3 = findViewById<SideImageButton>(R.id.btnSet3)
        val buttonDelete = findViewById<SideImageButton>(R.id.btnDelete)

        val buttonMacros1 = findViewById<SideImageButton>(R.id.btnMacros1)
        val buttonMacros2 = findViewById<SideImageButton>(R.id.btnMacros2)
        val buttonMacros3 = findViewById<SideImageButton>(R.id.btnMacros3)
        val buttonMacros4 = findViewById<SideImageButton>(R.id.btnMacros4)
        val buttonMacros5 = findViewById<SideImageButton>(R.id.btnMacros5)

        buttonDrums.setBackgroundResource(R.drawable.btns_menu2)

        val fileMac1 = File(getExternalFilesDir(null), "macros_1.json")
        if (fileMac1.exists()) {
            buttonMacros1.setBackgroundResource(R.drawable.btns_macros_green)
            macRec_1.setEvent(readTrackFromJson(fileMac1))
        }
        val fileMac2 = File(getExternalFilesDir(null), "macros_2.json")
        if (fileMac2.exists()) {
            buttonMacros2.setBackgroundResource(R.drawable.btns_macros_green)
            macRec_2.setEvent(readTrackFromJson(fileMac2))
        }
        val fileMac3 = File(getExternalFilesDir(null), "macros_3.json")
        if (fileMac3.exists()) {
            buttonMacros3.setBackgroundResource(R.drawable.btns_macros_green)
            macRec_3.setEvent(readTrackFromJson(fileMac3))
        }
        val fileMac4 = File(getExternalFilesDir(null), "macros_4.json")
        if (fileMac4.exists()) {
            buttonMacros4.setBackgroundResource(R.drawable.btns_macros_green)
            macRec_4.setEvent(readTrackFromJson(fileMac4))
        }
        val fileMac5 = File(getExternalFilesDir(null), "macros_5.json")
        if (fileMac5.exists()) {
            buttonMacros5.setBackgroundResource(R.drawable.btns_macros_green)
            macRec_5.setEvent(readTrackFromJson(fileMac5))
        }

        buttonDrums.addListener(this)
        buttonKeys.addListener(this)
        buttonSet1.addListener(this)
        buttonSet2.addListener(this)
        buttonSet3.addListener(this)
        buttonDelete.addListener(this)
        start.addListener(this)
        stop.addListener(this)
        wav.addListener(this)

        buttonMacros1.addListener(this)
        buttonMacros2.addListener(this)
        buttonMacros3.addListener(this)
        buttonMacros4.addListener(this)
        buttonMacros5.addListener(this)

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
        macRec_1.chLoop()
        macRec_2.chLoop()
        macRec_3.chLoop()
        macRec_4.chLoop()
        macRec_5.chLoop()

        trackRecorder.stopRecording()
        macRec_1.stopRecording()
        macRec_2.stopRecording()
        macRec_3.stopRecording()
        macRec_4.stopRecording()
        macRec_5.stopRecording()

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

    fun generateAudioData(trackEvents: List<TrackEvent>, sampleRate: Int): ByteArray {
        val bytesPerSample = 2 // 16-битный звук

        val maxTime = trackEvents.maxOfOrNull { event ->
            val soundData = loadSoundData(event.soundId)
            val soundDuration = (soundData.size / bytesPerSample) * 1000 / sampleRate
            event.timestamp + soundDuration
        } ?: 0

        val totalSamples = (maxTime * sampleRate / 1000).toInt()
        val audioData = ByteArray(totalSamples * bytesPerSample)

        for (event in trackEvents) {
            val soundData = loadSoundData(event.soundId)
            val startSample = (event.timestamp * sampleRate / 1000).toInt()

            for (i in soundData.indices) {
                val outputIndex = startSample * bytesPerSample + i
                if (outputIndex < audioData.size) {
                    val mixedValue = (audioData[outputIndex].toInt() + soundData[i].toInt()).coerceIn(-128, 127)
                    audioData[outputIndex] = mixedValue.toByte()
                }
            }
        }

        return audioData
    }

    fun saveAsWav(audioData: ByteArray, wavFile: File, sampleRate: Int, channels: Int) {
        val wavHeader = createWavHeader(audioData.size, sampleRate, channels)

        FileOutputStream(wavFile).use { outputStream ->
            outputStream.write(wavHeader)
            outputStream.write(audioData)
        }
    }

    fun saveWavToMediaStore(
        contentResolver: ContentResolver,
        audioData: ByteArray,
        fileName: String,
        sampleRate: Int,
        channels: Int
    ): Uri? {
        // Создаем временный файл в локальном хранилище
        val tempFile = File.createTempFile("temp_audio", ".wav")
        saveAsWav(audioData, tempFile, sampleRate, channels)

        // Создаем ContentValues для добавления записи в MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            put(MediaStore.Audio.Media.IS_PENDING, 1) // Файл будет доступен только вашему приложению до завершения записи
        }

        // Вставляем запись в MediaStore
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = contentResolver.insert(collection, contentValues)

        if (uri != null) {
            // Открываем OutputStream для записи данных в MediaStore
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use { os ->
                // Копируем данные из временного файла в OutputStream
                tempFile.inputStream().copyTo(os)
            }

            // Помечаем файл как доступный для всех приложений
            contentValues.clear()
            contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }

        // Удаляем временный файл
        tempFile.delete()

        return uri
    }

    private fun createWavHeader(dataSize: Int, sampleRate: Int, channels: Int): ByteArray {
        val header = ByteArray(44)
        val byteRate = sampleRate * channels * 2 // 2 байта на сэмпл для 16-битного звука

        // RIFF заголовок
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()

        // Размер файла (dataSize + 36)
        val fileSize = dataSize + 36
        header[4] = (fileSize and 0xff).toByte()
        header[5] = (fileSize shr 8 and 0xff).toByte()
        header[6] = (fileSize shr 16 and 0xff).toByte()
        header[7] = (fileSize shr 24 and 0xff).toByte()

        // WAVE формат
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        // Подзаголовок fmt
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()

        // Размер подзаголовка fmt (16 байт)
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // Аудиоформат (1 для PCM)
        header[20] = 1
        header[21] = 0

        // Количество каналов
        header[22] = channels.toByte()
        header[23] = 0

        // Частота дискретизации
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()

        // Байтовая скорость
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()

        // Блок выравнивания
        header[32] = (channels * 2).toByte()
        header[33] = 0

        // Бит на сэмпл (16 бит)
        header[34] = 16
        header[35] = 0

        // Подзаголовок data
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()

        // Размер данных
        header[40] = (dataSize and 0xff).toByte()
        header[41] = (dataSize shr 8 and 0xff).toByte()
        header[42] = (dataSize shr 16 and 0xff).toByte()
        header[43] = (dataSize shr 24 and 0xff).toByte()

        return header
    }


    fun loadSoundData(soundId: Int): ByteArray {
        // Загрузите звуковые данные для soundId (например, из assets или файловой системы)
        // Пример: загрузка из assets
        val fileName = when (soundId) {
            SoundPlayer.KEY11 -> cur_set!!.key11
            SoundPlayer.KEY12 -> cur_set!!.key12
            SoundPlayer.KEY13 -> cur_set!!.key13
            SoundPlayer.KEY14 -> cur_set!!.key14
            SoundPlayer.KEY15 -> cur_set!!.key15
            SoundPlayer.KEY21 -> cur_set!!.key21
            SoundPlayer.KEY22 -> cur_set!!.key22
            SoundPlayer.KEY23 -> cur_set!!.key23
            SoundPlayer.KEY24 -> cur_set!!.key24
            SoundPlayer.KEY25 -> cur_set!!.key25
            SoundPlayer.KEY31 -> cur_set!!.key31
            SoundPlayer.KEY32 -> cur_set!!.key32
            SoundPlayer.KEY33 -> cur_set!!.key33
            SoundPlayer.KEY34 -> cur_set!!.key34
            SoundPlayer.KEY35 -> cur_set!!.key35
            // Добавьте другие звуки
            else -> throw IllegalArgumentException("Unknown sound ID: $soundId")
        }
        if (!is_custom_preset) {
            val assetManager = getAssets()
            return assetManager.open(fileName).readBytes()
        }else{
            // Читаем файл и возвращаем его содержимое в виде ByteArray
            return FileInputStream(fileName).use { inputStream ->
                inputStream.readBytes()
            }

        }
    }

    override fun triggerDown(pad: TriggerPad) {
        // trigger the sound based on the pad
        findViewById<TriggerPad>(pad.id).setBackgroundResource(R.drawable.btns_keypads2)
        when (pad.id) {
            R.id.key_1_1 -> {if(cur_set!!.key11 != "") mSoundPlayer.trigger(SoundPlayer.KEY11)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY11)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY11)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY11)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY11)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY11)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY11)
            }
            R.id.key_1_2 -> {if(cur_set!!.key12 != "") mSoundPlayer.trigger(SoundPlayer.KEY12)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY12)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY12)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY12)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY12)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY12)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY12)
            }
            R.id.key_1_3 -> {if(cur_set!!.key13 != "") mSoundPlayer.trigger(SoundPlayer.KEY13)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY13)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY13)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY13)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY13)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY13)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY13)
                }
            R.id.key_1_4 -> {if(cur_set!!.key14 != "") mSoundPlayer.trigger(SoundPlayer.KEY14)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY14)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY14)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY14)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY14)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY14)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY14)
                }
            R.id.key_1_5 -> {if(cur_set!!.key15 != "") mSoundPlayer.trigger(SoundPlayer.KEY15)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY15)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY15)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY15)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY15)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY15)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY15)
                }

            R.id.key_2_1 -> {if(cur_set!!.key21 != "") mSoundPlayer.trigger(SoundPlayer.KEY21)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY21)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY21)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY21)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY21)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY21)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY21)
                }
            R.id.key_2_2 -> {if(cur_set!!.key22 != "") mSoundPlayer.trigger(SoundPlayer.KEY22)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY22)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY22)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY22)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY22)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY22)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY22)
                }
            R.id.key_2_3 -> {if(cur_set!!.key23 != "") mSoundPlayer.trigger(SoundPlayer.KEY23)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY23)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY23)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY23)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY23)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY23)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY23)
                }
            R.id.key_2_4 -> {if(cur_set!!.key24 != "") mSoundPlayer.trigger(SoundPlayer.KEY24)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY24)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY24)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY24)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY24)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY24)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY24)
                }
            R.id.key_2_5 -> {if(cur_set!!.key25 != "") mSoundPlayer.trigger(SoundPlayer.KEY25)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY25)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY25)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY25)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY25)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY25)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY25)
                }

            R.id.key_3_1 -> {if(cur_set!!.key31 != "") mSoundPlayer.trigger(SoundPlayer.KEY31)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY31)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY31)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY31)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY31)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY31)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY31)
                }
            R.id.key_3_2 -> {if(cur_set!!.key32 != "") mSoundPlayer.trigger(SoundPlayer.KEY32)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY32)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY32)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY32)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY32)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY32)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY32)
                }
            R.id.key_3_3 -> {if(cur_set!!.key33 != "") mSoundPlayer.trigger(SoundPlayer.KEY33)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY33)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY33)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY33)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY33)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY33)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY33)
                }
            R.id.key_3_4 -> {if(cur_set!!.key34 != "") mSoundPlayer.trigger(SoundPlayer.KEY34)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY34)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY34)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY34)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY34)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY34)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY34)
                }
            R.id.key_3_5 -> {if(cur_set!!.key35 != "") mSoundPlayer.trigger(SoundPlayer.KEY35)
                if (trackRecorder.isRec())
                    trackRecorder.recordEvent(SoundPlayer.KEY35)
                else if (macRec_1.isRec())
                    macRec_1.recordEvent(SoundPlayer.KEY35)
                else if (macRec_2.isRec())
                    macRec_2.recordEvent(SoundPlayer.KEY35)
                else if (macRec_3.isRec())
                    macRec_3.recordEvent(SoundPlayer.KEY35)
                else if (macRec_4.isRec())
                    macRec_4.recordEvent(SoundPlayer.KEY35)
                else if (macRec_5.isRec())
                    macRec_5.recordEvent(SoundPlayer.KEY35)
                }
        }
    }

    fun readTrackFromJson(file: File): List<TrackEvent> {
        val json = file.readText()
        val type = object : TypeToken<List<TrackEvent>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveTrackToFile(tRec: TrackRecorder, fileName: String) {
        val trackEvents = tRec.getEvents()
        val gson = Gson()
        val json = gson.toJson(trackEvents)

        val file = File(getExternalFilesDir(null), fileName)
        file.writeText(json)

        Log.d("record", "Track saved: ${file.absolutePath}")
    }

    private fun playTrack(tRec: TrackRecorder) {
        val trackEvents = tRec.getEvents()
        if (trackEvents.isEmpty()) return // Если трек пустой, ничего не делаем
        for (event in trackEvents) {
            Timer().schedule(event.timestamp) {
                mSoundPlayer.trigger(event.soundId)
            }
        }

        fun play() {
            for (event in trackEvents) {
                Timer().schedule(event.timestamp) {
                    if (trackRecorder.isRec())
                        trackRecorder.recordEvent(event.soundId)
                    mSoundPlayer.trigger(event.soundId)
                }
            }

            // Если включено циклическое воспроизведение, перезапускаем трек
            if (tRec.loop()) {
                Timer().schedule(trackEvents.last().timestamp) { // Ждем окончания трека + 1 секунда
                    play()
                }
            }
        }

        play()
    }

    override fun triggerUp(pad: TriggerPad) {
        findViewById<TriggerPad>(pad.id).setBackgroundResource(R.drawable.btns_keypads)
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

    private fun resetBackRes() {
        val buttonDrums = findViewById<SideImageButton>(R.id.btnDrums)
        val buttonKeys = findViewById<SideImageButton>(R.id.btnKeys)
        val buttonSet1 = findViewById<SideImageButton>(R.id.btnSet1)
        val buttonSet2 = findViewById<SideImageButton>(R.id.btnSet2)
        val buttonSet3 = findViewById<SideImageButton>(R.id.btnSet3)

        buttonDrums.setBackgroundResource(R.drawable.btns_menu)
        buttonKeys.setBackgroundResource(R.drawable.btns_menu)
        buttonSet1.setBackgroundResource(R.drawable.btns_menu)
        buttonSet2.setBackgroundResource(R.drawable.btns_menu)
        buttonSet3.setBackgroundResource(R.drawable.btns_menu)
    }

    override fun onButtonDown(button: SideImageButton) {
        Log.d("MainActivity", "Button down: ${button.id}")
        val btn = findViewById<SideImageButton>(button.id)
        when (button.id) {
            R.id.btnRecord -> {
                 if (!trackRecorder.isRec()) {
                     // Начало записи
                     Log.d("record", "rec start")
                     Toast.makeText(
                         this,
                         "Запись начата",
                         Toast.LENGTH_SHORT
                     ).show()
                     btn.setBackgroundResource(R.drawable.btns_menu2)
                     trackRecorder.startRecording()
                 }
                 btn.setBackgroundResource(R.drawable.btns_menu2)
            }
            R.id.btnPlayPause -> {
                Log.d("record", "play") // здесь пример того, как вызвать записанный в json макром (мини тречик)
                playTrack(trackRecorder)
                btn.setBackgroundResource(R.drawable.btns_menu2)
            }
            R.id.btnStop -> {
                if(trackRecorder.isRec()) {
                    Log.d("record", "rec stop")
                    trackRecorder.stopRecording()
                    saveTrackToFile(trackRecorder, "track.json") // Сохранение трека в файл

                    val jsonFile = File(getExternalFilesDir(null), "track.json")
                    val wavFile = File(getExternalFilesDir(null), "new_track.wav")

                    val trackEvents = readTrackFromJson(jsonFile)
                    val audioData = generateAudioData(trackEvents, sampleRate = 44100)
                    // saveAsWav(audioData, wavFile, sampleRate = 44100, channels = 1)
                    saveWavToMediaStore(contentResolver, audioData, "new_track.wav", 44100, 1)

                    Toast.makeText(
                        this,
                        "Трек сохранен",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                findViewById<SideImageButton>(R.id.btnRecord).setBackgroundResource(R.drawable.btns_menu)
                btn.setBackgroundResource(R.drawable.btns_menu2)
            }
            R.id.btnMacros1 -> {
                if (deleteMode && !macRec_1.isRecEmpty()){
                    macRec_1.deleteEvent()
                    val file = File(getExternalFilesDir(null), "macros_1.json")
                    if (file.exists()) {
                        file.delete()
                    }
                    btn.setBackgroundResource(R.drawable.btns_macros)
                }
                else if (macRec_1.isRecEmpty() && !macRec_1.isRec())
                {
                    btn.setBackgroundResource(R.drawable.btns_macros_red)
                    for(n in 1..4) {
                        Log.d("macros 1", "count " + n.toString())
                        Thread.sleep(500)
                    }
                    Log.d("macros 1", "rec start")
                    macRec_1.startRecording()
                } else if (macRec_1.isRec())
                {
                    btn.setBackgroundResource(R.drawable.btns_macros_green)
                    Log.d("macros 1", "rec stop")
                    macRec_1.stopRecording()
                    saveTrackToFile(macRec_1, "macros_1.json")
                } else if (!macRec_1.isRecEmpty())
                {
                    if (macRec_1.loop())
                    {
                        btn.setBackgroundResource(R.drawable.btns_macros_green)
                        macRec_1.chLoop()
                        Log.d("macros 1", "stop loop playing")
                    } else {
                        btn.setBackgroundResource(R.drawable.btns_macros_yellow)
                        macRec_1.chLoop()
                        Log.d("macros 1", "playing") // здесь пример того, как вызвать записанный в json макром (мини тречик)
                        playTrack(macRec_1)
                    }
                }
            }
            R.id.btnMacros2 -> {
                if (deleteMode && !macRec_2.isRecEmpty()){
                    macRec_2.deleteEvent()
                    val file = File(getExternalFilesDir(null), "macros_2.json")
                    if (file.exists()) {
                        file.delete()
                    }
                    btn.setBackgroundResource(R.drawable.btns_macros)
                }
                else if (macRec_2.isRecEmpty() && !macRec_2.isRec())
                {
                    btn.setBackgroundResource(R.drawable.btns_macros_red)
                    for(n in 1..4) {
                        Log.d("macros 2", "count " + n.toString())
                        Thread.sleep(500)
                    }
                    Log.d("macros 2", "rec start")
                    macRec_2.startRecording()
                } else if (macRec_2.isRec())
                {
                    btn.setBackgroundResource(R.drawable.btns_macros_green)
                    Log.d("macros 2", "rec stop")
                    macRec_2.stopRecording()
                    saveTrackToFile(macRec_2, "macros_2.json")
                } else if (!macRec_2.isRecEmpty())
                {
                    if (macRec_2.loop())
                    {
                        btn.setBackgroundResource(R.drawable.btns_macros_green)
                        macRec_2.chLoop()
                        Log.d("macros 2", "stop loop playing")
                    } else {
                        btn.setBackgroundResource(R.drawable.btns_macros_yellow)
                        macRec_2.chLoop()
                        Log.d("macros 2", "playing") // здесь пример того, как вызвать записанный в json макром (мини тречик)
                        playTrack(macRec_2)
                    }
                }
            }
            R.id.btnMacros3 -> {
                if (deleteMode && !macRec_3.isRecEmpty()){
                    macRec_3.deleteEvent()
                    val file = File(getExternalFilesDir(null), "macros_3.json")
                    if (file.exists()) {
                        file.delete()
                    }
                    btn.setBackgroundResource(R.drawable.btns_macros)
                }
                else if (macRec_3.isRecEmpty() && !macRec_3.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_red)
                    for (n in 1..4) {
                        Log.d("macros 3", "count " + n.toString())
                        Thread.sleep(500)
                    }
                    Log.d("macros 3", "rec start")
                    macRec_3.startRecording()
                } else if (macRec_3.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_green)
                    Log.d("macros 3", "rec stop")
                    macRec_3.stopRecording()
                    saveTrackToFile(macRec_3, "macros_3.json")
                } else if (!macRec_3.isRecEmpty()) {
                    if (macRec_3.loop()) {
                        btn.setBackgroundResource(R.drawable.btns_macros_green)
                        macRec_3.chLoop()
                        Log.d("macros 3", "stop loop playing")
                    } else {
                        btn.setBackgroundResource(R.drawable.btns_macros_yellow)
                        macRec_3.chLoop()
                        Log.d(
                            "macros 3",
                            "playing"
                        ) // здесь пример того, как вызвать записанный в json макром (мини тречик)
                        playTrack(macRec_3)
                    }
                }
            }
            R.id.btnMacros4 -> {
                if (deleteMode && !macRec_4.isRecEmpty()){
                    macRec_4.deleteEvent()
                    val file = File(getExternalFilesDir(null), "macros_4.json")
                    if (file.exists()) {
                        file.delete()
                    }
                    btn.setBackgroundResource(R.drawable.btns_macros)
                }
                else if (macRec_4.isRecEmpty() && !macRec_4.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_red)
                    for (n in 1..4) {
                        Log.d("macros 4", "count " + n.toString())
                        Thread.sleep(500)
                    }
                    Log.d("macros 4", "rec start")
                    macRec_4.startRecording()
                } else if (macRec_4.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_green)
                    Log.d("macros 4", "rec stop")
                    macRec_4.stopRecording()
                    saveTrackToFile(macRec_4, "macros_4.json")
                } else if (!macRec_4.isRecEmpty()) {
                    if (macRec_4.loop()) {
                        btn.setBackgroundResource(R.drawable.btns_macros_green)
                        macRec_4.chLoop()
                        Log.d("macros 4", "stop loop playing")
                    } else {
                        btn.setBackgroundResource(R.drawable.btns_macros_yellow)
                        macRec_4.chLoop()
                        Log.d(
                            "macros 4",
                            "playing"
                        ) // здесь пример того, как вызвать записанный в json макром (мини тречик)
                        playTrack(macRec_4)
                    }
                }
            }
            R.id.btnMacros5 -> {
                if (deleteMode && !macRec_5.isRecEmpty()){
                    macRec_5.deleteEvent()
                    val file = File(getExternalFilesDir(null), "macros_5.json")
                    if (file.exists()) {
                        file.delete()
                    }
                    btn.setBackgroundResource(R.drawable.btns_macros)
                }
                else if (macRec_5.isRecEmpty() && !macRec_5.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_red)
                    for (n in 1..4) {
                        Log.d("macros 5", "count " + n.toString())
                        Thread.sleep(500)
                    }
                    Log.d("macros 5", "rec start")
                    macRec_5.startRecording()
                } else if (macRec_5.isRec()) {
                    btn.setBackgroundResource(R.drawable.btns_macros_green)
                    Log.d("macros 5", "rec stop")
                    macRec_5.stopRecording()
                    saveTrackToFile(macRec_5, "macros_5.json")
                } else if (!macRec_5.isRecEmpty()) {
                    if (macRec_5.loop()) {
                        btn.setBackgroundResource(R.drawable.btns_macros_green)
                        macRec_5.chLoop()
                        Log.d("macros 5", "stop loop playing")
                    } else {
                        btn.setBackgroundResource(R.drawable.btns_macros_yellow)
                        macRec_5.chLoop()
                        Log.d(
                            "macros 5",
                            "playing"
                        ) // здесь пример того, как вызвать записанный в json макром (мини тречик)
                        playTrack(macRec_5)
                    }
                }
            }

            R.id.btnDrums -> {
                resetBackRes()
                btn.setBackgroundResource(R.drawable.btns_menu2)
                is_custom_preset = false
                cur_set = allPresets?.drums
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssets(getAssets(), cur_set!!)
            }
            R.id.btnKeys -> {
                resetBackRes()
                btn.setBackgroundResource(R.drawable.btns_menu2)
                is_custom_preset = false
                cur_set = this.allPresets?.keys
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssets(getAssets(), cur_set!!)
            }
            R.id.btnSet1 -> {
                resetBackRes()
                btn.setBackgroundResource(R.drawable.btns_menu2)
                is_custom_preset = true
                cur_set = this.allPresets?.set1
                Log.d("MainActivity", cur_set.toString())
                Log.d("MainActivity", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnSet2 -> {
                resetBackRes()
                btn.setBackgroundResource(R.drawable.btns_menu2)
                is_custom_preset = true
                cur_set = this.allPresets?.set2
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnSet3 -> {
                resetBackRes()
                btn.setBackgroundResource(R.drawable.btns_menu2)
                is_custom_preset = true
                cur_set = this.allPresets?.set3
                mSoundPlayer.unloadWavAssets()
                mSoundPlayer.loadWavAssetsFromStorage(cur_set!!)
            }
            R.id.btnDelete -> {
                if(deleteMode){
                    deleteMode = false
                    btn.setBackgroundResource(R.drawable.btns_menu)
                } else {
                    deleteMode = true
                    btn.setBackgroundResource(R.drawable.btns_menu2)
                }
            }
        }
    }

    override fun onButtonUp(button: SideImageButton) {
        Log.d("daunblya", "zrobiu")
        val btn = findViewById<SideImageButton>(button.id)
        when (button.id) {
//            R.id.btnRecord -> btn.setBackgroundResource(R.drawable.btns_menu)
            R.id.btnPlayPause -> btn.setBackgroundResource(R.drawable.btns_menu)
            R.id.btnStop -> btn.setBackgroundResource(R.drawable.btns_menu)
//            R.id.btnDrums -> btn.setBackgroundResource(R.drawable.btns_menu)
//            R.id.btnKeys -> btn.setBackgroundResource(R.drawable.btns_menu)
//            R.id.btnSet1 -> btn.setBackgroundResource(R.drawable.btns_menu)
//            R.id.btnSet2 -> btn.setBackgroundResource(R.drawable.btns_menu)
//            R.id.btnSet3 -> btn.setBackgroundResource(R.drawable.btns_menu)
        }
    }
}