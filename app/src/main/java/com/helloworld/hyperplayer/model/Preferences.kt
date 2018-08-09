package com.helloworld.hyperplayer.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application

object Preferences
{
    fun exists(filename: String): Boolean
    {
        return Application.context.getSharedPreferences(filename, MODE_PRIVATE) != null
    }
    fun delete(filename: String)
    {
        Application.context.deleteSharedPreferences(filename)
    }
    fun save(filename: String, key: String, data: Iterable<String>)
    {
        val editor = Application.context.getSharedPreferences(filename, MODE_PRIVATE).edit()
        editor.putStringSet(key, data.toSet())
        editor.apply()
    }
    fun save(filename: String, key: String, data: String)
    {
        val editor = Application.context.getSharedPreferences(filename, MODE_PRIVATE).edit()
        editor.putString(key, data)
        editor.apply()
    }
    fun loadStringSet(filename: String, key: String): Iterable<String>
    {
        val preference = Application.context.getSharedPreferences(filename, MODE_PRIVATE)
        return preference.getStringSet(key, emptySet())
    }
    fun loadString(filename: String, key: String): String
    {
        val preference = Application.context.getSharedPreferences(filename, MODE_PRIVATE)
        return preference.getString(key, "")
    }
}