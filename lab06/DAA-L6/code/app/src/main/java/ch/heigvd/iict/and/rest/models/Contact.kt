// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.heigvd.iict.and.rest.api.dto.ContactDTO

/**
 * Class representing a contact.
 */
@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var remote_id: Long? = null,
    var status: Status = Status.OK,
    var name: String,
    var firstname: String? = null,
    var birthday: String? = null,
    var email: String? = null,
    var address: String? = null,
    var zip: String? = null,
    var city: String? = null,
    var type: PhoneType? = null,
    var phoneNumber: String? = null
) {
    companion object {
        /**
         * Convert a ContactDTO to a Contact.
         */
        fun fromDTO(dto: ContactDTO): Contact {
            return Contact(
                remote_id = dto.id,  // Server's ID becomes our remote_id
                status = Status.OK,
                name = dto.name,
                firstname = dto.firstname,
                birthday = dto.birthday,
                email = dto.email,
                address = dto.address,
                zip = dto.zip,
                city = dto.city,
                type = dto.type,
                phoneNumber = dto.phoneNumber
            )
        }
    }
}