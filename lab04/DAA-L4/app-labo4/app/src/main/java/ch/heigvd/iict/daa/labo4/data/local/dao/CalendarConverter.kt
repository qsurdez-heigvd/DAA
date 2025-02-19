// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.local.dao

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.Date

/**
 * A converter class for converting between Calendar objects and their
 * corresponding timestamp representations for Room database.
 */
class CalendarConverter {

    /**
     * Converts a timestamp to a Calendar object.
     *
     * @param dateLong The timestamp to be converted.
     * @return The corresponding Calendar object.
     */
    @TypeConverter
    fun toCalendar(dateLong: Long) = Calendar.getInstance().apply {
        time = Date(dateLong)
    }

    /**
     * Converts a Calendar object to a timestamp.
     *
     * @param date The Calendar object to be converted.
     * @return The corresponding timestamp.
     */
    @TypeConverter
    fun fromCalendar(date: Calendar) = date.time.time // Long
}