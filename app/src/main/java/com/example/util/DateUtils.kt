package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
  private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
  private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

  fun formatDate(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return "Not set"
    return dateFormat.format(Date(timestamp))
  }

  fun formatDateTime(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return "Not set"
    return dateTimeFormat.format(Date(timestamp))
  }

  fun formatShortDate(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return "-"
    return shortDateFormat.format(Date(timestamp))
  }

  fun isToday(timestamp: Long): Boolean {
    val target = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()
    return target.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
        target.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
  }

  fun formatQuantity(quantity: Int): String {
    return NumberFormat.getIntegerInstance(Locale.getDefault()).format(quantity)
  }
}
