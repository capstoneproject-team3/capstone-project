package com.example.personalfinancetracker

import android.content.Context
import androidx.work.*
import com.example.personalfinancetracker.data.entity.Bill
import java.util.concurrent.TimeUnit

fun scheduleReminder(context: Context, bill: Bill) {
    // Calculate delay: time until the bill's due date
    val delay = bill.dueDate - System.currentTimeMillis()

    // Don't schedule if the due date is in the past
    if (delay <= 0) {
        return
    }

    val data = workDataOf(
        "billName" to bill.name,
        "billAmount" to bill.amount
    )

    // Schedules a one-time work request to run the ReminderWorker
    val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}