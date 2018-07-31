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
import com.helloworld.hyperplayer.model.*
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment()
{
    private lateinit var player: Player
    private var info: MusicInfo = MusicInfo.default
    private val playbackNextMap = mapOf(
        PlaybackOption.PlaylistLoop to PlaybackOption.SingleLoop,
        PlaybackOption.SingleLoop to PlaybackOption.Random,
        PlaybackOption.Random to PlaybackOption.PlaylistLoop
    )
    private val playbackResourceMap = mapOf(
        PlaybackOption.PlaylistLoop to R.drawable.ic_repeat,
        PlaybackOption.SingleLoop to R.drawable.ic_repeat_one,
        PlaybackOption.Random to R.drawable.ic_shuffle
    )

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.d("fragment", toString())
        player = Player(seekBar, textTime)

        buttonPlayPause.isEnabled = false
        seekBar.isEnabled = false
        buttonNext.isEnabled = false
        buttonPrevious.isEnabled = false

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
        buttonPlaybackOption.setOnClickListener {
            player.playbackOption = playbackNextMap[player.playbackOption]!!
            buttonPlaybackOption.setImageResource(playbackResourceMap[player.playbackOption]!!)
        }
        buttonNext.setOnClickListener {
            player.next()
        }
    }

    fun openPlaylist(vararg path: String)
    {
        val playlist = Playlist(*(path.map { Music(it) }.toTypedArray()))
        player.playlist = playlist
        player.onChangeMusic = this::updateMusicInfo

        buttonPlayPause.isEnabled = true
        seekBar.isEnabled = true
        textOpenFileHint.visibility = View.GONE
        buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
        buttonNext.isEnabled = true
        buttonPrevious.isEnabled = true

        updateMusicInfo(playlist.first())
    }
    private fun updateMusicInfo(music: Music)
    {
        with(music.info) {
            textTitle.text = title
            textArtist.text = artist
            textAlbum.text = album
            imageAlbum.setImageBitmap(albumImage)
        }
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
