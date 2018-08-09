package com.helloworld.hyperplayer.model

import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application
import java.util.*

class Playlist(var name: String) : Iterable<Music>
{
    private val preferenceFileName
        get() = getPreferenceFileName(name)
    private val musics = mutableListOf<Music>()
    var saved: Boolean
        private set

    override fun iterator(): Iterator<Music> = musics.iterator()

    init
    {
        saved = if (Preferences.exists(name))
        {
            val musics = Preferences.loadStringSet(getPreferenceFileName(name), preferenceKey).map { Music(it) }
            if (musics.isNotEmpty())
            {
                this.musics.addAll(musics)
            }
            true
        }
        else
        {

            false
        }
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
    fun rename(newName: String): Boolean
    {
        return if (Preferences.exists(newName))
        {
            false
        }
        else
        {
            Preferences.delete(name)
            name = newName
            save()
            true
        }
    }
    companion object
    {
        private const val preferenceKey = "musics"
        private fun getPreferenceFileName(name: String) = "$name.playlist"
    }
}