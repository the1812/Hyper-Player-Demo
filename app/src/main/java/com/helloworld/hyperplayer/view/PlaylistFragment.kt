package com.helloworld.hyperplayer.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.helloworld.hyperplayer.R

class PlaylistFragment : Fragment(), UpdateTitleFragment
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        updateTitle()
    }

    override fun updateTitle()
    {
        activity?.title = getString(R.string.playlist)
    }
}
