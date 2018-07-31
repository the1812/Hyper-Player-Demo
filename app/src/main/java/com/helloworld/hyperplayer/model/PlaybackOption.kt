package com.helloworld.hyperplayer.model

import android.util.Log
import java.util.*

enum class PlaybackOption
{
    PlaylistLoop {
        override fun next(playlist: Playlist, playingMusic: Music?): Music?
        {
            return if (playingMusic != playlist.last())
            {
                val nextMusicIndex = playlist.indexOf(playingMusic) + 1
                playlist.elementAt(nextMusicIndex)
            }
            else
            {
                playlist.first()
            }
        }
    },
    SingleLoop {
        override fun next(playlist: Playlist, playingMusic: Music?): Music? = playingMusic
    },
    Random {
        override fun next(playlist: Playlist, playingMusic: Music?): Music?
        {
            val currentIndex = playlist.indexOf(playingMusic)
            var index = 0
            do
            {
                index = Random().nextInt(playlist.count())
            }
            while (index == currentIndex)
            return playlist.elementAt(index)
        }
    };

    abstract fun next(playlist: Playlist, playingMusic: Music?): Music?
}