// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.models

import android.content.Context
import android.content.res.Resources
import ch.heigvd.iict.and.rest.R

/**
 * Enum class representing the type of a phone number.
 */
enum class PhoneType {
    MOBILE,
    FAX,
    HOME,
    OFFICE;

    // Get the associated string resource id
    // We cannot override oString here because we don't have access to app resources or stringResource function
    fun stringRes(): Int {
        return when (this) {
            MOBILE -> R.string.phonetype_mobile
            FAX -> R.string.phonetype_fax
            HOME -> R.string.phonetype_home
            OFFICE -> R.string.phonetype_office
        }
    }
}