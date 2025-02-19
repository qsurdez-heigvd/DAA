// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ch.heigvd.iict.daa.labo4.NotesApp
import ch.heigvd.iict.daa.labo4.databinding.FragmentControlsBinding
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModel
import ch.heigvd.iict.daa.labo4.ui.viewmodel.NoteViewModelFactory
import java.util.Locale

/**
 * A simple [Fragment] subclass that represents the controls screen.
 * Uses view binding to interact with the layout.
 */
class ControlsFragment : Fragment() {
    // Binding object instance corresponding to the fragment_controls.xml layout
    private var _binding: FragmentControlsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // ViewModel instance for this fragment
    private val noteViewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((requireActivity().application as NotesApp).repository)
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentControlsBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Called immediately after `onCreateView` has returned, but before any saved state has been restored in the view.
     * This is where you should set up any handlers or listeners for your views.
     *
     * @param view The View returned by `onCreateView`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.controlsActionGenerate.setOnClickListener {
            noteViewModel.generateANote()
        }

        binding.controlsActionDelete.setOnClickListener {
            noteViewModel.deleteAllNotes()
        }

        noteViewModel.countNotes.observe(viewLifecycleOwner, Observer { count ->
            binding.controlsCounter.text = String.format(Locale.getDefault(), "%d", count)
        })
    }

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     * This is where you should clean up resources related to the view.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
