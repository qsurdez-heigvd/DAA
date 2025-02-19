// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest

import android.app.Application
import ch.heigvd.iict.and.rest.api.ContactApiServiceImpl
import ch.heigvd.iict.and.rest.database.ContactsDatabase

/**
 * Application class that provides a repository instance to the rest of the application.
 */
class ContactsApplication : Application() {
    private val database by lazy { ContactsDatabase.getDatabase(this) }
    val repository by lazy { ContactsRepository(database.contactsDao(), ContactApiServiceImpl(), this) }
}