// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.api.converters

import ch.heigvd.iict.and.rest.models.PhoneType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * A custom TypeAdapter for converting between PhoneType enums and JSON strings when using Gson.
 *
 * This adapter handles the serialization and deserialization of PhoneType enum values to and from
 * their string representations in JSON. It's designed to work with REST API responses where phone
 * types are represented as uppercase strings.
 *
 * The adapter supports all PhoneType enum values:
 * - HOME
 * - OFFICE
 * - MOBILE
 * - FAX
 *
 * Example JSON values: "HOME", "OFFICE", "MOBILE", "FAX"
 *
 * Usage in Gson configuration:
 * ```
 * val gson = GsonBuilder()
 *     .registerTypeAdapter(PhoneType::class.java, PhoneTypeConverter())
 *     .create()
 * ```
 *
 * This converter provides graceful handling of unknown phone types by returning null instead of
 * throwing exceptions, making it resilient to API changes or invalid data.
 *
 * @see TypeAdapter
 * @see PhoneType
 * @see JsonReader
 * @see JsonWriter
 */
class PhoneTypeConverter : TypeAdapter<PhoneType>() {

    /**
     * Writes a PhoneType enum value to its JSON representation.
     *
     * This method converts the PhoneType enum to its name string representation.
     * For example, PhoneType.MOBILE will be written as "MOBILE".
     *
     * @param out The JSON writer to write to
     * @param value The PhoneType value to be written, can be null
     *
     * Example outputs:
     * - PhoneType.HOME -> "HOME"
     * - PhoneType.MOBILE -> "MOBILE"
     * - null -> null
     */
    override fun write(out: JsonWriter, value: PhoneType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name)
        }
    }

    /**
     * Reads a JSON string representation and converts it to a PhoneType enum value.
     *
     * This method attempts to parse the input string into a valid PhoneType enum value.
     * If the string doesn't match any valid PhoneType, it returns null instead of throwing
     * an exception, providing graceful handling of invalid data.
     *
     * @param input The JSON reader to read from
     * @return The corresponding PhoneType enum value, or null if the input is invalid
     *         or doesn't match any known phone type
     *
     * Example inputs and outputs:
     * - "HOME" -> PhoneType.HOME
     * - "INVALID_TYPE" -> null
     * - null -> null
     */
    override fun read(input: JsonReader): PhoneType? {

        // First check if the next token is null
        if (input.peek() == com.google.gson.stream.JsonToken.NULL) {
            input.nextNull()
            return null
        }

        return try {
            val typeStr = input.nextString()
            PhoneType.valueOf(typeStr)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}