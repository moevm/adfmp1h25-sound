package ru.etu.soundboard

import android.content.res.AssetManager
import android.net.Uri
import ru.etu.soundboard.Adapter.FileManager
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.IOException

class SoundPlayer {
    companion object {
        // Sample attributes
        val NUM_PLAY_CHANNELS: Int = 2  // The number of channels in the player Stream.
                                        // Stereo Playback, set to 1 for Mono playback

        // Sample Buffer IDs
        var KEY11: Int = 0
        var KEY12: Int = 0
        var KEY13: Int = 0
        var KEY14: Int = 0
        var KEY15: Int = 0
        var KEY21: Int = 0
        var KEY22: Int = 0
        var KEY23: Int = 0
        var KEY24: Int = 0
        var KEY25: Int = 0
        var KEY31: Int = 0
        var KEY32: Int = 0
        var KEY33: Int = 0
        var KEY34: Int = 0
        var KEY35: Int = 0

        // initial pan position for each drum sample
        val PAN_KEY11: Float = 0f
        val PAN_KEY12: Float = 0f
        val PAN_KEY13: Float = 0f
        val PAN_KEY14: Float = 0f
        val PAN_KEY15: Float = 0f
        val PAN_KEY21: Float = 0f
        val PAN_KEY22: Float = 0f
        val PAN_KEY23: Float = 0f
        val PAN_KEY24: Float = 0f
        val PAN_KEY25: Float = 0f
        val PAN_KEY31: Float = 0f
        val PAN_KEY32: Float = 0f
        val PAN_KEY33: Float = 0f
        val PAN_KEY34: Float = 0f
        val PAN_KEY35: Float = 0f

        // Logging Tag
        val TAG: String = "SoundPlayer"
    }

    fun setupAudioStream() {
        setupAudioStreamNative(NUM_PLAY_CHANNELS)
    }

    fun startAudioStream() {
        startAudioStreamNative()
    }

    fun teardownAudioStream() {
        teardownAudioStreamNative()
    }

    // asset-based samples
    fun loadWavAssets(assetMgr: AssetManager, preset: FileManager.Preset) {
        var counter = 0
        Log.d("kek", preset.key11)
        if(preset.key11 != "") {
            KEY11 = counter
            loadWavAsset(assetMgr, preset.key11, KEY11, PAN_KEY11)
            counter+=1
        }
        if(preset.key12 != "") {
            KEY12 = counter
            loadWavAsset(assetMgr, preset.key12, KEY12, PAN_KEY12)
            counter+=1
        }
        if(preset.key13 != "") {
            KEY13 = counter
            loadWavAsset(assetMgr, preset.key13, KEY13, PAN_KEY13)
            counter+=1
        }
        if(preset.key14 != "") {
            KEY14 = counter
            loadWavAsset(assetMgr, preset.key14, KEY14, PAN_KEY14)
            counter+=1
        }
        if(preset.key15 != "") {
            KEY15 = counter
            loadWavAsset(assetMgr, preset.key15, KEY15, PAN_KEY15)
            counter+=1
        }
        if(preset.key21 != "") {
            KEY21 = counter
            loadWavAsset(assetMgr, preset.key21, KEY21, PAN_KEY21)
            counter+=1
        }
        if(preset.key22 != "") {
            KEY22 = counter
            loadWavAsset(assetMgr, preset.key22, KEY22, PAN_KEY22)
            counter+=1
        }
        if(preset.key23 != "") {
            KEY23 = counter
            loadWavAsset(assetMgr, preset.key23, KEY23, PAN_KEY23)
            counter+=1
        }
        if(preset.key24 != "") {
            KEY24 = counter
            loadWavAsset(assetMgr, preset.key24, KEY24, PAN_KEY24)
            counter+=1
        }
        if(preset.key25 != "") {
            KEY25 = counter
            loadWavAsset(assetMgr, preset.key25, KEY25, PAN_KEY25)
            counter+=1
        }
        if(preset.key31 != "") {
            KEY31 = counter
            loadWavAsset(assetMgr, preset.key31, KEY31, PAN_KEY31)
            counter+=1
        }
        if(preset.key32 != "") {
            KEY32 = counter
            loadWavAsset(assetMgr, preset.key32, KEY32, PAN_KEY32)
            counter+=1
        }
        if(preset.key33 != "") {
            KEY33 = counter
            loadWavAsset(assetMgr, preset.key33, KEY33, PAN_KEY33)
            counter+=1
        }
        if(preset.key34 != "") {
            KEY34 = counter
            loadWavAsset(assetMgr, preset.key34, KEY34, PAN_KEY34)
            counter+=1
        }
        if(preset.key35 != "") {
            KEY35 = counter
            loadWavAsset(assetMgr, preset.key35, KEY35, PAN_KEY35)
            counter+=1
        }
    }

