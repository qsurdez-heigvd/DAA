// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.api

import ch.heigvd.iict.and.rest.api.dto.ContactDTO

/**
 * Service interface defining high-level contact management operations for the application.
 *
 * This interface acts as an abstraction layer between the repository and the raw API calls,
 * providing a clean and type-safe way to interact with the contact server. It handles both
 * enrollment operations and CRUD (Create, Read, Update, Delete) operations for contacts.
 *
 * The service layer adds several important benefits:
 * - Separates business logic from raw API calls
 * - Provides a consistent interface regardless of the underlying implementation
 * - Makes testing easier through interface mocking
 * - Ensures proper error handling and data transformation
 *
 * All operations are implemented as suspend functions to support coroutines, making them
 * safe for background execution. The UUID obtained from enrollment must be preserved and
 * used for all subsequent contact operations.
 *
 * Example usage in a repository:
 * ```
 * class ContactRepository(private val apiService: ContactApiService) {
 *     private var uuid: String? = null
 *
 *     suspend fun initialize() {
 *         uuid = apiService.enroll()
 *     }
 *
 *     suspend fun getContacts(): List<Contact> {
 *         return apiService.getAllContacts(uuid!!)
 *     }
 * }
 * ```
 *
 * @see ContactApi For the underlying API implementation
 * @see ContactDTO For the data transfer structure
 */
interface ContactApiService {
    /**
     * Enrolls a new user in the contact management system.
     *
     * This operation should be called once when initializing the application or when
     * re-enrolling a user. The returned UUID must be stored and used for all subsequent
     * operations.
     *
     * @return A UUID string for identifying the user's contact collection
     * @throws Exception if enrollment fails
     */
    suspend fun enroll(): String

    /**
     * Retrieves all contacts associated with the given UUID.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @return A list of contacts in DTO format
     * @throws Exception if the retrieval fails or if the UUID is invalid
     */
    suspend fun getAllContacts(uuid: String): List<ContactDTO>

    /**
     * Retrieves a specific contact by its ID.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contactId The server ID of the contact to retrieve
     * @return The requested contact in DTO format
     * @throws Exception if the contact is not found or if the UUID is invalid
     */
    suspend fun getContact(uuid: String, contactId: Long): ContactDTO

    /**
     * Creates a new contact on the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contact The contact information to create
     * @return The created contact in DTO format, including its server-assigned ID
     * @throws Exception if creation fails or if the UUID is invalid
     */
    suspend fun createContact(uuid: String, contact: ContactDTO): ContactDTO

    /**
     * Updates an existing contact on the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contact The updated contact information
     * @return The updated contact in DTO format
     * @throws Exception if the update fails or if the UUID is invalid
     */
    suspend fun updateContact(uuid: String, contact: ContactDTO): ContactDTO

    /**
     * Deletes a contact from the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contactId The server ID of the contact to delete
     * @throws Exception if deletion fails or if the UUID is invalid
     */
    suspend fun deleteContact(uuid: String, contactId: Long)
}