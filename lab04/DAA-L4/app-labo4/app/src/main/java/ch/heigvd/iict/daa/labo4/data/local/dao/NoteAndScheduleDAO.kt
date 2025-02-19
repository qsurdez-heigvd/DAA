// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.heigvd.iict.daa.labo4.data.local.entities.Note
import ch.heigvd.iict.daa.labo4.data.local.entities.NoteAndSchedule
import ch.heigvd.iict.daa.labo4.data.local.entities.Schedule

/**
 * Data Access Object (DAO) for accessing Note and Schedule entities in the database.
 */
@Dao
interface NoteAndScheduleDAO {

    /**
     * Retrieves all notes along with their schedules.
     *
     * @return A LiveData list of NoteAndSchedule objects.
     */
    @Transaction
    @Query("SELECT * FROM Note")
    fun getAllNotes(): LiveData<List<NoteAndSchedule>>

    /**
     * Inserts a new note into the database.
     *
     * @param note The Note entity to be inserted.
     * @return The row ID of the newly inserted note.
     */
    @Insert
    fun insertNote(note: Note): Long

    /**
     * Deletes all notes from the database.
     */
    @Query("DELETE FROM Note")
    fun truncateNote()

    /**
     * Inserts a new schedule into the database.
     *
     * @param schedule The Schedule entity to be inserted.
     * @return The row ID of the newly inserted schedule.
     */
    @Insert
    fun insertSchedule(schedule: Schedule): Long

    /**
     * Deletes all schedules from the database.
     */
    @Query("DELETE FROM Schedule")
    fun truncateSchedule()

    /**
     * Retrieves all schedules from the database.
     *
     * @return A LiveData list of Schedule objects.
     */
    @Query("SELECT * FROM Schedule")
    fun getAllSchedules(): LiveData<List<Schedule>>

    /**
     * Retrieves the count of notes in the database.
     *
     * @return A LiveData object containing the count of notes.
     */
    @Query("SELECT COUNT(*) FROM Note")
    fun getCount(): LiveData<Long>

    /**
     * Retrieves the count of notes in the database directly.
     *
     * @return The count of notes as a Long.
     */
    @Query("SELECT COUNT(*) FROM Note")
    fun getDirectCount(): Long
}