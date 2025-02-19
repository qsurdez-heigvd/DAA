// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.heigvd.iict.and.rest.models.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = true)
abstract class ContactsDatabase : RoomDatabase() {
    /**
     * Get the DAO for contacts.
     */
    abstract fun contactsDao(): ContactsDao

    companion object {
        /**
         * Database instance.
         */
        @Volatile
        private var INSTANCE: ContactsDatabase? = null

        /**
         * Get the database instance.
         *
         * @param context the context
         */
        fun getDatabase(context: Context): ContactsDatabase {
            return INSTANCE ?: synchronized(this) {
                val _instance = Room.databaseBuilder(
                    context.applicationContext, ContactsDatabase::class.java, "contacts.db"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = _instance
                _instance
            }
        }
    }
}