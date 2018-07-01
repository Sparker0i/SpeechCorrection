package me.sparker0i.speechcorrection.app

import android.app.Application
import android.util.Log
import me.sparker0i.speechcorrection.ObservedObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * SpeechApp is the entry point of the Application.
 * This class is invoked even before the start of any Activity.
 * All activities in the app will share the same child of this
 * Application class
 *
 * In this case SpeechApp extends android.app.Application class
 */
class SpeechApp : Application() {
    /**
     * isDictionaryRead is an object of ObservedObject class, which is a child of
     * the Observable Class.
     */
    var isDictionaryRead = ObservedObject()

    /**
     * An ArrayList to store the list of words inside the dictionary, for as long
     * as the app is in the memory.
     */
    lateinit var wordslist: ArrayList<String>

    /**
     * onCreate() comes into existence when the Application is instantiated
     * by the Android System
     */
    override fun onCreate() {
        super.onCreate()
        wordslist = ArrayList()
    }

    /**
     * asyncReadDictionary() is a helper function which helps us to call the
     * function which reads from the dictionary, inside a thread, and start the
     * thread
     */
    fun asyncReadDictionary() {
        if (!isDictionaryRead.value)
            Thread { execute() }.start()
    }

    /**
     * execute() is used to retrieve the words from the dictionary stored in
     * words.txt. There are around 370k words inside the dictionary which are read
     * line by line. The file is opened using getAssets().open() method
     *
     * After we have read each and every value of the dictionary, the value of
     * isDictionaryRead is set to true, and will thereby notify all the observers
     * due to implementation of set() for isDictionaryRead
     */
    private fun execute() {
        val inputStream = assets.open("words.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line = reader.readLine()
        while (line != null) {
            Log.i("Read", line)
            wordslist.add(line)
            line = reader.readLine()
        }
        isDictionaryRead.value = true
    }
}