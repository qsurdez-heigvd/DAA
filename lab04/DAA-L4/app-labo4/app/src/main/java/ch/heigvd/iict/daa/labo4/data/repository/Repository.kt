// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.repository

import ch.heigvd.iict.daa.labo4.data.local.dao.NoteAndScheduleDAO
import ch.heigvd.iict.daa.labo4.data.local.entities.Note
import ch.heigvd.iict.daa.labo4.data.local.entities.Schedule
import kotlin.concurrent.thread

/**
 * This repository is responsible to run actions on given DAOs and hand useful live data
 */
class Repository(
    private val nasDao: NoteAndScheduleDAO
) {
    val allNotes = nasDao.getAllNotes()
    val countNotes = nasDao.getCount()

    /**
     * Insert a note and an optional associated schedule
     */
    fun insertNAS(note: Note, schedule: Schedule?) {
        thread {
            val id =  nasDao.insertNote(note)

            if (schedule != null) {
                schedule.ownerId = id
                nasDao.insertSchedule(schedule)
            }
        }
    }

    fun deleteAll() {
        thread {
            // Empty all tables
            nasDao.truncateSchedule()
            nasDao.truncateNote()
        }
    }
}
