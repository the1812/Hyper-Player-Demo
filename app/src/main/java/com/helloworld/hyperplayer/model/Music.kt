package com.helloworld.hyperplayer.model

class Music(val path: String)
{
    val info: MusicInfo = getMusicInfo(path)
}