package com.helloworld.hyperplayer.model

import java.util.*

object History
{
    init
    {
        // TODO: read history from preferences
    }
    private val historyStack = Stack<HistoryItem>()
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
}
data class HistoryItem(val music: Music, val playlist: Playlist)