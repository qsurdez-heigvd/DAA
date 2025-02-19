// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import ch.heigvd.iict.daa.labo4.data.local.entities.Note
import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule
import ch.heigvd.iict.daa.labo4.data.repository.Repository
import ch.heigvd.iict.daa.labo4.utils.NoteUtils

/**
 * ViewModel for the NotesFragment. It is responsible for providing the data to the UI and handling the
 * communication with the model.
 *
 * @property repository The repository to interact with the data layer.
 */
class NoteViewModel(private val repository: Repository) : ViewModel() {
    // LiveData containing all notes and their schedules
    private val allNotes: LiveData<List<NoteAndSchedule>> = repository.allNotes

    // LiveData containing the count of notes
    val countNotes: LiveData<Long> = repository.countNotes

    // LiveData containing notes sorted by creation date
    private val sortedNotesByDate = this.allNotes.map { notes ->
        notes.sortedByDescending { it.note.creationDate }
    }

    // LiveData containing notes sorted by ETA
    private val sortedNotesByETA = this.allNotes.map { notes ->
        notes.sortedByDescending { it.schedule?.date }
    }

    // MutableLiveData to hold the current sort type
    private var sortType: MutableLiveData<NoteUtils.Companion.SortType> = MutableLiveData<NoteUtils.Companion.SortType>().apply {
        value = NoteUtils.Companion.SortType.None
    }

    // LiveData containing notes sorted based on the current sort type
    val sortedNotes: LiveData<List<NoteAndSchedule>> = sortType.switchMap { sortType ->
        when (sortType) {
            NoteUtils.Companion.SortType.CREATION_DATE -> sortedNotesByDate
            NoteUtils.Companion.SortType.ETA -> sortedNotesByETA
            NoteUtils.Companion.SortType.None -> allNotes
            else -> allNotes
        }
    }

    /**
     * Sets the sort type for the notes.
     *
     * @param updatedSortType The new sort type to be applied.
     */
    fun setSortType(updatedSortType: NoteUtils.Companion.SortType) {
        sortType.value = updatedSortType
    }

    /**
     * Generates a random note and inserts it into the repository.
     */
    fun generateANote() {
        repository.insertNAS(Note.generateRandomNote(), Note.generateRandomSchedule())
    }

    /**
     * Deletes all notes from the repository.
     */
    fun deleteAllNotes() {
        repository.deleteAll()
    }
}