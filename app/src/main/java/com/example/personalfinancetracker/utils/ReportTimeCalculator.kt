package com.example.personalfinancetracker.utils

import java.util.Calendar

object ReportTimeCalculator {
    /** Returns a Pair of (Start Timestamp, End Timestamp) for the current month. */
    fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // 1. Calculate Start Date (First day of the current month, 00:00:00.000)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // 2. Calculate End Date (Last millisecond of the current month)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    /** Returns a Pair of (Start Timestamp, End Timestamp) for the current quarter. */
    fun getCurrentQuarterRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) // 0=Jan, 11=Dec

        // Determine the start month of the current quarter (0, 3, 6, or 9)
        val startMonth = (currentMonth / 3) * 3

        // 1. Calculate Start Date
        calendar.set(Calendar.MONTH, startMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // 2. Calculate End Date
        calendar.add(Calendar.MONTH, 2) // Move to the last month of the quarter
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    /** Returns a Pair of (Start Timestamp, End Timestamp) for the current year. */
    fun getCurrentYearRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // 1. Calculate Start Date (Jan 1st, 00:00:00.000)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // 2. Calculate End Date (Dec 31st, 23:59:59.999)
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }
}