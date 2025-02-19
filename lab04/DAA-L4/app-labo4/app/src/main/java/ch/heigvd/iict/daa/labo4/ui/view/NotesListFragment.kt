// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ch.heigvd.iict.daa.labo4.NotesApp
import ch.heigvd.iict.daa.labo4.adapter.NotesAdapter
import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule
import ch.heigvd.iict.daa.labo4.databinding.FragmentNotesListBinding
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModel
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModelFactory
import ch.heigvd.iict.daa.labo4.utils.OnClickListener

/**
 * Fragment to display a list of notes.
 */
class NotesListFragment : Fragment() {
    // Binding object instance corresponding to the fragment_notes_list.xml layout
    private var _binding: FragmentNotesListBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    // ViewModel instance shared between fragments
    private val noteViewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((requireActivity().application as NotesApp).repository)
    }

    // Adapter for the RecyclerView
    private lateinit var notesAdapter: NotesAdapter

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter with a click listener
        notesAdapter = NotesAdapter(object : OnClickListener {
            override fun onNoteClicked(noteAndSchedule: NoteAndSchedule) {
                Toast.makeText(context, "Note clicked", Toast.LENGTH_SHORT).show()
            }
        })

        // Set up the RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }

        // Observe the sorted notes and update the adapter's items
        noteViewModel.sortedNotes.observe(viewLifecycleOwner) { notes ->
            notes?.let {
                notesAdapter.items = it
                binding.recyclerView.post {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}