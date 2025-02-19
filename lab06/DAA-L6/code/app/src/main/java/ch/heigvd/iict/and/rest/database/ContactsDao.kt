// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact

/**
 * Data Access Object (DAO) for the Contact entity.
 * Provides methods for interacting with the Contact table in the database.
 */
@Dao
interface ContactsDao {

    /**
     * Inserts a single contact into the database.
     * @param contact The contact to be inserted.
     *
     * @return The row ID of the newly inserted contact.
     */
    @Insert
    fun insert(contact: Contact) : Long

    /**
     * Inserts multiple contacts into the database.
     *
     * @param contacts The list of contacts to be inserted.
     */
    @Insert
    fun insertAll(contacts: List<Contact>)

    /**
     * Updates an existing contact in the database.
     *
     * @param contact The contact to be updated.
     */
    @Update
    fun update(contact: Contact)

    /**
     * Deletes a contact from the database.
     *
     * @param contact The contact to be deleted.
     */
    @Delete
    fun delete(contact: Contact)

    /**
     * Retrieves all contacts that are not marked as deleted.
     *
     * @return A LiveData list of all non-deleted contacts.
     */
    @Query("SELECT * FROM Contact WHERE status != 'DELETED'")
    fun getAllContactsLiveData() : LiveData<List<Contact>>

    /**
     * Retrieves a contact by its ID.
     *
     * @param id The ID of the contact to be retrieved.
     * @return The contact with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id : Long) : Contact?

    /**
     * Deletes all contacts from the database.
     */
    @Query("DELETE FROM Contact")
    fun clearAllContacts()

    /**
     * Retrieves all contacts that have a status other than 'OK'.
     * These are considered dirty contacts that need actions towards the API.
     *
     * @return A list of dirty contacts.
     */
    @Query("SELECT * FROM Contact WHERE status != 'OK'")
    fun getDirtyContacts() : List<Contact>
}