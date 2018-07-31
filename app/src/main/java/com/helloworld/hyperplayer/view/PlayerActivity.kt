package com.helloworld.hyperplayer.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.helloworld.hyperplayer.R
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.app_bar_player.*
import kotlinx.android.synthetic.main.content_player.*

class PlayerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private val pickFileCode = 1
    private lateinit var fragments: Map<Int, Class<out Fragment>>
    private lateinit var fragmentTitles: Map<Class<out Fragment>, String>

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


        fragments = mapOf(
            R.id.nav_player to PlayerFragment::class.java,
            R.id.nav_playlist to PlaylistFragment::class.java
        )
        fragmentTitles = mapOf(
            PlayerFragment::class.java to getString(R.string.player),
            PlaylistFragment::class.java to getString(R.string.playlist)
        )
        switchFragment(PlayerFragment::class.java)
    }
    private fun switchFragment(fragmentClass: Class<out Fragment>)
    {
        val manager = supportFragmentManager
        var fragment = manager.findFragmentByTag(fragmentClass.name)
        val transaction  = manager.beginTransaction()

        if (fragment == null)
        {
            fragment = fragmentClass.newInstance() as Fragment
            transaction.add(R.id.fragment, fragment, fragmentClass.name)
        }
        else
        {
            transaction.replace(R.id.fragment, fragment, fragmentClass.name)
        }
        transaction.commit()

        title = fragmentTitles[fragmentClass]
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
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.type = "audio/*"
                startActivityForResult(intent, pickFileCode)
            }
            else ->
            {
                if (fragments.containsKey(item.itemId))
                {
                    switchFragment(fragments[item.itemId]!!)
                }
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
                pickFileCode ->
                {
                    val fragment = supportFragmentManager.findFragmentByTag(fragments[R.id.nav_player]!!.name) as PlayerFragment
                    if (data.clipData != null)
                    {
                        fragment.openPlaylist(*(0..(data.clipData.itemCount - 1))
                            .map { data.clipData.getItemAt(it).uri.toString() }
                            .toTypedArray())
                    }
                    else
                    {
                        fragment.openPlaylist(data.dataString)
                    }
                }
            }
        }
    }

}
