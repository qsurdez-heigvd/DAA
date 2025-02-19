package ch.heigvd.iict.daa.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import ch.heigvd.iict.daa.template.R

private const val ARG_COUNTER = "param_counter"

/**
 * A simple [Fragment] subclass that displays a counter.
 * Use the [FragmentStackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentStackFragment : Fragment() {

    private var counter: Int = 0

    /**
     * Called to do initial creation of a fragment.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            counter = it.getInt(ARG_COUNTER)
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself,
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     * @param counter Parameter 1.
     * @return A new instance of fragment FragmentStackFragment.
     */
    companion object {
        @JvmStatic
        fun newInstance(counter: Int = 0) =
            FragmentStackFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COUNTER, counter)
                }
            }
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link GUI
        val counterTv = view.findViewById<TextView>(R.id.f_counter)

        // Initialize views
        counterTv.text = "$counter"
    }

    /**
     * Called to ask the fragment to save its current dynamic state,
     * so it can later be reconstructed in a new instance of its process is restarted.
     * @param outState Bundle in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_COUNTER, counter)
    }

}