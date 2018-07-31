package com.helloworld.hyperplayer.model

class Playlist(val name: String) : Iterable<Music>
{
    private val musics = mutableListOf<Music>()
    override fun iterator(): Iterator<Music> = musics.iterator()

    init
    {
        // TODO: Read playlist from preferences
    }
    constructor(vararg musics: Music) : this("Untitled")
    {
        this.musics.addAll(musics)
    }

}