package com.helloworld.hyperplayer.model

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import com.helloworld.hyperplayer.Application
import kotlin.math.min

class Player(val seekBar: SeekBar, val textTime: TextView)
{
    private val postInterval = 1000L
    private val handler = Handler()
    private lateinit var updateUi: Runnable
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var playingMusic: Music? = null

    var autoStart = true
    var playbackOption = PlaybackOption.PlaylistLoop
        set(value)
        {
            field = value
            updatePlaybackOption()
        }
    var playlist: Playlist = Playlist()
        private set
    var onChangeMusic: ((music: Music) -> Unit)? = null

    init
    {
        updateUi = Runnable {
            seekBar.post {
                seekBar.progress = mediaPlayer.currentPosition
            }
            textTime.post {
                val text = "${getTime(min(mediaPlayer.currentPosition, mediaPlayer.duration))} / ${getTime(mediaPlayer.duration)}"
                textTime.text = text
            }
            handler.postDelayed(updateUi, postInterval)
        }
    }

    fun playMusic(music: Music)
    {
        openFile(music.path)
    }
    fun openPlaylist(playlist: Playlist)
    {
        this.playlist = playlist
        val music = playlist.first()
        play(music)
    }
    private fun openFile(path: String)
    {
        val playlist = Playlist(Music(path))
        openPlaylist(playlist)
    }
    private fun play(music: Music)
    {
        destroy()
        playingMusic = music
        mediaPlayer = MediaPlayer.create(Application.context, Uri.parse(music.path))
        mediaPlayer.setOnPreparedListener {
            seekBar.progress = 0
            seekBar.max = mediaPlayer.duration
            if (autoStart)
            {
                start()
            }
            onChangeMusic?.invoke(music)
            updatePlaybackOption()
        }
        mediaPlayer.setOnCompletionListener {
            next()
        }
    }
    private fun updatePlaybackOption()
    {
        mediaPlayer.isLooping = playbackOption == PlaybackOption.SingleLoop
    }
    fun stopUpdateUi() = handler.removeCallbacks(updateUi)
    fun startUpdateUi() = updateUi.run()
    fun destroy()
    {
        mediaPlayer.stop()
        mediaPlayer.reset()
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
    fun next(statusUpdate: ((hasMusic: Boolean) -> Unit)? = null)
    {
        History.push(playingMusic!!, playlist)
        val nextMusic = playbackOption.nextMusic(playlist, playingMusic)
        statusUpdate?.invoke(nextMusic != null)
        if (nextMusic != null)
        {
            play(nextMusic)
        }
    }
    fun previous(statusUpdate: ((hasMusic: Boolean) -> Unit)? = null)
    {
        statusUpdate?.invoke(!History.isEmpty)
        if (!History.isEmpty)
        {
            val historyItem = History.pop()
            playlist = historyItem.playlist
            play(historyItem.music)
        }
    }
    val isPlaying
        get() = mediaPlayer.isPlaying
    val duration
        get() = mediaPlayer.duration

}
