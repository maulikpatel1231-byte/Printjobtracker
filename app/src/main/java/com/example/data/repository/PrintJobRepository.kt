package com.example.data.repository

import com.example.data.local.PrintJobDao
import com.example.data.local.PrintJobEntity
import kotlinx.coroutines.flow.Flow

class PrintJobRepository(private val dao: PrintJobDao) {

  val allJobs: Flow<List<PrintJobEntity>> = dao.getAllJobs()

  suspend fun insertJob(job: PrintJobEntity): Long {
    return dao.insertJob(job)
  }

  suspend fun updateJob(job: PrintJobEntity) {
    dao.updateJob(job.copy(updatedAt = System.currentTimeMillis()))
  }

  suspend fun deleteJob(job: PrintJobEntity) {
    dao.deleteJob(job)
  }

  suspend fun deleteJobById(id: Long) {
    dao.deleteJobById(id)
  }

  suspend fun toggleCompletion(job: PrintJobEntity) {
    val newStatus = !job.isCompleted
    val completedDate = if (newStatus) System.currentTimeMillis() else null
    dao.updateCompletionStatus(
      id = job.id,
      isCompleted = newStatus,
      completedDate = completedDate,
      updatedAt = System.currentTimeMillis()
    )
  }

  suspend fun toggleDieStatus(job: PrintJobEntity) {
    val newStatus = !job.isDieArrived
    val date = if (newStatus) System.currentTimeMillis() else null
    dao.updateDieStatus(
      id = job.id,
      isArrived = newStatus,
      arrivalDate = date,
      updatedAt = System.currentTimeMillis()
    )
  }

  suspend fun toggleEmbossStatus(job: PrintJobEntity) {
    val newStatus = !job.isEmbossArrived
    val date = if (newStatus) System.currentTimeMillis() else null
    dao.updateEmbossStatus(
      id = job.id,
      isArrived = newStatus,
      arrivalDate = date,
      updatedAt = System.currentTimeMillis()
    )
  }

  // No default data seeding. Only user entered data will be stored and managed.
  suspend fun seedInitialDataIfEmpty() {
    // Intentionally left empty as per user request to not preload default mock data.
  }
}
