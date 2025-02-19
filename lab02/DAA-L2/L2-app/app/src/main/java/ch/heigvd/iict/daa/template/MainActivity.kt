package ch.heigvd.iict.daa.template

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.iict.daa.contracts.PickNameContract

class MainActivity : AppCompatActivity() {
    private val tag: String = javaClass.simpleName
    private var name: String? = null

    private val getName = registerForActivityResult(PickNameContract()) { returnedName ->
        Log.d(tag, "Received name: $returnedName")
        name = returnedName
        setWelcomeText(name ?: getString(R.string.welcome_default_text))
    }

    @SuppressLint("SetTextI18n")
    private fun setWelcomeText(name: String) {
        val textView = findViewById<android.widget.TextView>(R.id.welcome_text)
        textView.text = getString(R.string.welcome_prefix) + " " + name
        Log.d(tag, "Setting name: $name")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
        setContentView(R.layout.activity_main)

        setWelcomeText(getString(R.string.welcome_default_text))


        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            getName.launch(name)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(tag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(tag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "onDestroy")
    }
}
