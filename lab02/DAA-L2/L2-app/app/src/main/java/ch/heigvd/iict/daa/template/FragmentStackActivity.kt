package ch.heigvd.iict.daa.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import ch.heigvd.iict.daa.fragments.FragmentStackFragment

/**
 * Activity that manages a stack of fragments.
 */
class FragmentStackActivity : AppCompatActivity() {

    private val tag: String = javaClass.simpleName

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_stack)
        Log.i(tag,"onCreate")

        // If this is the first creation, add the initial fragment to the container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentStackFragment.newInstance())
                .commitNow()
        }

        // Set up the "Next" button to replace the current fragment with a new one
        findViewById<Button>(R.id.next_button).setOnClickListener {
            next()
            Log.i(tag, "next " + supportFragmentManager.backStackEntryCount)
        }

        // Set up the "Back" button to pop the back stack or finish the activity
        findViewById<Button>(R.id.back_button).setOnClickListener {
            back()
            Log.i(tag, "back " + supportFragmentManager.backStackEntryCount)
        }

        // Set up the "Close" button to finish the activity
        findViewById<Button>(R.id.close_button).setOnClickListener {
            finish()
        }
    }

    /**
     * Pops the back stack if there are more than one fragments, otherwise finishes the activity.
     */
    private fun back() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            finish()
        }
    }

    /**
     * Replaces the current fragment with a new one and adds the transaction to the back stack.
     */
    private fun next() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FragmentStackFragment.newInstance(supportFragmentManager.backStackEntryCount + 1))
            .addToBackStack(null)
            .commit()
    }
}