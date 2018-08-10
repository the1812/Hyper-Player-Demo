package com.helloworld.hyperplayer.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.helloworld.hyperplayer.Application
import java.io.File

object Preferences
{
    fun exists(filename: String): Boolean
    {
        return File("${Application.context.filesDir.parent}/shared_prefs/$filename.xml").exists()
    }
    fun delete(filename: String)
    {
        if (exists(filename))
        {
            Application.context.deleteSharedPreferences(filename)
        }
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
    fun save(filename: String, map: Map<String, String>)
    {
        val editor = Application.context.getSharedPreferences(filename, MODE_PRIVATE).edit()
        map.forEach { editor.putString(it.key, it.value) }
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
    fun loadAllString(filename: String): Map<String, String>
    {
        val preference = Application.context.getSharedPreferences(filename, MODE_PRIVATE)
        val result = mutableMapOf<String, String>()
        preference.all.forEach {
            if (it.value is String)
            {
                result[it.key] = it.value as String
            }
        }
        return result
    }
    fun editString(filename: String, key: String, transform: (String) -> String)
    {
        val preference = Application.context.getSharedPreferences(filename, MODE_PRIVATE)
        val editor = preference.edit()
        val result = transform(preference.getString(key, ""))
        editor.putString(key, result)
        editor.apply()
    }
}
