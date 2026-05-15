package com.example.smartpantry.presentation.home

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}

fun convertDateToMillis(
    date: String
): Long {

    return try {

        val formatter =
            SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            )

        formatter.parse(date)?.time
            ?: System.currentTimeMillis()

    } catch (exception: Exception) {

        System.currentTimeMillis()
    }
}