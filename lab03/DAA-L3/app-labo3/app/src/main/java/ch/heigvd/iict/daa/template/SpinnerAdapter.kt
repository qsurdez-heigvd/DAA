// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.template

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Custom adapter for the spinner that displays a hint in the first position.
 */
class SpinnerAdapter(context: Context, items: List<String>) :
    ArrayAdapter<String>(
        context,
        android.R.layout.simple_spinner_item,
        listOf(context.getString(R.string.spinner_hint)) + items
    ) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    /**
     * Returns the view for the spinner item at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        if (position == 0) {
            view.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }
        return view
    }

    /**
     * Returns the view for the dropdown item at the specified position.
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (position == 0) {
            view.visibility = View.GONE
        } else {
            view.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
        return view
    }
}
