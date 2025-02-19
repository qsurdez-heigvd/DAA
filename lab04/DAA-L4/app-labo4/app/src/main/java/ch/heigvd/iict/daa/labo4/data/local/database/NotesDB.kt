// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.heigvd.iict.daa.labo4.data.local.dao.*
import ch.heigvd.iict.daa.labo4.data.local.entities.*
import kotlin.concurrent.thread

/**
 * The Room database for this app, containing the Note and Schedule entities.
 */
@Database(
    entities = [Note::class, Schedule::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(CalendarConverter::class)
abstract class NotesDB : RoomDatabase() {
    /**
     * Provides access to the NoteAndScheduleDAO.
     *
     * @return The NoteAndScheduleDAO instance.
     */
    abstract fun nasDAO(): NoteAndScheduleDAO

    companion object {
        private var INSTANCE: NotesDB? = null

        /**
         * Gets the singleton instance of the database.
         *
         * @param context The application context.
         * @return The NotesDB instance.
         */
        fun getDatabase(context: Context): NotesDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDB::class.java, "notes.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                populate() // does nothing if not empty
                INSTANCE!!
            }
        }

        /**
         * Populates the database with initial data if it is empty.
         */
        fun populate() {
            INSTANCE?.let { database ->
                thread {
                    // We suppose if the notes table is not empty, the database has already been populated
                    val isEmpty = database.nasDAO().getDirectCount() == 0L
                    if (isEmpty) {
                        // Generate 20 random notes and maybe associated schedules
                        for (i in 1..20) {
                            val noteId = database.nasDAO().insertNote(Note.generateRandomNote())
                            val schedule = Note.generateRandomSchedule()

                            if (schedule != null) {
                                schedule.ownerId = noteId
                                database.nasDAO().insertSchedule(schedule)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Callback class for database setup operations.
     */
    private class NotesDBSetup : RoomDatabase.Callback() {
        /**
         * Called when the database is created for the first time.
         *
         * @param db The database.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            populate()
        }

        /**
         * Called when the database has been opened.
         *
         * @param db The database.
         */
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }

        /**
         * Called when a destructive migration occurs.
         *
         * @param db The database.
         */
        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }
    }
}