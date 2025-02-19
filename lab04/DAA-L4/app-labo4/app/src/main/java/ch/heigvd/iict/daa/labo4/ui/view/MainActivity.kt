// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.ui.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.iict.daa.labo4.NotesApp
import ch.heigvd.iict.daa.labo4.R
import ch.heigvd.iict.daa.labo4.databinding.ActivityMainBinding
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModel
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModelFactory
import ch.heigvd.iict.daa.labo4.utils.NoteUtils

/**
 * Main activity of the application. It is responsible for setting up the UI and handling menu actions.
 */
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding

    // ViewModel instance for managing UI-related data
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApp).repository)
    }

    /**
     * Called when the activity is starting. This is where most initialization should go.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return True for the menu to be displayed; false to hide it.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600

        inflater.inflate(R.menu.menu_sort, menu)
        if (!isTablet) {
            Log.d(TAG, String.format("Tablet: %b", isTablet))
            inflater.inflate(R.menu.menu_notes, menu)
        }

        return true
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return False to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_generate -> {
                noteViewModel.generateANote()
                true
            }

            R.id.action_delete -> {
                noteViewModel.deleteAllNotes()
                true
            }

            R.id.sort_creation -> {
                noteViewModel.setSortType(NoteUtils.Companion.SortType.CREATION_DATE)
                true
            }

            R.id.sort_schedule -> {
                noteViewModel.setSortType(NoteUtils.Companion.SortType.ETA)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}