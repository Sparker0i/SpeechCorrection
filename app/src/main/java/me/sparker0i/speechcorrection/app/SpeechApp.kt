package me.sparker0i.speechcorrection.app

import android.app.Application
import android.util.Log
import me.sparker0i.speechcorrection.ObservedObject
import java.io.BufferedReader
import java.io.InputStreamReader

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

    fun execute() {
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

    fun displaySize() {
        System.out.println(wordslist.size)
    }
}