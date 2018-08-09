package com.helloworld.hyperplayer.view

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar

import com.helloworld.hyperplayer.R
import com.helloworld.hyperplayer.model.*
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment()
{
    private val historyRequestCode = 2
    private lateinit var player: Player
    private var info: MusicInfo = MusicInfo.default

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.d("fragment", toString())
        player = Player(seekBar, textTime)
        player.onChangePlaylist = this::updatePlaylistStatus

        disableUi()
        setupListener()
        loadLastMusic()
    }

    private fun disableUi()
    {
        buttonPlayPause.isEnabled = false
        seekBar.isEnabled = false
        textOpenFileHint.visibility = View.VISIBLE
        buttonNext.isEnabled = false
        buttonPrevious.isEnabled = false
    }
    private fun enableUi()
    {
        buttonPlayPause.isEnabled = true
        seekBar.isEnabled = true
        textOpenFileHint.visibility = View.GONE
        buttonNext.isEnabled = true
        buttonPrevious.isEnabled = true
    }
    private fun updatePlaylistStatus(playlist: Playlist)
    {
        if (activity is PlayerActivity)
        {
            val item = (activity as PlayerActivity).optionsMenu?.findItem(R.id.action_save_playlist)
            item?.isVisible = !playlist.saved
        }
    }
    private fun setupListener()
    {
        buttonPlayPause.setOnClickListener {
            if (player.isPlaying)
            {
                player.pause()
                showPlayButton()
            }
            else
            {
                player.start()
                showPauseButton()
            }
        }
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener
            {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean)
                {
                    val text = "${getTime(progress)} / ${getTime(player.duration)}"
                    textTime.text = text
                }

                override fun onStartTrackingTouch(seekbar: SeekBar?)
                {
                    player.stopUpdateUi()
                }

                override fun onStopTrackingTouch(seekbar: SeekBar?)
                {
                    player.seekTo(seekBar.progress)
                    player.startUpdateUi()
                }
            }
        )
        buttonPlaybackOption.setOnClickListener {
            player.playbackOption = player.playbackOption.nextOption
            buttonPlaybackOption.setImageResource(player.playbackOption.resourceId)
        }
        buttonNext.setOnClickListener {
            player.next { hasMusic ->
                updateButtons(hasMusic)
            }
        }
        buttonPrevious.setOnClickListener {
            player.previous { hasMusic ->
                updateButtons(hasMusic)
            }
        }
        buttonHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivityForResult(intent, historyRequestCode)
        }
    }

    fun openPlaylist(vararg path: String)
    {
        val playlist = Playlist(*(path.map { Music(it) }.toTypedArray()))
        player.autoStart = true
        player.openPlaylist(playlist)
        player.onChangeMusic = this::updateMusicInfo

        enableUi()
        showPauseButton()
        updateMusicInfo(playlist.first())
    }

    private fun loadLastMusic()
    {
        player.autoStart = false
        val music = player.loadLastMusic()
        if (music != null)
        {
            enableUi()
            showPlayButton()
            updateMusicInfo(music)
        }
    }
    private fun updateButtons(playing: Boolean)
    {
        if (playing)
        {
            showPauseButton()
        }
        else
        {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle)
        }
    }

    private fun showPlayButton()
    {
        buttonPlayPause.setImageResource(R.drawable.ic_play_circle)
    }

    private fun showPauseButton()
    {
        buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
    }

    private fun updateButtons()
    {
        updateButtons(player.isPlaying)
    }
    private fun updateMusicInfo(music: Music)
    {
        with(music.info) {
            textTitle.text = title
            textArtist.text = artist
            textAlbum.text = album
            imageAlbum.setImageBitmap(albumImage)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {
            historyRequestCode ->
            {
                if (resultCode == RESULT_OK)
                {
                    val goBackTimes = data?.getIntExtra("goBackTimes", 0) ?: 0
                    repeat(goBackTimes) {
                        buttonPrevious.performClick()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item != null)
        {
            when (item.itemId)
            {
                R.id.action_save_playlist ->
                {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle(R.string.save_playlist)
                    builder.setMessage(R.string.save_playlist_description)
                    builder.setCancelable(true)
                    val view = layoutInflater.inflate(R.layout.dialog_save_playlist, null)
                    builder.setView(view)
                    builder.setPositiveButton(R.string.ok) { dialog, _ ->
                        val textBox = view.findViewById<EditText>(R.id.textNewName)
                        val newName = textBox.text.toString()
                        if (newName.isBlank())
                        {
                            Snackbar.make(root_layout, R.string.save_playlist_empty_name, Snackbar.LENGTH_SHORT).show()
                        }
                        else
                        {
                            if (player.playlist.name != newName)
                            {
                                player.playlist.rename(newName)
                            }
                            dialog.dismiss()
                        }
                    }
                    builder.setNegativeButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy()
    {
        super.onDestroy()
        player.destroy()
    }
    override fun onPause()
    {
        super.onPause()
        player.saveAsLastMusic()
    }
}
