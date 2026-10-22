package com.example

import com.example.model.Categories
import com.example.util.BanglaUtils
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun banglaDigitsConversion_isCorrect() {
    val english = "0123456789"
    val bangla = BanglaUtils.toBanglaDigits(english)
    assertEquals("০১২৩৪৫৬৭৮৯", bangla)
  }

  @Test
  fun toEnglishDigits_isCorrect() {
    val bangla = "০১২৩৪৫৬৭৮৯"
    val english = BanglaUtils.toEnglishDigits(bangla)
    assertEquals("0123456789", english)
  }

  @Test
  fun yearRange_isBanglaDigits() {
    val years = listOf(2024, 2025, 2026, 2027, 2028, 2029, 2030)
    val banglaYears = years.map { BanglaUtils.toBanglaDigits(it) }
    assertEquals(
      listOf("২০২৪", "২০২৫", "২০২৬", "২০২৭", "২০২৮", "২০২৯", "২০৩০"),
      banglaYears
    )
  }

  @Test
  fun takaFormatting_isCorrect() {
    val takaFormatted = BanglaUtils.formatTaka(2500.0, useBangla = true)
    assertEquals("৳ ২,৫০০", takaFormatted)

    val englishFormatted = BanglaUtils.formatTaka(2500.0, useBangla = false)
    assertEquals("৳ 2,500", englishFormatted)
  }

  @Test
  fun categoryLookup_isCorrect() {
    val bazar = Categories.findByName("বাজার")
    assertEquals("বাজার", bazar.nameBn)
    assertEquals("🧺", bazar.emoji)

    val wifi = Categories.findByName("ওয়াইফাই বিল")
    assertEquals("ওয়াইফাই বিল", wifi.nameBn)
    assertEquals("📶", wifi.emoji)
  }
}
