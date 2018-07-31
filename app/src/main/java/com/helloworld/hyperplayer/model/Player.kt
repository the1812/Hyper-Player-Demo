package com.helloworld.hyperplayer.model

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.helloworld.hyperplayer.Application
import java.util.*
import java.util.concurrent.TimeUnit

class Player(val seekBar: SeekBar, val textTime: TextView)
{
    private val postInterval = 1000L
    private val handler = Handler()
    private lateinit var updateUi: Runnable
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var playingMusic: Music? = null

    var playbackOption = PlaybackOption.PlaylistLoop
        set(value)
        {
            field = value
            updatePlaybackOption()
        }
    var playlist: Playlist = Playlist()
        set(value)
        {
            field = value
            openPlaylist(value)
        }
    var onChangeMusic: ((music: Music) -> Unit)? = null

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

    fun playMusic(music: Music)
    {
        openFile(music.path)
    }
    private fun openFile(path: String)
    {
        val playlist = Playlist(Music(path))
        openPlaylist(playlist)
    }
    private fun openPlaylist(playlist: Playlist)
    {
        val music = playlist.first()
        play(music)
    }
    private fun play(music: Music)
    {
        destroy()
        playingMusic = music
        onChangeMusic?.invoke(music)
        mediaPlayer = MediaPlayer.create(Application.context, Uri.parse(music.path))
        mediaPlayer.setOnPreparedListener {
            seekBar.progress = 0
            seekBar.max = mediaPlayer.duration
            start()
            updatePlaybackOption()
        }
    }
    private fun updatePlaybackOption()
    {
        when (playbackOption)
        {
            PlaybackOption.PlaylistLoop ->
            {
                mediaPlayer.isLooping = false
                mediaPlayer.setOnCompletionListener {
                    if (playingMusic != playlist.last())
                    {
                        val nextMusicIndex = playlist.indexOf(playingMusic) + 1
                        if (nextMusicIndex >= playlist.count())
                        {
                            Log.e("Player", "Index unexpectedly out of range!")
                        }
                        else
                        {
                            val nextMusic = playlist.elementAt(nextMusicIndex)
                            play(nextMusic)
                        }
                    }
                    else
                    {
                        play(playlist.first())
                    }
                }
            }
            PlaybackOption.SingleLoop ->
            {
                mediaPlayer.isLooping = true
                mediaPlayer.setOnCompletionListener(null)
            }
            PlaybackOption.Random ->
            {
                mediaPlayer.isLooping = false
                mediaPlayer.setOnCompletionListener {
                    val currentIndex = playlist.indexOf(playingMusic)
                    var index = 0
                    do
                    {
                        index = Random().nextInt(playlist.count())
                    }
                    while (index == currentIndex)
                    val nextMusic = playlist.elementAt(index)
                    play(nextMusic)
                }
            }
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
