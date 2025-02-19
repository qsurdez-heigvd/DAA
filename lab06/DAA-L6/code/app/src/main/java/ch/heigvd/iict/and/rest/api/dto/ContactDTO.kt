package ch.heigvd.iict.and.rest.api.dto

import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType

/**
 * Data Transfer Object (DTO) representing a contact as received from or sent to the REST API.
 *
 * This class acts as an intermediary between the server's JSON representation of a contact and
 * our local Contact entity. It matches the exact structure of the server's JSON response,
 * making it ideal for serialization/deserialization with Gson.
 *
 * Example JSON from server:
 * ```json
 * {
 *   "id": 983,
 *   "name": "Desjardins",
 *   "firstname": "Yvonne",
 *   "birthday": "1994-07-06T00:00:00.000+00:00",
 *   "email": null,
 *   "address": null,
 *   "zip": null,
 *   "city": null,
 *   "type": "HOME",
 *   "phoneNumber": "+41 24 123 00 00"
 * }
 * ```
 *
 * Note that this DTO deliberately excludes local-only fields like 'status' and 'remote_id'
 * that are used in the local Contact entity but aren't part of the server's data model.
 *
 * @property id The unique identifier assigned by the server
 * @property name The contact's family name (required)
 * @property firstname The contact's given name (optional)
 * @property birthday The contact's birth date, stored as a string (optional)
 * @property email The contact's email address (optional)
 * @property address The contact's street address (optional)
 * @property zip The contact's postal code (optional)
 * @property city The contact's city of residence (optional)
 * @property type The type of phone number (HOME, OFFICE, MOBILE, or FAX) (optional)
 * @property phoneNumber The contact's phone number (optional)
 *
 * @see Contact The local entity this DTO gets converted to
 * @see PhoneTypeConverter For type field serialization
 */
data class ContactDTO(
    val id: Long?,
    val name: String,
    val firstname: String?,
    val birthday: String?,
    val email: String?,
    val address: String?,
    val zip: String?,
    val city: String?,
    val type: PhoneType?,
    val phoneNumber: String?
) {
    companion object {
        /**
         * Converts a Contact entity to its DTO representation.
         *
         * This method is used when sending a local Contact entity to the server.
         *
         * @param contact The local Contact entity to convert
         * @return A new ContactDTO object with the same data as the input entity
         */
        fun toDTO(contact: Contact): ContactDTO {
            return ContactDTO(
                id = contact.remote_id,
                name = contact.name,
                firstname = contact.firstname,
                birthday = contact.birthday,
                email = contact.email,
                address = contact.address,
                zip = contact.zip,
                city = contact.city,
                type = contact.type,
                phoneNumber = contact.phoneNumber
            )
        }
    }
}