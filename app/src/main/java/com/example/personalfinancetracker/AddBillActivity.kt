package com.example.personalfinancetracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.data.entity.Bill
import com.example.personalfinancetracker.data.entity.viewmodel.BillViewModel
import com.example.personalfinancetracker.databinding.ActivityAddBillBinding
import java.text.SimpleDateFormat
import java.util.*

class AddBillActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBillBinding
    private val billViewModel: BillViewModel by viewModels()

    // Holds the selected date and time for the bill due date
    private val billDueDate: Calendar = Calendar.getInstance()

    // Formats for displaying the date/time in the EditText fields
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set action bar title and back button (matches Prince's style)
        supportActionBar?.title = "Add Recurring Bill"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityAddBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupDateTimePickers()
        setupActionButtons() // 👈 NEW: This handles the Save and Cancel buttons

        // Initialize fields with current date/time as a placeholder
        binding.editDueDate.setText(dateFormat.format(billDueDate.time))
        binding.editDueTime.setText(timeFormat.format(billDueDate.time))
    }

    private fun setupSpinners() {
        val recurrenceOptions = resources.getStringArray(R.array.recurrence_options)
        binding.spinnerRecurrence.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, recurrenceOptions
        )
    }

    private fun setupDateTimePickers() {
        // --- Date Picker Setup ---
        binding.editDueDate.setOnClickListener {
            val year = billDueDate.get(Calendar.YEAR)
            val month = billDueDate.get(Calendar.MONTH)
            val day = billDueDate.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Update date components
                    billDueDate.set(selectedYear, selectedMonth, selectedDay)

                    // 🚩 CRITICAL FIX: Reset time components to 0 on date selection
                    // The time picker will set the exact time later.
                    billDueDate.set(Calendar.HOUR_OF_DAY, 0)
                    billDueDate.set(Calendar.MINUTE, 0)
                    billDueDate.set(Calendar.SECOND, 0)
                    billDueDate.set(Calendar.MILLISECOND, 0)

                    binding.editDueDate.setText(dateFormat.format(billDueDate.time))
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // --- Time Picker Setup ---
        binding.editDueTime.setOnClickListener {
            val hour = billDueDate.get(Calendar.HOUR_OF_DAY)
            val minute = billDueDate.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Update time components
                    billDueDate.set(Calendar.HOUR_OF_DAY, selectedHour)
                    billDueDate.set(Calendar.MINUTE, selectedMinute)
                    binding.editDueTime.setText(timeFormat.format(billDueDate.time))
                },
                hour, minute, true
            )
            timePickerDialog.show()
        }
    }

    private fun setupActionButtons() {
        // Save button listener
        binding.btnSaveBill.setOnClickListener {
            saveBillAndScheduleReminder()
        }

        // 🚩 CANCEL BUTTON FIX: Closes the activity
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }


    private fun saveBillAndScheduleReminder() {
        val name = binding.editBillName.text.toString().trim()
        val amount = binding.editBillAmount.text.toString().toDoubleOrNull()
        val recurrence = binding.spinnerRecurrence.selectedItem.toString()

        val currentTimeMs = System.currentTimeMillis()
        val selectedDateTimeMs = billDueDate.timeInMillis

        // --- Input Validation ---
        if (name.isEmpty() || amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid name and amount.", Toast.LENGTH_LONG).show()
            return
        }

        // Check if the scheduled time is in the future
        if (selectedDateTimeMs <= currentTimeMs) {
            Toast.makeText(this, "Please select a future date and time for the reminder.", Toast.LENGTH_LONG).show()
            return
        }
        // --- End Validation ---

        // Create the Bill object using the combined timestamp
        val bill = Bill(name = name, amount = amount, dueDate = selectedDateTimeMs, recurrence = recurrence)

        // 1. Save the bill
        billViewModel.insert(bill)

        // 2. Schedule the reminder
        scheduleReminder(this, bill)

        Toast.makeText(this, "Bill saved and reminder scheduled for: ${dateFormat.format(billDueDate.time)} ${timeFormat.format(billDueDate.time)}", Toast.LENGTH_LONG).show()

        finish()
    }

    // Handles the UP button (back arrow) on the ActionBar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}