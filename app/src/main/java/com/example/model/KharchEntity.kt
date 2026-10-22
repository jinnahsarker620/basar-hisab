package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "kharch_table")
data class KharchEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val category: String,
  val taka: Double,
  val dateMillis: Long = System.currentTimeMillis(),
  val tarikhDisplay: String = "",
  val note: String? = null,
  val paymentMethod: String = "নগদ",
  val member: String = "পরিবার",
  val bazarKoreche: String = "আবদুল্লাহ আল মামুন",
  val month: Int = Calendar.getInstance().get(Calendar.MONTH),
  val year: Int = Calendar.getInstance().get(Calendar.YEAR)
)
