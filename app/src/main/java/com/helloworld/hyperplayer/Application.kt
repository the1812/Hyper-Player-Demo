package com.helloworld.hyperplayer

import android.app.Application
import android.content.Context

class Application : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        appContext = applicationContext
    }
    companion object
    {
        private lateinit var appContext: Context
        val context
            get() = appContext
    }
}