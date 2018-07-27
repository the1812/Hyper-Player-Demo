package com.helloworld.hyperplayer.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.helloworld.hyperplayer.R
import com.helloworld.hyperplayer.model.Player
import com.helloworld.hyperplayer.model.getMusicInfo
import com.helloworld.hyperplayer.model.getTime
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.app_bar_player.*
import kotlinx.android.synthetic.main.content_player.*

class PlayerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private val pickFileCode = 1
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        nav.setNavigationItemSelectedListener(this)
        title = getString(R.string.player)

        player = Player(seekBar, textTime)

        buttonPlayPause.isEnabled = false
        seekBar.isEnabled = false

        buttonPlayPause.setOnClickListener {
            if (player.isPlaying)
            {
                player.pause()
                buttonPlayPause.setImageResource(R.drawable.ic_play_circle)
            }
            else
            {
                player.start()
                buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)
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
            })
    }

    override fun onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else                 -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.nav_open    ->
            {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*"
                startActivityForResult(intent, pickFileCode)
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (data != null)
        {
            when (requestCode)
            {
                pickFileCode -> openFile(data.dataString)
            }
        }
    }

    private fun openFile(path: String)
    {
        player.mediaSource = path
        buttonPlayPause.isEnabled = true
        seekBar.isEnabled = true
        textOpenFileHint.visibility = View.GONE
        buttonPlayPause.setImageResource(R.drawable.ic_pause_circle)

        val info = getMusicInfo(path)
        title = info.title
        textArtist.text = info.artist
        textAlbum.text = info.album
        imageAlbum.setImageBitmap(info.albumImage)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        player.destroy()
    }
}
