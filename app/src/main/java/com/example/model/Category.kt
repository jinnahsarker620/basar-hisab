package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class Category(
  val id: String,
  val nameBn: String,
  val nameEn: String,
  val emoji: String,
  val bgColor: Color,
  val fgColor: Color
)

object Categories {
  val BAZAR = Category("bazar", "বাজার", "Bazar", "🧺", CatBazarBg, CatBazarFg)
  val CURRENT_BILL = Category("current", "কারেন্ট বিল", "Electricity", "⚡", CatCurrentBg, CatCurrentFg)
  val GAS = Category("gas", "গ্যাস", "Gas", "🔥", CatGasBg, CatGasFg)
  val WIFI = Category("wifi", "ওয়াইফাই বিল", "WiFi", "📶", CatWifiBg, CatWifiFg)
  val RENT = Category("rent", "বাসা ভাড়া", "House Rent", "🏠", CatRentBg, CatRentFg)
  val MEDICAL = Category("medical", "ওষুধ ও চিকিৎসা", "Medical", "💊", CatMedicalBg, CatMedicalFg)
  val EDUCATION = Category("education", "পড়াশোনা", "Education", "📚", CatEducationBg, CatEducationFg)
  val TRANSPORT = Category("transport", "যাতায়াত", "Transport", "🚗", CatTransportBg, CatTransportFg)
  val OTHER = Category("other", "অন্যান্য", "Other", "💰", CatOtherBg, CatOtherFg)

  val ALL = listOf(
    BAZAR,
    CURRENT_BILL,
    GAS,
    WIFI,
    RENT,
    MEDICAL,
    EDUCATION,
    TRANSPORT,
    OTHER
  )

  fun findByName(name: String): Category {
    return ALL.find { it.nameBn.equals(name, ignoreCase = true) || it.nameEn.equals(name, ignoreCase = true) } ?: OTHER
  }
}
