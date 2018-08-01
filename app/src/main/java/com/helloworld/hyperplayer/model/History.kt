package com.helloworld.hyperplayer.model

import java.util.*

object History
{
    init
    {
        // TODO: read history from preferences
    }
    private val historyStack = Stack<Music>()
    val musicList
        get() = historyStack.toTypedArray()
    val isEmpty
        get() = historyStack.isEmpty()

    fun push(music: Music) = historyStack.push(music)
    fun pop() = historyStack.pop()
    operator fun get(index: Int): Music
    {
        return historyStack.elementAt(index)
    }
}