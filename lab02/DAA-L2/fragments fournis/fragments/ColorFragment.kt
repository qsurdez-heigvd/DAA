package ch.heigvd.iict.and.labo2.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import ch.heigvd.iict.and.labo2.R

private const val ARG_HEX_COLOR = "param_color"
private const val DEFAULT_COLOR = "#CDCDCD"

class ColorFragment : Fragment() {

    private var color: Int = Color.parseColor(DEFAULT_COLOR)

    private lateinit var rSeekBar : SeekBar
    private lateinit var gSeekBar : SeekBar
    private lateinit var bSeekBar : SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            color = Color.parseColor(it.getString(ARG_HEX_COLOR))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // création de la vue à partir du xml
        return inflater.inflate(R.layout.fragment_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            color = it.getInt(ARG_HEX_COLOR, Color.parseColor(DEFAULT_COLOR))
        }

        // linkage de la GUI
        rSeekBar = view.findViewById(R.id.color_r)
        gSeekBar = view.findViewById(R.id.color_g)
        bSeekBar = view.findViewById(R.id.color_b)

        // initialisation des vues
        // on colore la racine de la vue avec la couleur
        view.setBackgroundColor(color)
        // on initialise les 3 sliders avec les valeurs des 3 composantes (0..255)
        rSeekBar.progress = Color.red(color)
        gSeekBar.progress = Color.green(color)
        bSeekBar.progress = Color.blue(color)

        // on enregistre les évenements sur les seekBars
        rSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        gSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        bSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
            val rComponent = Color.red(color)
            val gComponent = Color.green(color)
            val bComponent = Color.blue(color)

            color = when(seekBar) {
                rSeekBar -> Color.rgb(value, gComponent, bComponent)
                gSeekBar -> Color.rgb(rComponent, value, bComponent)
                bSeekBar -> Color.rgb(rComponent, gComponent, value)
                else -> Color.rgb(rComponent, gComponent, bComponent) // pas de changement
            }

            // on met à jour la couleur du background
            // l'appel à view (qui correspond à getView()) permet de récupérer la référence
            // vers la vue racine de Fragment
            view?.setBackgroundColor(color)
        }

        // nous n'implémentons pas ces deux méthodes
        override fun onStartTrackingTouch(p0: SeekBar?) = Unit
        override fun onStopTrackingTouch(p0: SeekBar?) = Unit

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_HEX_COLOR, color)
    }

    companion object {
        @JvmStatic
        fun newInstance(color: String = DEFAULT_COLOR) =
            ColorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_HEX_COLOR, color)
                }
            }
    }
}
