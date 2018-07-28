package com.helloworld.hyperplayer.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.webkit.URLUtil
import com.helloworld.hyperplayer.Application
import com.helloworld.hyperplayer.view.PlayerActivity

data class MusicInfo(
    val title: String,
    val artist: String,
    val album: String,
    val albumImage: Bitmap?)
{
    companion object
    {
        val default = MusicInfo("", "Unknown artist", "Unknown album", null)
    }
}

fun getMusicInfo(path: String): MusicInfo
{
    val metadata = MediaMetadataRetriever()
    metadata.setDataSource(Application.context, Uri.parse(path))

    val title = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        ?: URLUtil.guessFileName(path, null, null)
            .dropLastWhile { it != '.' }.dropLast(1)
    val artist = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: MusicInfo.default.artist
    val album = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: MusicInfo.default.album
    val image = BitmapFactory.decodeByteArray(metadata.embeddedPicture, 0, metadata.embeddedPicture.size)
    return MusicInfo(title, artist, album, image)
}