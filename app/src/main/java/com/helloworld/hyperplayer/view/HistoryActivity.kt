package com.helloworld.hyperplayer.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.helloworld.hyperplayer.R
import com.helloworld.hyperplayer.model.History
import com.helloworld.hyperplayer.model.Music
import com.helloworld.hyperplayer.model.MusicInfo
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity()
{
    inner class ViewHolder
    (val imageAlbum: ImageView,
     val textTitle: TextView,
     val textArtist: TextView,
     val textAlbum: TextView)
    {
        fun apply(musicInfo: MusicInfo)
        {
            with(musicInfo) {
                imageAlbum.setImageBitmap(albumImage)
                textTitle.text = title
                textArtist.text = artist
                textAlbum.text = album
            }
        }
    }

    private var viewHolder: ViewHolder? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val adapter = object : ArrayAdapter<Music>(this, R.layout.history_item, History.musicList)
        {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
            {
                val music = getItem(position)
                if (convertView == null)
                {
                    val view = LayoutInflater.from(context).inflate(R.layout.history_item, null)
                    viewHolder = ViewHolder(
                        view.findViewById(R.id.imageAlbum),
                        view.findViewById(R.id.textTitle),
                        view.findViewById(R.id.textArtist),
                        view.findViewById(R.id.textAlbum)
                    )
                    view.tag = viewHolder
                    viewHolder?.apply(music.info)
                    return view
                }
                else
                {
                    viewHolder = convertView.tag as ViewHolder
                    viewHolder?.apply(music.info)
                    return convertView
                }
            }
        }
        historyList.adapter = adapter
    }
}
