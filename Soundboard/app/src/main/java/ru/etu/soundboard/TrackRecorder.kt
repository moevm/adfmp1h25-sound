package ru.etu.soundboard

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

data class TrackEvent(
    val soundId: Int, // ID звука (например, SoundPlayer.BASSDRUM)
    val timestamp: Long // Время нажатия (в миллисекундах)
)

class TrackRecorder {

    private val events = mutableListOf<TrackEvent>()
    private var startTime: Long = 0
    private var isRecording = false

    fun startRecording() {
        events.clear()
        startTime = System.currentTimeMillis()
        isRecording = true
    }

    fun isRecEmpty(): Boolean
    {
        return events.isEmpty()
    }

    fun isRec(): Boolean
    {
        return isRecording
    }

    fun stopRecording() {
        isRecording = false
    }

    fun recordEvent(soundId: Int) {
        if (isRecording) {
            val currentTime = System.currentTimeMillis() - startTime
            events.add(TrackEvent(soundId, currentTime))
        }
    }

    fun getEvents(): List<TrackEvent> {
        return events.toList()
    }

    fun clear() {
        events.clear()
    }
}