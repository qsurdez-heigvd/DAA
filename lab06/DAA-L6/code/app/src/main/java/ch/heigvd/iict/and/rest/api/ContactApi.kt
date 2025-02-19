// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.api

import ch.heigvd.iict.and.rest.api.dto.ContactDTO
import ch.heigvd.iict.and.rest.models.Contact
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface defining the REST API endpoints for contact management using Retrofit.
 *
 * This interface maps HTTP requests to our server endpoints at "https://daa.iict.ch/".
 * It handles all contact-related operations including enrollment, retrieval, creation,
 * modification, and deletion of contacts. The interface uses coroutines for asynchronous
 * operations and DTOs for clean data transfer.
 *
 * All endpoints except enrollment require a UUID header for authentication. This UUID
 * is obtained through the enrollment process and must be included in subsequent requests
 * to identify the user's contact collection.
 *
 * Example UUID format: "420e384b-7082-464e-9afe-25f6e6319301"
 *
 * Each method corresponds to a specific API endpoint:
 * - Enrollment: GET /enroll
 * - List Contacts: GET /contacts
 * - Single Contact: GET /contacts/{id}
 * - Create Contact: POST /contacts
 * - Update Contact: PUT /contacts/{id}
 * - Delete Contact: DELETE /contacts/{id}
 *
 * @see ContactDTO For the data transfer structure
 * @see Contact For the local entity structure
 */
interface ContactApi {
    /**
     * Enrolls a new user and creates their initial contact collection.
     *
     * @return A UUID string that will be used for subsequent API calls
     */
    @GET("enroll")
    suspend fun enroll(): String

    /**
     * Retrieves all contacts associated with the given UUID.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @return A list of contacts in DTO format
     */
    @GET("contacts")
    suspend fun getAllContacts(@Header("X-UUID") uuid: String): List<ContactDTO>

    /**
     * Retrieves a specific contact by its ID.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contactId The server ID of the contact to retrieve
     * @return A Response containing the contact in DTO format if found
     */
    @GET("contacts/{id}")
    suspend fun getContact(
        @Header("X-UUID") uuid: String, @Path("id") contactId: Long
    ): Response<ContactDTO>

    /**
     * Creates a new contact on the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contact The contact information to create
     * @return A Response containing the created contact in DTO format
     */
    @POST("contacts")
    suspend fun createContact(
        @Header("X-UUID") uuid: String, @Body contact: ContactDTO
    ): ContactDTO

    /**
     * Updates an existing contact on the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contactId The server ID of the contact to update
     * @param contact The updated contact information
     * @return A Response containing the updated contact in DTO format
     */
    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Header("X-UUID") uuid: String, @Path("id") contactId: Long, @Body contact: ContactDTO
    ): ContactDTO

    /**
     * Deletes a contact from the server.
     *
     * @param uuid The user's UUID obtained from enrollment
     * @param contactId The server ID of the contact to delete
     */
    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Header("X-UUID") uuid: String, @Path("id") contactId: Long
    ): Response<Unit> // returns 204 - NO_CONTENT so we have an empty body
    // Help found on https://stackoverflow.com/questions/59636219/how-to-handle-204-response-in-retrofit-using-kotlin-coroutines
}