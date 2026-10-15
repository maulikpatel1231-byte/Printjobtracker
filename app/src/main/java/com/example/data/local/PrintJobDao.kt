package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintJobDao {
  @Query("SELECT * FROM print_jobs ORDER BY createdAt DESC")
  fun getAllJobs(): Flow<List<PrintJobEntity>>

  @Query("SELECT * FROM print_jobs WHERE id = :id LIMIT 1")
  suspend fun getJobById(id: Long): PrintJobEntity?

  @Query("SELECT * FROM print_jobs WHERE LOWER(jobNumber) = LOWER(:jobNumber) LIMIT 1")
  suspend fun getJobByNumber(jobNumber: String): PrintJobEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJob(job: PrintJobEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(jobs: List<PrintJobEntity>)

  @Update
  suspend fun updateJob(job: PrintJobEntity)

  @Delete
  suspend fun deleteJob(job: PrintJobEntity)

  @Query("DELETE FROM print_jobs WHERE id = :id")
  suspend fun deleteJobById(id: Long)

  @Query("SELECT COUNT(*) FROM print_jobs")
  suspend fun getJobCount(): Int

  @Query("UPDATE print_jobs SET isCompleted = :isCompleted, completedDate = :completedDate, updatedAt = :updatedAt WHERE id = :id")
  suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean, completedDate: Long?, updatedAt: Long)

  @Query("UPDATE print_jobs SET isDieArrived = :isArrived, dieArrivedDate = :arrivalDate, updatedAt = :updatedAt WHERE id = :id")
  suspend fun updateDieStatus(id: Long, isArrived: Boolean, arrivalDate: Long?, updatedAt: Long)

  @Query("UPDATE print_jobs SET isEmbossArrived = :isArrived, embossArrivedDate = :arrivalDate, updatedAt = :updatedAt WHERE id = :id")
  suspend fun updateEmbossStatus(id: Long, isArrived: Boolean, arrivalDate: Long?, updatedAt: Long)
}
