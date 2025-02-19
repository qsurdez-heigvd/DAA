package ch.heigvd.iict.daa.template

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditNameActivity : AppCompatActivity() {
    private val tag: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
        setContentView(R.layout.activity_edit_name)

        val textField = findViewById<EditText>(R.id.edit_text)
        val button = findViewById<android.widget.Button>(R.id.save_button)
        intent.getStringExtra(NAME_PARAMETER_KEY)?.let {
            Log.d(tag, it)
            textField.setText(it)
        }

        button.setOnClickListener {
            val fieldValue = textField.text.toString()
            val data = Intent()
            data.putExtra(NAME_PARAMETER_KEY, fieldValue)
            setResult(RESULT_OK, data)
            finish()
        }


        val name = intent.getStringExtra(NAME_PARAMETER_KEY)

        Log.d(tag, "Name: $name")
    }

    companion object {
        const val NAME_PARAMETER_KEY = "NAME_PARAMETER_KEY"
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
