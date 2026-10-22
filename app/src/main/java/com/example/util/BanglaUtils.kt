package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object BanglaUtils {

  private val BANGLA_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

  val BANGLA_MONTHS = arrayOf(
    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
  )

  val MONTHS_LIST: List<String> = BANGLA_MONTHS.toList()

  fun getMonthName(monthIndex: Int): String {
    return BANGLA_MONTHS.getOrElse(monthIndex) { "" }
  }

  private val BANGLA_DAYS = arrayOf(
    "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
  )

  /**
   * Converts English digits in a string or number to Bangla digits.
   */
  fun toBanglaDigits(numberStr: String): String {
    val sb = StringBuilder()
    for (char in numberStr) {
      if (char in '0'..'9') {
        sb.append(BANGLA_DIGITS[char - '0'])
      } else {
        sb.append(char)
      }
    }
    return sb.toString()
  }

  fun toBanglaDigits(number: Number): String {
    return toBanglaDigits(number.toString())
  }

  /**
   * Converts Bangla digits in a string to English standard digits.
   */
  fun toEnglishDigits(str: String): String {
    val sb = StringBuilder()
    for (char in str) {
      val idx = BANGLA_DIGITS.indexOf(char)
      if (idx != -1) {
        sb.append(idx)
      } else {
        sb.append(char)
      }
    }
    return sb.toString()
  }

  /**
   * Format currency with Taka sign (৳) in either Bangla or English digits.
   */
  fun formatTaka(amount: Double, useBangla: Boolean = true): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
    val formattedNumber = formatter.format(amount)

    return if (useBangla) {
      "৳ " + toBanglaDigits(formattedNumber)
    } else {
      "৳ $formattedNumber"
    }
  }

  fun formatTaka(amount: Long, useBangla: Boolean = true): String {
    return formatTaka(amount.toDouble(), useBangla)
  }

  fun formatTaka(amount: Int, useBangla: Boolean = true): String {
    return formatTaka(amount.toDouble(), useBangla)
  }

  /**
   * Formats a timestamp into Bengali date: e.g. "২২ অক্টোবর ২০২৬"
   */
  fun formatTarikh(timeMillis: Long, useBangla: Boolean = true): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val monthIndex = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)

    val monthName = BANGLA_MONTHS.getOrElse(monthIndex) { "" }

    return if (useBangla) {
      "${toBanglaDigits(day)} $monthName ${toBanglaDigits(year)}"
    } else {
      "$day $monthName $year"
    }
  }

  /**
   * Returns a short date: e.g. "১২ নভেম্বর"
   */
  fun formatShortTarikh(timeMillis: Long, useBangla: Boolean = true): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val monthIndex = cal.get(Calendar.MONTH)
    val monthName = BANGLA_MONTHS.getOrElse(monthIndex) { "" }

    return if (useBangla) {
      "${toBanglaDigits(day)} $monthName"
    } else {
      "$day $monthName"
    }
  }

  /**
   * Returns current day with day of week: e.g. "বুধবার, ২২ অক্টোবর"
   */
  fun getTodayBanglaHeader(): String {
    val cal = Calendar.getInstance()
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val monthIndex = cal.get(Calendar.MONTH)

    val dayName = BANGLA_DAYS.getOrElse(dayOfWeek - 1) { "" }
    val monthName = BANGLA_MONTHS.getOrElse(monthIndex) { "" }

    return "$dayName, ${toBanglaDigits(day)} $monthName"
  }

  fun getCurrentMonthName(): String {
    val cal = Calendar.getInstance()
    return BANGLA_MONTHS.getOrElse(cal.get(Calendar.MONTH)) { "" }
  }
}
