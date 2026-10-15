package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PrintDatabase
import com.example.data.local.PrintJobEntity
import com.example.data.repository.PrintJobRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class JobStatusFilter {
  ALL,
  PENDING,
  COMPLETED,
  DIE_AWAITED,
  EMBOSS_AWAITED
}

enum class JobSortOption(val displayName: String) {
  NEWEST_ARRIVED("Newest Arrival First"),
  OLDEST_ARRIVED("Oldest Arrival First"),
  JOB_NUMBER("Job Number (A-Z)"),
  QUANTITY_DESC("Quantity (High to Low)")
}

data class JobStats(
  val totalJobs: Int = 0,
  val pendingJobs: Int = 0,
  val completedJobs: Int = 0,
  val urgentJobs: Int = 0,
  val arrivedToday: Int = 0
)

class PrintJobViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: PrintJobRepository

  init {
    val database = PrintDatabase.getDatabase(application)
    repository = PrintJobRepository(database.printJobDao())
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  val searchQuery = MutableStateFlow("")
  val statusFilter = MutableStateFlow(JobStatusFilter.ALL)
  val sortOption = MutableStateFlow(JobSortOption.NEWEST_ARRIVED)

  // Selected job for detail dialog
  val selectedJobForDetail = MutableStateFlow<PrintJobEntity?>(null)
  
  // Job for editing (null means adding new)
  val editingJob = MutableStateFlow<PrintJobEntity?>(null)
  val showAddEditSheet = MutableStateFlow(false)

  // Status message for Snackbars
  val snackbarMessage = MutableStateFlow<String?>(null)

  val allJobs: StateFlow<List<PrintJobEntity>> = repository.allJobs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val stats: StateFlow<JobStats> = allJobs.combine(MutableStateFlow(Unit)) { jobs, _ ->
    val total = jobs.size
    val pending = jobs.count { !it.isCompleted }
    val completed = jobs.count { it.isCompleted }
    val urgent = jobs.count { it.isUrgent && !it.isCompleted }
    val arrivedToday = jobs.count { DateUtils.isToday(it.jobArrivedDate) }
    JobStats(
      totalJobs = total,
      pendingJobs = pending,
      completedJobs = completed,
      urgentJobs = urgent,
      arrivedToday = arrivedToday
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JobStats())

  val filteredJobs: StateFlow<List<PrintJobEntity>> = combine(
    allJobs,
    searchQuery,
    statusFilter,
    sortOption
  ) { jobs, query, filter, sort ->
    var result = jobs

    // Filter by query (job number, client name, notes, paper)
    if (query.isNotBlank()) {
      val trimmed = query.trim().lowercase()
      result = result.filter { job ->
        job.jobNumber.lowercase().contains(trimmed) ||
            job.clientName.lowercase().contains(trimmed) ||
            job.notes.lowercase().contains(trimmed) ||
            job.paperDetails.lowercase().contains(trimmed)
      }
    }

    // Filter by status
    result = when (filter) {
      JobStatusFilter.ALL -> result
      JobStatusFilter.PENDING -> result.filter { !it.isCompleted }
      JobStatusFilter.COMPLETED -> result.filter { it.isCompleted }
      JobStatusFilter.DIE_AWAITED -> result.filter { !it.isCompleted && !it.isDieArrived }
      JobStatusFilter.EMBOSS_AWAITED -> result.filter { !it.isCompleted && !it.isEmbossArrived }
    }

    // Sort
    result = when (sort) {
      JobSortOption.NEWEST_ARRIVED -> result.sortedByDescending { it.jobArrivedDate }
      JobSortOption.OLDEST_ARRIVED -> result.sortedBy { it.jobArrivedDate }
      JobSortOption.JOB_NUMBER -> result.sortedBy { it.jobNumber.lowercase() }
      JobSortOption.QUANTITY_DESC -> result.sortedByDescending { it.quantity }
    }

    result
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun onSearchQueryChanged(newQuery: String) {
    searchQuery.value = newQuery
  }

  fun onStatusFilterSelected(filter: JobStatusFilter) {
    statusFilter.value = filter
  }

  fun onSortOptionSelected(sort: JobSortOption) {
    sortOption.value = sort
  }

  fun toggleJobCompletion(job: PrintJobEntity) {
    viewModelScope.launch {
      val willBeCompleted = !job.isCompleted
      repository.toggleCompletion(job)
      snackbarMessage.value = if (willBeCompleted) {
        "Job #${job.jobNumber} marked as Completed! ✓"
      } else {
        "Job #${job.jobNumber} marked as Pending ⏳"
      }
      // If currently viewed in detail, update it
      if (selectedJobForDetail.value?.id == job.id) {
        selectedJobForDetail.value = job.copy(isCompleted = willBeCompleted)
      }
    }
  }

  fun toggleDieStatus(job: PrintJobEntity) {
    viewModelScope.launch {
      repository.toggleDieStatus(job)
      if (selectedJobForDetail.value?.id == job.id) {
        val willBeArrived = !job.isDieArrived
        selectedJobForDetail.value = job.copy(isDieArrived = willBeArrived)
      }
    }
  }

  fun toggleEmbossStatus(job: PrintJobEntity) {
    viewModelScope.launch {
      repository.toggleEmbossStatus(job)
      if (selectedJobForDetail.value?.id == job.id) {
        val willBeArrived = !job.isEmbossArrived
        selectedJobForDetail.value = job.copy(isEmbossArrived = willBeArrived)
      }
    }
  }

  fun openAddJobDialog() {
    editingJob.value = null
    showAddEditSheet.value = true
  }

  fun openEditJobDialog(job: PrintJobEntity) {
    editingJob.value = job
    showAddEditSheet.value = true
  }

  fun closeAddEditSheet() {
    showAddEditSheet.value = false
    editingJob.value = null
  }

  fun selectJobForDetail(job: PrintJobEntity?) {
    selectedJobForDetail.value = job
  }

  fun saveJob(
    jobNumber: String,
    clientName: String,
    quantity: Int,
    jobArrivedDate: Long,
    isDieArrived: Boolean,
    dieArrivedDate: Long?,
    isEmbossArrived: Boolean,
    embossArrivedDate: Long?,
    isCompleted: Boolean,
    paperDetails: String,
    notes: String,
    isUrgent: Boolean
  ) {
    viewModelScope.launch {
      val currentEditing = editingJob.value
      if (currentEditing != null) {
        val updated = currentEditing.copy(
          jobNumber = jobNumber.trim(),
          clientName = clientName.trim(),
          quantity = quantity,
          jobArrivedDate = jobArrivedDate,
          isDieArrived = isDieArrived,
          dieArrivedDate = if (isDieArrived) dieArrivedDate ?: System.currentTimeMillis() else null,
          isEmbossArrived = isEmbossArrived,
          embossArrivedDate = if (isEmbossArrived) embossArrivedDate ?: System.currentTimeMillis() else null,
          isCompleted = isCompleted,
          completedDate = if (isCompleted) (currentEditing.completedDate ?: System.currentTimeMillis()) else null,
          paperDetails = paperDetails.trim(),
          notes = notes.trim(),
          isUrgent = isUrgent
        )
        repository.updateJob(updated)
        snackbarMessage.value = "Job #${updated.jobNumber} updated successfully"
        if (selectedJobForDetail.value?.id == updated.id) {
          selectedJobForDetail.value = updated
        }
      } else {
        val newJob = PrintJobEntity(
          jobNumber = jobNumber.trim(),
          clientName = clientName.trim(),
          quantity = quantity,
          jobArrivedDate = jobArrivedDate,
          isDieArrived = isDieArrived,
          dieArrivedDate = if (isDieArrived) dieArrivedDate ?: System.currentTimeMillis() else null,
          isEmbossArrived = isEmbossArrived,
          embossArrivedDate = if (isEmbossArrived) embossArrivedDate ?: System.currentTimeMillis() else null,
          isCompleted = isCompleted,
          completedDate = if (isCompleted) System.currentTimeMillis() else null,
          paperDetails = paperDetails.trim(),
          notes = notes.trim(),
          isUrgent = isUrgent
        )
        repository.insertJob(newJob)
        snackbarMessage.value = "New Job #${newJob.jobNumber} created!"
      }
      closeAddEditSheet()
    }
  }

  fun deleteJob(job: PrintJobEntity) {
    viewModelScope.launch {
      repository.deleteJob(job)
      if (selectedJobForDetail.value?.id == job.id) {
        selectedJobForDetail.value = null
      }
      snackbarMessage.value = "Job #${job.jobNumber} deleted"
    }
  }

  fun clearSnackbarMessage() {
    snackbarMessage.value = null
  }
}
