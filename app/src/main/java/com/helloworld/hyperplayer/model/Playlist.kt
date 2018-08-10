package com.helloworld.hyperplayer.model

import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application
import java.util.*

class Playlist(var name: String) : Iterable<Music>
{
    private val preferenceFileName
        get() = getPreferenceFileName(name)
    private val musics = mutableListOf<Music>()
    val saved: Boolean
        get()
        {
            return Preferences.exists(preferenceFileName) && !name.startsWith(tempPrefix)
        }

    override fun iterator(): Iterator<Music> = musics.iterator()

    init
    {
        if (saved)
        {
            val musics = Preferences.loadStringSet(preferenceFileName, preferenceKey).map { Music(it) }
            if (musics.isNotEmpty())
            {
                this.musics.addAll(musics)
            }
        }
    }
    constructor(vararg musics: Music) : this(tempPrefix + UUID.randomUUID().toString().toUpperCase())
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
    fun rename(newName: String)
    {
        Preferences.delete(preferenceFileName)
        History.renamePlaylist(name, newName)
        name = newName
        save()
    }
    companion object
    {
        const val tempPrefix = "__temp__"
        private const val preferenceKey = "musics"
        private fun getPreferenceFileName(name: String) = "$name.playlist"
    }
}