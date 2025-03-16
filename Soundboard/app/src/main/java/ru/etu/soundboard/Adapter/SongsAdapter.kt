package ru.etu.soundboard.Adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ru.etu.soundboard.Model.SongModel
import ru.etu.soundboard.Player
import ru.etu.soundboard.R

class SongsAdapter(private val arrayList: ArrayList<SongModel>, private val context: Context?) :
    RecyclerView.Adapter<SongsAdapter.MyViewHolder>() {

    class MyViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.icon_song_1)
        val songName: TextView = itemView.findViewById(R.id.songName)
        val dateRelease: TextView = itemView.findViewById(R.id.release)
        val duration: TextView = itemView.findViewById(R.id.duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val views = LayoutInflater.from(parent.context).inflate(R.layout.song_view, parent, false)
        return MyViewHolder(views)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //This method retrieves data and displays inside the view (i.e. Card) while binding
       // val byteArray = getAlbumArt(arrayList[position].image)

        //Glide Library has a function to convert byte array and display as Bitmap inside the target or ImageView.
       // Glide.with(context!!).asBitmap().load(byteArray).centerCrop()
       //     .placeholder(R.drawable.music_note).into(holder.imageView)


        holder.songName.text = arrayList[position].name
        holder.dateRelease.text = arrayList[position].date
        holder.duration.text = arrayList[position].duration

        holder.itemView.setOnClickListener {

            val intent = Intent(context, Player::class.java)
            intent.putExtra("name", holder.songName.text.toString())
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    //AlbumArt - we use metaDataRetriever to retrieve the Image in ByteArray from
    // Uri provided and returns that ByteArray.
    private fun getAlbumArt(uri: Uri): ByteArray? {

        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(uri.toString())

        val result: ByteArray? = metadataRetriever.embeddedPicture
        metadataRetriever.release()

        return result
    }

}