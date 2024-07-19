package com.innovativetools.assignment.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getGreeting(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    fun parseChartLabelToDate(chartLabel: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val date = sdf.parse(chartLabel)
            Calendar.getInstance().apply {
                if (date != null) {
                    time = date
                }
            }
        } catch (e: ParseException) {
            null
        }
    }

    fun updateDateRangeText(selectedStartDate: Calendar, selectedEndDate: Calendar): String {
        val startDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedStartDate.time)
        val endDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedEndDate.time)
        return "$startDateString - $endDateString"
    }

}
