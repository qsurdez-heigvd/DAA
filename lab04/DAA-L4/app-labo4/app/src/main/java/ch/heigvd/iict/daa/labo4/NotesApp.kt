// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4

import android.app.Application
import ch.heigvd.iict.daa.labo4.data.local.database.NotesDB
import ch.heigvd.iict.daa.labo4.data.repository.Repository

/**
 * The NotesApp class extends the Application class and serves as the entry point for the application.
 * It initializes the repository lazily when it is first accessed.
 */
class NotesApp : Application() {

    /**
     * Lazy initialization of the repository.
     * The repository is created using the NotesDB database instance and the DAO.
     */
    val repository by lazy {
        // Get the database instance
        val database = NotesDB.getDatabase(this)
        // Create the repository using the DAO from the database
        Repository(database.nasDAO())
    }
}