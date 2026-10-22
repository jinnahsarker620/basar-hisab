package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_table")
data class MemberEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String
)
