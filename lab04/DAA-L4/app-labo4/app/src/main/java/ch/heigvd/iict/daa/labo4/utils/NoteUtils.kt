// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.utils

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import ch.heigvd.iict.daa.labo4.R
import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule
import ch.heigvd.iict.daa.labo4.data.local.entities.State
import ch.heigvd.iict.daa.labo4.data.local.entities.Type

/**
 * Utility class for handling note-related operations.
 */
class NoteUtils {
    companion object {
        /**
         * Returns the color associated with the state of the given note.
         *
         * @param context The context to use for accessing resources.
         * @param noteAndSchedule The note and its schedule.
         * @return The ColorStateList associated with the note's state.
         */
        fun getColor(context: Context, noteAndSchedule: NoteAndSchedule) : ColorStateList? {
            return when (noteAndSchedule.note.state) {
                State.IN_PROGRESS -> ContextCompat.getColorStateList(context, R.color.green)
                State.DONE -> ContextCompat.getColorStateList(context, R.color.grey)
            }
        }

        /**
         * Returns the drawable resource ID associated with the type of the given note.
         *
         * @param noteAndSchedule The note and its schedule.
         * @return The drawable resource ID associated with the note's type.
         */
        fun getResource(noteAndSchedule: NoteAndSchedule) : Int {
            return when (noteAndSchedule.note.type) {
                Type.NONE -> R.drawable.note
                Type.WORK -> R.drawable.work
                Type.TODO -> R.drawable.todo
                Type.FAMILY -> R.drawable.family
                Type.SHOPPING -> R.drawable.shopping
            }
        }

        /**
         * Enum class representing the different types of sorting for notes.
         */
        enum class SortType {
            CREATION_DATE,
            ETA,
            None
        }
    }
}