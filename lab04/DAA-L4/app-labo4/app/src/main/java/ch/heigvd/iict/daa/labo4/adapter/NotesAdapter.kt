// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.daa.labo4.R
import ch.heigvd.iict.daa.labo4.data.local.database.CalendarConverter
import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule
import ch.heigvd.iict.daa.labo4.databinding.NoteItemViewBinding
import ch.heigvd.iict.daa.labo4.utils.NoteUtils
import ch.heigvd.iict.daa.labo4.utils.OnClickListener

/**
 * Custom RecyclerView adapter for displaying a list of NoteAndSchedule items.
 *
 * @param clickListener A click listener to be invoked when a note is clicked.
 * @param dataset       The initial list of NoteAndSchedule items to display.
 */
class NotesAdapter(
    private val clickListener: OnClickListener, private val dataset: List<NoteAndSchedule> = listOf()
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, NoteAndScheduleDiffCallback())

    /**
     * The list of NoteAndSchedule items to be displayed.
     *
     * @get The current list of NoteAndSchedule items.
     * @set value The new list of NoteAndSchedule items to set.
     */
    var items = listOf<NoteAndSchedule>()
        get() = differ.currentList
        set(value) {
            field = value
            differ.submitList(value)
        }

    init {
        items = dataset
    }

    /**
     * Creates a new ViewHolder instance based on the provided view type.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type, either NOTE or SCHEDULE.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteItemViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds the data at the given position to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the items list.
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * The ViewHolder class that holds the views for a single note or scheduled note.
     *
     * @param binding The binding object for the note or scheduled note view.
     */
    inner class ViewHolder(private val binding: NoteItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Define click listener for the ViewHolder's View
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onNoteClicked(items[position])
                }
            }
        }

        /**
         * Binds the data from the given NoteAndSchedule item to the views in the ViewHolder.
         *
         * @param noteAndSchedule The NoteAndSchedule item to bind the data from.
         */
        fun bind(noteAndSchedule: NoteAndSchedule) {
            with(binding) {
                with(noteAndSchedule) {
                    noteTextTitle.text = note.title
                    noteTextDescription.text = note.text
                    noteIcon.setImageResource(NoteUtils.getResource(noteAndSchedule))
                    noteIcon.imageTintList = NoteUtils.getColor(itemView.context, noteAndSchedule)
                    if (schedule != null) {
                        noteTextSchedule.visibility = View.VISIBLE
                        // Convert the note's schedule date to a friendly time string
                        val friendlyTime = schedule.date.let { date ->
                            CalendarConverter().convertDateToFriendlyTime(itemView.context, date)
                        }
                        noteTextSchedule.text = friendlyTime

                        // Check if the schedule is "Late" and set the icon color to red
                        if (friendlyTime == itemView.context.getString(R.string.late)) {
                            noteTextSchedule.compoundDrawablesRelative[1]?.setTint(
                                ContextCompat.getColor(itemView.context, R.color.red)
                            )
                        } else {
                            noteTextSchedule.compoundDrawablesRelative[1]?.setTint(
                                ContextCompat.getColor(itemView.context, R.color.grey)
                            )
                        }
                    } else {
                        noteTextSchedule.visibility = View.GONE
                    }
                }
            }
        }
    }
}

/**
 * A DiffUtil.ItemCallback implementation for efficiently updating the list of NoteAndSchedule items.
 */
class NoteAndScheduleDiffCallback : DiffUtil.ItemCallback<NoteAndSchedule>() {
    override fun areItemsTheSame(oldItem: NoteAndSchedule, newItem: NoteAndSchedule): Boolean {
        return oldItem.note.noteId == newItem.note.noteId
    }

    override fun areContentsTheSame(oldItem: NoteAndSchedule, newItem: NoteAndSchedule): Boolean {
        return oldItem == newItem
    }
}