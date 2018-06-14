package me.sparker0i.speechcorrection.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import java.util.*
import android.util.Log
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import me.sparker0i.speechcorrection.ObservedObject
import me.sparker0i.speechcorrection.R
import me.sparker0i.speechcorrection.app.SpeechApp


class MainActivity(private val REQ_CODE_SPEECH_INPUT: Int = 100) : AppCompatActivity() , Observer{
    override fun update(o: Observable?, arg: Any?) {
        (o as ObservedObject).printVal()
        dialog.hide()
    }

    private var output: TextView? = null
    private lateinit var app : SpeechApp
    private lateinit var dialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        output = findViewById(R.id.output)

        dialog = MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Loading from the Dictionary")
                .progress(true , 0)
                .build()

        app = application as SpeechApp
        app.isDictionaryRead.addObserver(this)
        app.asyncReadDictionary()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_speech, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id) {
            R.id.menu_option_speech -> {
                invokeSpeech()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun invokeSpeech() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE , Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT , getString(R.string.say_something))

        try {
            startActivityForResult(intent , REQ_CODE_SPEECH_INPUT)
        }
        catch (ex: ActivityNotFoundException) {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout , "Speech Recognition is not supported on your system" , Snackbar.LENGTH_SHORT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    output!!.text = result[0]
                    for (i in 0 until result.size)
                        Log.i("Result", result[i])

                    dialog.show()
                    var i = 0
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
