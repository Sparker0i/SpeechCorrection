package me.sparker0i.speechcorrection

import android.util.Log
import java.util.*

/**
 * ObservedObject is a custom class which is a child of the Observable
 * class. This class has been mainly created to facilitate the status
 * of isDictionaryRead, and making it known to all observers registered
 * for the app in the memory
 */
class ObservedObject : Observable() {
    /**
     * value is the field which we want to manipulate for isDictionaryRead.
     * By default it will be false
     */
    var value: Boolean = false
        /**
         * set() is invoked whenever the value of isDictionaryRead inside
         * SpeechApp is changed (app.isDictionaryRead.value = true)
         * Once this is invoked, all the observers will be notified of this
         * change, and all the observers will invoke their update()
         */
        set(newValue) {
            field = newValue
            setChanged()
            notifyObservers()
        }

    /**
     * Stub function used to print the value of the variable value
     */
    fun printVal() {
        Log.i("Value" , "" + value)
    }
}