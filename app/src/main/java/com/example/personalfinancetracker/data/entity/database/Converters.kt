package com.example.personalfinancetracker.data.database

import androidx.room.TypeConverter
import com.example.personalfinancetracker.data.entity.RecurrencePeriod
import com.example.personalfinancetracker.data.entity.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromRecurrencePeriod(value: RecurrencePeriod?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRecurrencePeriod(value: String?): RecurrencePeriod? {
        return value?.let { RecurrencePeriod.valueOf(it) }
    }
}