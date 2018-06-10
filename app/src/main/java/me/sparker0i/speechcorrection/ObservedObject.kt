package me.sparker0i.speechcorrection

import android.util.Log
import java.util.*

class ObservedObject(var value: Boolean) : Observable() {
    init {
        value = false
    }

    fun setVal(vals: Boolean) {
        value = vals
        setChanged()
        notifyObservers()
    }

    fun printVal() {
        Log.i("Value" , "" + value)
    }
}