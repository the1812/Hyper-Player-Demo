package com.helloworld.hyperplayer.model

import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.helloworld.hyperplayer.Application
import java.util.*

object History
{
    private val historyStack = Stack<HistoryItem>()
    private const val preferenceKey = "history"
    const val historyFileName = "history.pref"
    val musicArray: Array<Music>
        get()
        {
            val array = historyStack.map { it.music }.toTypedArray()
            array.reverse()
            return array
        }
    val isEmpty
        get() = historyStack.isEmpty()

    fun push(music: Music, playlist: Playlist): HistoryItem = historyStack.push(HistoryItem(music, playlist))
    fun pop(): HistoryItem = historyStack.pop()
    operator fun get(index: Int): HistoryItem
    {
        return historyStack.elementAt(index)
    }
    fun renamePlaylist(oldName: String, newName: String)
    {
        historyStack.filter { it.playlist.name == oldName }
            .forEach { it.playlist.name = newName }
        Preferences.editString(historyFileName, Player.lastMusicKey) {
            if (it.contains(HistoryItem.delimiter))
            {
                val (path, name) = it.split(HistoryItem.delimiter)
                "$path${HistoryItem.delimiter}${name.replace(oldName, newName)}"
            }
            else
            {
                it
            }
        }
        save()
    }
    fun save()
    {
        historyStack.forEach { it.playlist.save() }
        Preferences.save(historyFileName, preferenceKey, historyStack.map { it.toPreferenceString() })
    }
    fun load()
    {
        historyStack.clear()
        Preferences.loadStringSet(historyFileName, preferenceKey)
            .map { HistoryItem.fromPreferenceString(it) }
            .asReversed()
            .forEach {
                historyStack.push(it)
            }
    }
}
data class HistoryItem(val music: Music, val playlist: Playlist)
{
    fun toPreferenceString(): String
    {
        return "${music.path}$delimiter${playlist.toPreferenceString()}"
    }
    companion object
    {
        const val delimiter = "|"
        fun fromPreferenceString(preferenceString: String): HistoryItem
        {
            val (path, playlistName) = preferenceString.split(delimiter)
            val music = Music(path)
            val playlist = Playlist(playlistName)
            return HistoryItem(music, playlist)
        }
    }
}