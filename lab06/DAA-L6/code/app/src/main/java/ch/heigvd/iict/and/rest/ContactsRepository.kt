// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import ch.heigvd.iict.and.rest.api.ContactApiService
import ch.heigvd.iict.and.rest.api.dto.ContactDTO
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.Status
import ch.heigvd.iict.and.rest.utils.withNetworkCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class that coordinates between local database storage and remote API operations.
 *
 * This repository follows the single source of truth principle, where the local database acts
 * as the primary source of data for the application. The repository handles the complexity
 * of synchronizing data between the local database and the remote server, ensuring data
 * consistency and proper error handling.
 *
 * Think of this repository as a librarian who manages both physical books (local database)
 * and an online catalog (remote API). The librarian needs to keep both systems in sync
 * while handling situations like network failures or update conflicts.
 *
 * Key responsibilities:
 * - Managing user enrollment and UUID storage
 * - Coordinating data synchronization
 * - Converting between DTOs and local entities
 * - Providing a LiveData stream of contacts
 * - Handling errors and logging
 *
 * @property contactsDao Data Access Object for local database operations
 * @property apiService Service interface for remote API operations
 * @property uuid The user's UUID for API authentication, stored in memory during session
 */
class ContactsRepository(
    private val contactsDao: ContactsDao, private val apiService: ContactApiService, private val context: Context
) {
    private val TAG = this.javaClass.simpleName.toString()

    /**
     * Shared preferences for storing the UUID.
     */
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("contacts_prefs", Context.MODE_PRIVATE)

    /**
     * Stores the UUID obtained during enrollment.
     * This is kept in memory and should be persisted for longer term storage.
     */
    private var uuid: String?
        get() = sharedPreferences.getString("UUID", null)
        set(value) {
            sharedPreferences.edit().putString("UUID", value).apply()
        }

    /**
     * LiveData stream of all contacts from the local database.
     * This automatically updates the UI whenever the database changes.
     */
    val allContacts = contactsDao.getAllContactsLiveData()

    /**
     * Trash the database entirely, useful during initial setup
     */
    suspend fun trashDb() = withContext(Dispatchers.IO) {
        contactsDao.clearAllContacts()
    }

    /**
     * Performs user enrollment with the remote server.
     *
     * This operation:
     * 1. Requests a new UUID from the server
     * 2. Stores the UUID for future API calls
     * 3. Logs the process for debugging purposes
     *
     * @return The newly obtained UUID
     * @throws Exception if enrollment fails
     */
    suspend fun enroll() {
        withNetworkCheck(context) {
            try {
                Log.d(TAG, "Enrolling...")
                uuid = apiService.enroll()
                Log.d(TAG, "Enrolled with UUID: $uuid")
                Toast.makeText(context,
                    context.getString(R.string.enroll_success), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                Toast.makeText(context, context.getString(R.string.enroll_error), Toast.LENGTH_SHORT).show()
                throw Exception("Failed to enroll")
            }
        }
    }

    /**
     * Retrieves all contacts from the remote server and stores them in the local database.
     *
     * This method performs several steps:
     * 1. Fetches contact DTOs from the server
     * 2. Converts DTOs to local Contact entities
     * 3. Stores the contacts in the local database
     *
     * The operation runs on the IO dispatcher because it involves both network
     * and database operations, which should not be performed on the main thread.
     * Without this dispatcher, the app would crash due to performing disk/network
     * operations on the main thread.
     *
     * @return List of Contact entities
     * @throws Exception if the operation fails (e.g., network error, missing UUID)
     */
    suspend fun loadAllContacts(): List<Contact> {
        return withNetworkCheck(context) {
            uuid?.let { userUuid ->
                withContext(Dispatchers.IO) {
                    try {
                        val contactDTOs = apiService.getAllContacts(userUuid)
                        Log.d(TAG, "Fetched ${contactDTOs.size} contacts")
                        val contacts = contactDTOs.map { dto -> Contact.fromDTO(dto) }
                        contactsDao.insertAll(contacts)
                        contacts
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                        throw Exception("Failed to get contacts")
                    }
                }
            } ?: run {
                Log.d(TAG, "UUID is null, cannot fetch contacts")
                Toast.makeText(
                    context,
                    context.getString(R.string.missing_uuid),
                    Toast.LENGTH_SHORT
                ).show()
                emptyList()
            }
        } ?: emptyList()
    }

    /*
    * Syncing entrypoint to try to sync dirty contacts when there is internet connectivity
    * */
    suspend fun sync() {
        withNetworkCheck(context) {
            var dirtyContactsCounter = 0
            withContext(Dispatchers.IO) {
                contactsDao.getDirtyContacts().let { contacts ->
                    dirtyContactsCounter = contacts.size
                    Log.d(TAG, "Starting synchronization for ${contacts.count()} contacts")
                    contacts.forEach { contact ->
                        try {
                            save(contact)
                        } catch (e: Exception) {
                            Log.d(TAG, e.toString())
                            Log.d(TAG, "Failed to sync contact $contact")
                        }
                    }
                }
            }
            if (dirtyContactsCounter == 0) {
                Toast.makeText(context,
                    context.getString(R.string.nothing_to_sync), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.message_sync_success, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Try to delete a contact remotely or just save changes locally
     */
    suspend fun tryDeleteContact(id: Long?) = withContext(Dispatchers.IO) {
        id?.let { contactsDao.getContactById(it) }?.apply {
            if (status != Status.DELETED) {
                status = Status.DELETED
                contactsDao.update(this)
            }

            remote_id?.let { remoteId ->
                withNetworkCheck(context) {
                    try {
                        apiService.deleteContact(uuid.toString(), remoteId)
                        contactsDao.delete(this) // real deletion as the API worked
                        Log.d(TAG, "Deletion successful for contact $this")
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                        Log.d(TAG, "Deletion failed for contact $this")
                    }
                }
            } ?: run {
                contactsDao.delete(this) // Local deletion only
                Log.d(TAG, "Contact $this deleted locally")
            }
        } ?: Log.d(
            TAG, "Contact ID is null or contact not found in local database, cannot delete contact"
        )
    }

    /**
     * Try to update a contact remotely or just save changes locally
     */
    private suspend fun tryUpdateContact(contact: Contact) = withContext(Dispatchers.IO) {
        contactsDao.update(contact) // apply local modification in all cases

        withNetworkCheck(context) {
            apiService.updateContact(uuid.toString(), ContactDTO.toDTO(contact))
            contact.status = Status.OK
            contactsDao.update(contact)
            Log.d(TAG, "Update has worked for contact $contact")
        }
    }

    /**
     * Try to create a contact remotely or just save changes locally
     */
    private suspend fun tryCreateContact(contact: Contact) = withContext(Dispatchers.IO) {
        // If the contact already has an ID, it's only dirty and we just need to save and sync it
        contact.id?.let {
            try {
                contactsDao.update(contact)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                Log.d(TAG, "Failed to update contact $contact")
            }
        } ?: run {
            val id = contactsDao.insert(contact)
            contact.id = id
        }

        withNetworkCheck(context) {
            val createdContactDTO = apiService.createContact(uuid.toString(), ContactDTO.toDTO(contact))
            contact.remote_id = createdContactDTO.id
            contact.status = Status.OK
            contactsDao.update(contact)
            Log.d(TAG, "Creation successful for contact $contact")
        }
    }

    /**
     * Save given contact, either create, update or delete it. Do it locally and then try to sync
     * the change to the API.
     */
    suspend fun save(contact: Contact) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Saving contact $contact")

        when (contact.status) {
            Status.NEW -> tryCreateContact(contact)
            Status.MODIFIED -> tryUpdateContact(contact)
            Status.DELETED -> tryDeleteContact(contact.id)
            Status.OK -> {
                // Do nothing, the contact is already synchronized
                Log.d(TAG, "Contact is already synchronized: $contact")
            }
        }
    }
}