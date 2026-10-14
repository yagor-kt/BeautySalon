package com.example.beautysalon.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val russianLocale = Locale("ru", "RU")

    fun startOfDay(offsetDays: Int = 0): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, offsetDays)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun endOfDay(offsetDays: Int = 0): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, offsetDays)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    fun monthStart(): Long =
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun weekAgo(): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun formatDate(millis: Long): String =
        SimpleDateFormat("dd.MM.yyyy", russianLocale).format(Date(millis))

    fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", russianLocale).format(Date(millis))

    fun formatDateTime(millis: Long): String =
        SimpleDateFormat("dd.MM.yyyy, HH:mm", russianLocale).format(Date(millis))

    fun monthName(millis: Long): String {
        val month = SimpleDateFormat("LLLL yyyy", russianLocale).format(Date(millis))
        return month.replaceFirstChar { character ->
            if (character.isLowerCase()) {
                character.titlecase(russianLocale)
            } else {
                character.toString()
            }
        }
    }

    /**
     * Строго разбирает дату ДД.ММ.ГГГГ и время ЧЧ:ММ
     * в локальном часовом поясе. Возвращает null при любом
     * формате или значении, не соответствующем календарю.
     */
    fun parseDateTime(date: String, time: String): Long? {
        val dateMatch = Regex("""^(\d{2})\.(\d{2})\.(\d{4})$""")
            .matchEntire(date.trim())
            ?: return null

        val timeMatch = Regex("""^(\d{2}):(\d{2})$""")
            .matchEntire(time.trim())
            ?: return null

        val day = dateMatch.groupValues[1].toIntOrNull() ?: return null
        val month = dateMatch.groupValues[2].toIntOrNull() ?: return null
        val year = dateMatch.groupValues[3].toIntOrNull() ?: return null
        val hour = timeMatch.groupValues[1].toIntOrNull() ?: return null
        val minute = timeMatch.groupValues[2].toIntOrNull() ?: return null

        if (year < 1 || month !in 1..12 || day !in 1..31) return null
        if (hour !in 0..23 || minute !in 0..59) return null

        return try {
            Calendar.getInstance().apply {
                isLenient = false
                clear()
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun parseTimeToToday(hhmm: String): Long? =
        parseDateTime(formatDate(System.currentTimeMillis()), hhmm)
}