// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.utils

import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule

/**
 * Interface definition for a callback to be invoked when a note is clicked.
 */
interface OnClickListener {
    /**
     * Called when a note has been clicked.
     *
     * @param noteAndSchedule The note and its schedule that was clicked.
     */
    fun onNoteClicked(noteAndSchedule: NoteAndSchedule)
}