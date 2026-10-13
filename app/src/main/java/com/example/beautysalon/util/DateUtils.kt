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
            if (character.isLowerCase()) character.titlecase(russianLocale) else character.toString()
        }
    }

    fun parseTimeToToday(hhmm: String): Long? {
        val match = Regex("""^(\d{2}):(\d{2})$""").matchEntire(hhmm) ?: return null
        val hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}