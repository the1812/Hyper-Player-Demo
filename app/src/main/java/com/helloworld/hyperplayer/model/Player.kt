package com.helloworld.hyperplayer.model

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import com.helloworld.hyperplayer.Application
import java.util.concurrent.TimeUnit

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
            textTime.post {
                val text = "${getTime(mediaPlayer.currentPosition)} / ${getTime(mediaPlayer.duration)}"
                textTime.text = text
            }
            handler.postDelayed(updateUi, postInterval)
        }
    }
    var mediaSource: String = ""
        set(value)
        {
            destroy()
            mediaPlayer = MediaPlayer.create(Application.context, Uri.parse(value))
            mediaPlayer.setOnPreparedListener {
                seekBar.progress = 0
                seekBar.max = mediaPlayer.duration
                start()
            }
        }

    fun stopUpdateUi() = handler.removeCallbacks(updateUi)
    fun startUpdateUi() = updateUi.run()
    fun destroy()
    {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
    fun start()
    {
        mediaPlayer.start()
        startUpdateUi()
    }
    fun pause()
    {
        mediaPlayer.pause()
        stopUpdateUi()
    }
    fun stop()
    {
        mediaPlayer.stop()
        stopUpdateUi()
    }
    fun seekTo(position: Int)
    {
        mediaPlayer.seekTo(position)
    }
    val isPlaying
        get() = mediaPlayer.isPlaying
    val duration
        get() = mediaPlayer.duration

}
