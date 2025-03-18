package ru.etu.soundboard.Adapter

//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
import android.content.Context
//import android.content.Context.MODE_PRIVATE
//import android.content.SharedPreferences
//import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object FileManager{
    private var conf: Configuration? = null
//    private val gson: Gson = GsonBuilder()
//        .serializeNulls()
//        .setPrettyPrinting()
//        .create()

    data class Configuration(
        val drums: Preset,
        val keys: Preset,
        val set1: Preset,
        val set2: Preset,
        val set3: Preset
    )

    data class Preset(
        var key11: String,
        var key12: String,
        var key13: String,
        var key14: String,
        var key15: String,
        var key21: String,
        var key22: String,
        var key23: String,
        var key24: String,
        var key25: String,
        var key31: String,
        var key32: String,
        var key33: String,
        var key34: String,
        var key35: String,
    )

    fun setConf(config: Configuration) {
        this.conf = config
    }

    fun getConf(): Configuration? {
        val config = conf?:return null
        return config
    }

    fun readFile(context: Context): String {
        try {
            val file = context.assets.open("presets.json")
            println(file)
            val bufferedReader = BufferedReader(InputStreamReader(file))
            val stringBuilder = StringBuilder()
            bufferedReader.useLines { lines ->
                lines.forEach {
                    stringBuilder.append(it)
                }
            }
            val jsonString = stringBuilder.toString()
            return jsonString
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}