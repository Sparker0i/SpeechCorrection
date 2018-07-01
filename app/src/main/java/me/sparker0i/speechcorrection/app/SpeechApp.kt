package me.sparker0i.speechcorrection.app

import android.app.Application
import android.util.Log
import me.sparker0i.speechcorrection.ObservedObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * SpeechApp is the entry point of the Application.
 * This class is invoked even before the start of any Activity.
 * All activities in the app will share the same child of this Application class
 *
 * In this case SpeechApp extends android.app.Application class
 */
class SpeechApp: Application() {
    var isDictionaryRead = ObservedObject()
    lateinit var wordslist : ArrayList<String>

    override fun onCreate() {
        super.onCreate()
        wordslist = ArrayList()
    }

    fun asyncReadDictionary() {
        if (!isDictionaryRead.value)
            Thread { execute() }.start()
    }

    private fun execute() {
        val inputStream = assets.open("words.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line = reader.readLine()
        while (line != null) {
            Log.i("Read" , line)
            wordslist.add(line)
            line = reader.readLine()
        }
        isDictionaryRead.value = (true)
    }
}