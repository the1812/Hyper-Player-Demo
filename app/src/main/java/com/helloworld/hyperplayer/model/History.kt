package com.helloworld.hyperplayer.model

import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application
import java.util.*

object History
{
    private val historyStack = Stack<HistoryItem>()
    private const val preferenceKey = "history"
    private const val historyFileName = "history.pref"
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
    fun save()
    {
        val editor = Application.context.getSharedPreferences(historyFileName, MODE_PRIVATE).edit()
        historyStack.forEach { it.playlist.save() }
        editor.putStringSet(preferenceKey, historyStack
            .map { it.toPreferenceString() }
            .toSet())
        editor.apply()
    }
    fun load()
    {
        val preference = Application.context.getSharedPreferences(historyFileName, MODE_PRIVATE)
        historyStack.clear()
        preference.getStringSet(preferenceKey, emptySet())
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
        return "${music.path}|${playlist.toPreferenceString()}"
    }
    companion object
    {
        fun fromPreferenceString(preferenceString: String): HistoryItem
        {
            val (path, playlistName) = preferenceString.split("|")
            val music = Music(path)
            val playlist = Playlist.fromPreferenceString(playlistName)
            return HistoryItem(music, playlist)
        }
    }
}