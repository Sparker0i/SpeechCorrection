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

/**
 * MainActivity is the screen that will be visible to the end user.
 * This class extends the AppCompatActivity class to denote that this
 * class is an Activity. It also implements the Observer pattern, to
 * listen to the changes made in isDictionaryRead inside SpeechApp
 */
class MainActivity(private val REQ_CODE_SPEECH_INPUT: Int = 100) :
        AppCompatActivity() , Observer{

    /**
     * output refers to the TextView where our speech output with the
     * underline will be displayed
     */
    private var output: TextView? = null

    /**
     * app is an object of SpeechApp, currently assigned to null
     */
    private lateinit var app : SpeechApp

    /**
     * dialog is a dialog box, that denotes that the dictionary
     * is currently being read
     */
    private var dialog: MaterialDialog? = null

    /**
     * onCreate() is called when an Activity is instantiated.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
            setContentView() will set the layout of our app to the one
            stored in layout/activity_main.
         */
        setContentView(R.layout.activity_main)
        output = findViewById(R.id.output) //findViewById() binds the TextView in the layout to the code in the activity

        createDialog() //Used to initialize the dialog box

        /*
            getApplication() (application in Kotlin) will give me the
            current app's Application object and then typecast it to SpeechApp
         */
        app = application as SpeechApp
        app.isDictionaryRead.addObserver(this)  //Adding an observer to isDictionaryRead of SpeechApp
        app.asyncReadDictionary()   //Calls a function which Reads from the words.txt inside a thread
        if (!app.isDictionaryRead.value)
            dialog!!.show()
    }

    /**
     * Used to instantiate the dialog object declared before
     */
    fun createDialog() {
        dialog = MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Loading from the Dictionary")
                .progress(true , 0)
                .cancelable(false)
                .build()
    }

    /**
     * Called when there is a change to an Observable.
     * In our case, this is called when there is a change in the value of isDictionaryRead
     */
    override fun update(o: Observable?, arg: Any?) {
        (o as ObservedObject).printVal()    //To test whether the value of isDictionaryRead has changed or not
        /*
            runOnUiThread() is being used here because update() runs
            on its own thread, and to make changes to the UI thread (app screen)
            from a thread, we have to do it using runOnUiThread()
         */
        runOnUiThread(Runnable { dialog!!.dismiss() })
    }

    /**
     * Creates a 3-Dot menu at the top-right corner of the screen, inside the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_speech, menu)
        return true
    }

    /**
     * This is invoked when an option from a menu is selected. Here the ID of the
     * selected item is passed as an argument
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when(id) {
            R.id.menu_option_speech -> {
                invokeSpeech()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Used to invoke the speech recording capabilities of Android.
     * The system calls the speech screen (via startActivityForResult() using an Intent),
     * records the input and gives us a list of possible outputs. The first output of them
     * is the most probable answer.
     *
     * We have to call startActivityForResult() in a try-catch block, as some Android phones (~0.1%)
     * may not have such capabilities (phones without a Play Store, or running on a
     * version of Android that is not supported by the Google app)
     */
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

    /**
     * Invoked after startActivityForResult() finishes executing
     * In our case, if the user recorded a speech, the list of possible sentences will
     * be available in the data argument.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //when is nothing but a switch case equivalent for Kotlin
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    process(result[0])
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Used to process our speech output, and checks whether the word is in the dictionary
     * or not. If not, then it shows that word in Red color, else in normal black color
     * Red underline is not working ATM, so we had to adjust with the red font color.
     */
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
