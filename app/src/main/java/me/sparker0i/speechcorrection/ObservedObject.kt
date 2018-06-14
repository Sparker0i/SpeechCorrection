package me.sparker0i.speechcorrection

import android.util.Log
import java.util.*

class ObservedObject : Observable() {
    var value: Boolean = false
        set(newValue) {
            field = newValue
            setChanged()
            notifyObservers()
        }

    fun printVal() {
        Log.i("Value" , "" + value)
    }
}