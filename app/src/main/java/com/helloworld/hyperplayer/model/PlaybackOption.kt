package com.helloworld.hyperplayer.model

import com.helloworld.hyperplayer.R
import java.util.*

enum class PlaybackOption
{
    PlaylistLoop {
        override fun nextMusic(playlist: Playlist, playingMusic: Music?): Music?
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
        override val nextOption
            get() = SingleLoop
        override val resourceId
            get() = R.drawable.ic_repeat
    },
    SingleLoop {
        override fun nextMusic(playlist: Playlist, playingMusic: Music?): Music? = playingMusic
        override val nextOption
            get() = Shuffle
        override val resourceId
            get() = R.drawable.ic_repeat_one
    },
    Shuffle {
        override fun nextMusic(playlist: Playlist, playingMusic: Music?): Music?
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
        override val nextOption
            get() = PlaylistLoop
        override val resourceId
            get() = R.drawable.ic_shuffle
    };

    abstract fun nextMusic(playlist: Playlist, playingMusic: Music?): Music?
    abstract val nextOption: PlaybackOption
    abstract val resourceId: Int
}