package com.helloworld.hyperplayer.model

import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application
import java.util.*

class Playlist(val name: String) : Iterable<Music>
{
    private val preferenceFileName
        get() = getPreferenceFileName(name)
    private val musics = mutableListOf<Music>()
    override fun iterator(): Iterator<Music> = musics.iterator()

    init
    {
        // TODO: Read playlist from preferences
    }
    constructor(vararg musics: Music) : this(UUID.randomUUID().toString().toUpperCase())
    {
        this.musics.addAll(musics)
    }
    fun toPreferenceString(): String
    {
        return name
    }
    fun save()
    {
        Preferences.save(preferenceFileName, preferenceKey, musics.map { it.path })
    }
    companion object
    {
        private const val preferenceKey = "musics"
        private fun getPreferenceFileName(name: String) = "$name.playlist"
        fun fromPreferenceString(name: String): Playlist
        {
            val preference = Application.context.getSharedPreferences(getPreferenceFileName(name), MODE_PRIVATE)
            val musics = preference.getStringSet(preferenceKey, emptySet()).map { Music(it) }
            val playlist = Playlist(name)
            playlist.musics.addAll(musics)
            return playlist
        }
    }
}