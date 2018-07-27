package com.helloworld.hyperplayer.model

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import com.helloworld.hyperplayer.Application

class Player(val seekBar: SeekBar, val textTime: TextView)
{
    private val postInterval = 1000L
    private val handler = Handler()
    private lateinit var updateUi: Runnable
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    init
    {
        updateUi = Runnable {
            seekBar.post {
                seekBar.progress = mediaPlayer.currentPosition
            }
            handler.postDelayed(updateUi, postInterval)
        }
    }

    var mediaSource: String = ""
        set(value)
        {
            mediaPlayer = MediaPlayer.create(Application.context, Uri.parse(value))
            mediaPlayer.setOnPreparedListener {
                seekBar.progress = 0
                seekBar.max = mediaPlayer.duration
                updateUi.run()
                mediaPlayer.start()
            }
        }

    fun destroy()
    {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
    fun start()
    {
        mediaPlayer.start()
        updateUi.run()
    }
    fun pause()
    {
        mediaPlayer.pause()
        handler.removeCallbacks(updateUi)
    }
    fun stop()
    {
        mediaPlayer.stop()
        handler.removeCallbacks(updateUi)
    }
    fun seekTo(position: Int)
    {
        mediaPlayer.seekTo(position)
    }
    val isPlaying
        get() = mediaPlayer.isPlaying

}
