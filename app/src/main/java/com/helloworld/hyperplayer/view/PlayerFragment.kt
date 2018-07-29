package com.helloworld.hyperplayer.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.helloworld.hyperplayer.R
import com.helloworld.hyperplayer.model.MusicInfo
import com.helloworld.hyperplayer.model.Player
import com.helloworld.hyperplayer.model.getMusicInfo
import com.helloworld.hyperplayer.model.getTime
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment()
{
    private lateinit var player: Player
    private var info: MusicInfo = MusicInfo.default

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.d("fragment", toString())
        player = Player(seekBar, textTime)

        buttonPlayPause.isEnabled = false
        seekBar.isEnabled = false

        buttonPlayPause.setOnClickListener {
            if (player.isPlaying)
            {
                player.pause()
                buttonPlayPause.setImageResource(R.drawable.ic_play_circle)
            }
            else
            {
                player.start()
                buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
            }
        }
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener
            {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean)
                {
                    val text = "${getTime(progress)} / ${getTime(player.duration)}"
                    textTime.text = text
                }
                override fun onStartTrackingTouch(seekbar: SeekBar?)
                {
                    player.stopUpdateUi()
                }
                override fun onStopTrackingTouch(seekbar: SeekBar?)
                {
                    player.seekTo(seekBar.progress)
                    player.startUpdateUi()
                }
            }
        )
    }

    fun openFile(path: String)
    {
        player.mediaSource = path
        buttonPlayPause.isEnabled = true
        seekBar.isEnabled = true
        textOpenFileHint.visibility = View.GONE
        buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)

        info = getMusicInfo(path)
        textTitle.text = info.title
        textArtist.text = info.artist
        textAlbum.text = info.album
        imageAlbum.setImageBitmap(info.albumImage)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        player.destroy()
    }
}
