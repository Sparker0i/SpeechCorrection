package me.sparker0i.speechcorrection.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.Snackbar
import android.text.Html
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
    private var output: TextView? = null
    private lateinit var app : SpeechApp
    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        output = findViewById(R.id.output)

        createDialog()

        app = application as SpeechApp
        app.isDictionaryRead.addObserver(this)
        app.asyncReadDictionary()
    }

    fun createDialog() {
        dialog = MaterialDialog.Builder(this)
                    .title("Please Wait")
                    .content("Loading from the Dictionary")
                    .progress(true , 0)
                    .build()
    }

    override fun update(o: Observable?, arg: Any?) {
        (o as ObservedObject).printVal()
        runOnUiThread(Runnable { dialog!!.hide() })
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
                    process(result[0])
                    for (i in 0 until result.size)
                        Log.i("Result", result[i])

                    if (!app.isDictionaryRead.value)
                        dialog!!.show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun process(result: String) {
        val array = result.split(" ")
        val builder = StringBuilder()
        for (i in 0 until array.size) {
            if (array[i].toLowerCase() in app.wordslist)
                builder.append(array[i] + " ")
            else
                builder.append("<font color='red'>" + array[i] + "</font> ")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            output!!.text = Html.fromHtml(builder.toString() , Html.FROM_HTML_MODE_LEGACY)
        else
            output!!.text = Html.fromHtml(builder.toString())
    }
}
