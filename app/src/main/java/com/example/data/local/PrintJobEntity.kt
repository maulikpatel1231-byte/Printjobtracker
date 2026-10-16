package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "print_jobs")
data class PrintJobEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val jobNumber: String,
  val clientName: String = "",
  val quantity: Int = 0,
  val jobArrivedDate: Long = System.currentTimeMillis(),
  val dieType: String = "REPEAT", // "REPEAT", "NEW", "NOT_REQUIRED"
  val dieArrivedDate: Long? = null,
  val isDieArrived: Boolean = false,
  val embossType: String = "REPEAT", // "REPEAT", "NEW", "NOT_REQUIRED"
  val embossArrivedDate: Long? = null,
  val isEmbossArrived: Boolean = false,
  val isCompleted: Boolean = false,
  val completedDate: Long? = null,
  val notes: String = "",
  val paperDetails: String = "",
  val isUrgent: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
