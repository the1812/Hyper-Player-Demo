package com.helloworld.hyperplayer.view

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import android.widget.SeekBar
import com.helloworld.hyperplayer.R
import com.helloworld.hyperplayer.model.getMusicInfo
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.app_bar_player.*
import kotlinx.android.synthetic.main.content_player.*

class PlayerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private lateinit var player: MediaPlayer
    private val pickFileCode = 1
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        updateSeekBar = Runnable {
            runOnUiThread {
                seekBar.progress = player.currentPosition
            }
            handler.postDelayed(updateSeekBar, 1000)
        }

        nav.setNavigationItemSelectedListener(this)
        buttonPlayPause.visibility = View.GONE
        seekBar.visibility = View.GONE
        buttonPlayPause.setOnClickListener {
            if (player.isPlaying)
            {
                player.pause()
                handler.removeCallbacks(updateSeekBar)
            }
            else
            {
                player.start()
                updateSeekBar?.run()
            }
        }

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener
            {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {}
                override fun onStartTrackingTouch(seekbar: SeekBar?) {}
                override fun onStopTrackingTouch(seekbar: SeekBar?) = player.seekTo(seekBar.progress)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else                 -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        // Handle navigation view item clicks here.
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

    private var updateSeekBar: Runnable? = null

    private fun openFile(path: String)
    {
        player = MediaPlayer.create(this, Uri.parse(path))
        player.setOnPreparedListener {
            seekBar.max = player.duration
            updateSeekBar?.run()
            player.start()
            buttonPlayPause.visibility = View.VISIBLE
            seekBar.visibility = View.VISIBLE
        }
        val info = getMusicInfo(path)
        title = info.title
        textArtist.text = info.artist
        textAlbum.text = info.album
        imageAlbum.setImageBitmap(info.albumImage)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        player.stop()
        player.release()
    }
}