    fun unloadWavAssets() {
        KEY11 = 0
        KEY12 = 0
        KEY13 = 0
        KEY14 = 0
        KEY15 = 0
        KEY21 = 0
        KEY22 = 0
        KEY23 = 0
        KEY24 = 0
        KEY25 = 0
        KEY31 = 0
        KEY32 = 0
        KEY33 = 0
        KEY34 = 0
        KEY35 = 0
        unloadWavAssetsNative()
    }

    private fun loadWavAsset(assetMgr: AssetManager, assetName: String, index: Int, pan: Float) {
        try {
            val assetFD = assetMgr.openFd(assetName)
            val dataStream = assetFD.createInputStream()
            val dataLen = assetFD.getLength().toInt()
            val dataBytes = ByteArray(dataLen)
            dataStream.read(dataBytes, 0, dataLen)
            loadWavAssetNative(dataBytes, index, pan)
            assetFD.close()
        } catch (ex: IOException) {
            Log.i(TAG, "IOException$ex")
        }
    }

    //берём файлы из папки music или из любой другой (менять soundDir)
    fun loadWavAssetsFromStorage(preset: FileManager.Preset) {
        var counter = 0
        Log.d("kek", preset.key11)
        if(preset.key11 != "") {
            KEY11 = counter
            loadWavFromFile(preset.key11, KEY11, PAN_KEY11)
            counter+=1
        }
        if(preset.key12 != "") {
            KEY12 = counter
            loadWavFromFile(preset.key12, KEY12, PAN_KEY12)
            counter+=1
        }
        if(preset.key13 != "") {
            KEY13 = counter
            loadWavFromFile(preset.key13, KEY13, PAN_KEY13)
            counter+=1
        }
        if(preset.key14 != "") {
            KEY14 = counter
            loadWavFromFile(preset.key14, KEY14, PAN_KEY14)
            counter+=1
        }
        if(preset.key15 != "") {
            KEY15 = counter
            loadWavFromFile(preset.key15, KEY15, PAN_KEY15)
            counter+=1
        }
        if(preset.key21 != "") {
            KEY21 = counter
            loadWavFromFile(preset.key21, KEY21, PAN_KEY21)
            counter+=1
        }
        if(preset.key22 != "") {
            KEY22 = counter
            loadWavFromFile(preset.key22, KEY22, PAN_KEY22)
            counter+=1
        }
        if(preset.key23 != "") {
            KEY23 = counter
            loadWavFromFile(preset.key23, KEY23, PAN_KEY23)
            counter+=1
        }
        if(preset.key24 != "") {
            KEY24 = counter
            loadWavFromFile(preset.key24, KEY24, PAN_KEY24)
            counter+=1
        }
        if(preset.key25 != "") {
            KEY25 = counter
            loadWavFromFile(preset.key25, KEY25, PAN_KEY25)
            counter+=1
        }
        if(preset.key31 != "") {
            KEY31 = counter
            loadWavFromFile(preset.key31, KEY31, PAN_KEY31)
            counter+=1
        }
        if(preset.key32 != "") {
            KEY32 = counter
            loadWavFromFile(preset.key32, KEY32, PAN_KEY32)
            counter+=1
        }
        if(preset.key33 != "") {
            KEY33 = counter
            loadWavFromFile(preset.key33, KEY33, PAN_KEY33)
            counter+=1
        }
        if(preset.key34 != "") {
            KEY34 = counter
            loadWavFromFile(preset.key34, KEY34, PAN_KEY34)
            counter+=1
        }
        if(preset.key35 != "") {
            KEY35 = counter
            loadWavFromFile(preset.key35, KEY35, PAN_KEY35)
            counter+=1
        }
    }

    fun loadWavFromFile(filePath: String, index: Int, pan: Float) {
        try {
            val file = File(filePath)
            val dataBytes = file.readBytes()
            loadWavAssetNative(dataBytes, index, pan)
        } catch (ex: IOException) {
            Log.i(TAG, "IOException: $ex")
        }
    }

    private external fun setupAudioStreamNative(numChannels: Int)
    private external fun startAudioStreamNative()
    private external fun teardownAudioStreamNative()

    private external fun loadWavAssetNative(wavBytes: ByteArray, index: Int, pan: Float)
    private external fun unloadWavAssetsNative()

    external fun trigger(drumIndex: Int)
    external fun stopTrigger(drumIndex: Int)


    external fun getOutputReset() : Boolean
    external fun clearOutputReset()

    external fun restartStream()
}
