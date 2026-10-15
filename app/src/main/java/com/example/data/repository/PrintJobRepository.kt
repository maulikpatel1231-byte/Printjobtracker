package com.example.data.repository

import com.example.data.local.PrintJobDao
import com.example.data.local.PrintJobEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

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

  suspend fun seedInitialDataIfEmpty() {
    val count = dao.getJobCount()
    if (count == 0) {
      val now = System.currentTimeMillis()
      val cal = Calendar.getInstance()

      cal.timeInMillis = now
      cal.add(Calendar.DAY_OF_YEAR, -3)
      val threeDaysAgo = cal.timeInMillis

      cal.timeInMillis = now
      cal.add(Calendar.DAY_OF_YEAR, -2)
      val twoDaysAgo = cal.timeInMillis

      cal.timeInMillis = now
      cal.add(Calendar.DAY_OF_YEAR, -1)
      val oneDayAgo = cal.timeInMillis

      val sampleJobs = listOf(
        PrintJobEntity(
          jobNumber = "JOB-1024",
          clientName = "Shree Sweets (1kg Mithai Box)",
          quantity = 5000,
          jobArrivedDate = threeDaysAgo,
          dieArrivedDate = twoDaysAgo,
          isDieArrived = true,
          embossArrivedDate = oneDayAgo,
          isEmbossArrived = true,
          isCompleted = false,
          notes = "350 GSM FBB board, drip-off varnish, gold foil on logo. Priority run.",
          paperDetails = "350 GSM ITC Board",
          isUrgent = true,
          createdAt = threeDaysAgo,
          updatedAt = oneDayAgo
        ),
        PrintJobEntity(
          jobNumber = "JOB-1025",
          clientName = "Apex Pharma (Cough Syrup Outer Cartons)",
          quantity = 25000,
          jobArrivedDate = twoDaysAgo,
          dieArrivedDate = oneDayAgo,
          isDieArrived = true,
          embossArrivedDate = null,
          isEmbossArrived = false,
          isCompleted = false,
          notes = "Braille emboss plate awaited. Cyan + Magenta + Black + Pantone 286C.",
          paperDetails = "300 GSM Cyber XLC",
          isUrgent = false,
          createdAt = twoDaysAgo,
          updatedAt = oneDayAgo
        ),
        PrintJobEntity(
          jobNumber = "JOB-1026",
          clientName = "Royal Heritage (Wedding Invite Folio)",
          quantity = 1200,
          jobArrivedDate = oneDayAgo,
          dieArrivedDate = null,
          isDieArrived = false,
          embossArrivedDate = null,
          isEmbossArrived = false,
          isCompleted = false,
          notes = "Die making in progress with vendor. Deep blind embossing required.",
          paperDetails = "400 GSM Cotton Textured White",
          isUrgent = true,
          createdAt = oneDayAgo,
          updatedAt = oneDayAgo
        ),
        PrintJobEntity(
          jobNumber = "JOB-1023",
          clientName = "Gourmet Tea (Mono Carton 250g)",
          quantity = 10000,
          jobArrivedDate = threeDaysAgo,
          dieArrivedDate = threeDaysAgo,
          isDieArrived = true,
          embossArrivedDate = twoDaysAgo,
          isEmbossArrived = true,
          isCompleted = true,
          completedDate = now,
          notes = "Printed, laminated, die-cut and inspected. Ready for dispatch.",
          paperDetails = "320 GSM MetPET Silver",
          isUrgent = false,
          createdAt = threeDaysAgo,
          updatedAt = now
        )
      )
      dao.insertAll(sampleJobs)
    }
  }
}
