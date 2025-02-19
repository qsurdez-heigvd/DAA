// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.labo4.data.local.database

import android.content.Context
import androidx.room.TypeConverter
import ch.heigvd.iict.daa.labo4.R
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A converter class for converting between Calendar objects and their
 * corresponding timestamp representations for Room database.
 */
class CalendarConverter {

    /**
     * Converts a timestamp to a Calendar object.
     *
     * @param value The timestamp to be converted.
     * @return The corresponding Calendar object, or null if the value is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply {
                timeInMillis = it
            }
        }
    }

    /**
     * Converts a Calendar object to a timestamp.
     *
     * @param calendar The Calendar object to be converted.
     * @return The corresponding timestamp, or null if the calendar is null.
     */
    @TypeConverter
    fun dateToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    /**
     * Converts a Calendar date to a user-friendly time string.
     *
     * @param context The context to access resources.
     * @param date The Calendar date to be converted.
     * @return A user-friendly time string representing the date.
     */
    fun convertDateToFriendlyTime(context: Context, date: Calendar?): String {
        if (date == null) return ""
        val now = Calendar.getInstance()
        if (date.before(now)) {
            return context.getString(R.string.late)
        }

        val diffInMillis = date.timeInMillis - now.timeInMillis
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return when {
            diffInDays < 1 -> context.resources.getString(R.string.today)
            diffInDays < 2 -> context.resources.getString(R.string.tomorrow)
            diffInDays < 7 -> context.resources.getQuantityString(R.plurals.days, diffInDays.toInt(), diffInDays.toInt())
            diffInDays < 8 -> context.resources.getQuantityString(R.plurals.weeks, 1, 1)
            diffInDays < 14 -> context.resources.getQuantityString(R.plurals.weeks, 2, 2)
            diffInDays < 21 -> context.resources.getQuantityString(R.plurals.weeks, 3, 3)
            diffInDays < 30 -> context.resources.getQuantityString(R.plurals.weeks, 4, 4)
            diffInDays < 60 -> context.resources.getQuantityString(R.plurals.months, 1, 1)
            diffInDays < 90 -> context.resources.getQuantityString(R.plurals.months, 2, 2)
            diffInDays < 120 -> context.resources.getQuantityString(R.plurals.months, 3, 3)
            diffInDays < 182 -> context.resources.getQuantityString(R.plurals.months, 6, 6)
            diffInDays < 365 -> context.resources.getQuantityString(R.plurals.months, (diffInDays / 30).toInt(), (diffInDays / 30).toInt())
            else -> context.resources.getQuantityString(R.plurals.years, (diffInDays / 365).toInt(), (diffInDays / 365).toInt())
        }
    }
}