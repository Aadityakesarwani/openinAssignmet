package com.innovativetools.assignment.utils


import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
                time = date
            }
        } catch (e: ParseException) {
            null
        }
    }

    fun updateDateRangeText(selectedStartDate: Calendar, selectedEndDate: Calendar): String {
        val startDateString = SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedStartDate.time)
        val endDateString = SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedEndDate.time)
        return "$startDateString - $endDateString"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDatePickerDialog(context: Context?, selectedStartDate: Calendar, selectedEndDate: Calendar, onDateSet: (Calendar, Calendar) -> Unit) {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    selectedStartDate.apply {
                        set(year, month, 1)
                    }
                    selectedEndDate.apply {
                        set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                    onDateSet(selectedStartDate, selectedEndDate)
                },
                selectedStartDate.get(Calendar.YEAR),
                selectedStartDate.get(Calendar.MONTH),
                selectedStartDate.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog?.show()
    }
}
