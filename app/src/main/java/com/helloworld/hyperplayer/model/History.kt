package com.helloworld.hyperplayer.model

import java.util.*

object History
{
    init
    {
        // TODO: read history from preferences
    }
    private val historyStack = Stack<Music>()
    val musicArray: Array<Music>
        get()
        {
            val array = historyStack.toTypedArray()
            array.reverse()
            return array
        }
    val isEmpty
        get() = historyStack.isEmpty()

    fun push(music: Music): Music = historyStack.push(music)
    fun pop(): Music = historyStack.pop()
    operator fun get(index: Int): Music
    {
        return historyStack.elementAt(index)
    }
}