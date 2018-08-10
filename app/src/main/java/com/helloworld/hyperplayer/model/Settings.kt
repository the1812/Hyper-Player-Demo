package com.helloworld.hyperplayer.model

object Settings
{
    private const val preferenceFilename = "settings.pref"
    var playbackOption = PlaybackOption.PlaylistLoop

    fun load()
    {
        val items = Preferences.loadAllString(preferenceFilename)

        playbackOption = PlaybackOption.valueOf(items[this::playbackOption.name] ?: PlaybackOption.PlaylistLoop.name)
    }
    fun save()
    {
        val items = mapOf(
            this::playbackOption.name to playbackOption.name
        )

        Preferences.save(preferenceFilename, items)
    }
}