package com.helloworld.hyperplayer.view

import android.app.Activity.RESULT_OK
import android.content.Intent
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
    private val historyRequestCode = 2
    private lateinit var player: Player
    private var info: MusicInfo = MusicInfo.default

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
            player.playbackOption = player.playbackOption.nextOption
            buttonPlaybackOption.setImageResource(player.playbackOption.resourceId)
        }
        buttonNext.setOnClickListener {
            player.next { hasMusic ->
                updateButtons(hasMusic)
            }
        }
        buttonPrevious.setOnClickListener {
            player.previous { hasMusic ->
                updateButtons(hasMusic)
            }
        }
        buttonHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivityForResult(intent, historyRequestCode)
        }
    }

    fun openPlaylist(vararg path: String)
    {
        val playlist = Playlist(*(path.map { Music(it) }.toTypedArray()))
        player.openPlaylist(playlist)
        player.onChangeMusic = this::updateMusicInfo

        buttonPlayPause.isEnabled = true
        seekBar.isEnabled = true
        textOpenFileHint.visibility = View.GONE
        buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
        buttonNext.isEnabled = true
        buttonPrevious.isEnabled = true

        updateMusicInfo(playlist.first())
    }
    private fun updateButtons(playing: Boolean)
    {
        if (playing)
        {
            buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
        }
        else
        {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle)
        }
    }
    private fun updateButtons()
    {
        updateButtons(player.isPlaying)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {
            historyRequestCode ->
            {
                if (resultCode == RESULT_OK)
                {
                    val goBackTimes = data?.getIntExtra("goBackTimes", 0) ?: 0
                    repeat(goBackTimes) {
                        buttonPrevious.performClick()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onDestroy()
    {
        super.onDestroy()
        player.destroy()
    }
}
