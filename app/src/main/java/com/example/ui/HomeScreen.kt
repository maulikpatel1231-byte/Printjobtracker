package com.example.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrintJobEntity
import com.example.ui.components.AddEditJobDialog
import com.example.ui.components.JobCard
import com.example.ui.components.JobDetailDialog
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningContainer
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: PrintJobViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val filteredJobs by viewModel.filteredJobs.collectAsState()
  val stats by viewModel.stats.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val statusFilter by viewModel.statusFilter.collectAsState()
  val sortOption by viewModel.sortOption.collectAsState()

  val showAddEditSheet by viewModel.showAddEditSheet.collectAsState()
  val editingJob by viewModel.editingJob.collectAsState()
  val selectedDetailJob by viewModel.selectedJobForDetail.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  var jobPendingDelete by remember { mutableStateOf<PrintJobEntity?>(null) }
  var showSortMenu by remember { mutableStateOf(false) }

  // Handle snackbar messages
  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbarMessage()
    }
  }

  // Quick share daily pending press report
  fun sharePendingJobsReport() {
    val pendingList = filteredJobs.filter { !it.isCompleted }
    val text = buildString {
      appendLine("📢 *PRINTJOB TRACKER - PENDING JOBS REPORT*")
      appendLine("Date: ${DateUtils.formatDate(System.currentTimeMillis())}")
      appendLine("Total Pending: ${stats.pendingJobs} jobs")
      appendLine("-----------------------------")
      if (pendingList.isEmpty()) {
        appendLine("No pending jobs! All jobs are completed. ✓")
      } else {
        pendingList.forEachIndexed { index, job ->
          appendLine("${index + 1}) *#${job.jobNumber}* - ${job.clientName}")
          appendLine("   • Qty: ${DateUtils.formatQuantity(job.quantity)} pcs")
          appendLine("   • Arrived: ${DateUtils.formatDate(job.jobArrivedDate)}")
          val dieStatus = when (job.dieType) {
            "REPEAT" -> "Repeat Die"
            "NEW" -> if (job.isDieArrived) "New Die - Arrived ✓" else "New Die - Awaited ⏳"
            else -> "N/A"
          }
          val embossStatus = when (job.embossType) {
            "REPEAT" -> "Repeat Emboss"
            "NEW" -> if (job.isEmbossArrived) "New Emboss - Arrived ✓" else "New Emboss - Awaited ⏳"
            else -> "N/A"
          }
          appendLine("   • Die: $dieStatus")
          appendLine("   • Emboss: $embossStatus")
          if (job.notes.isNotBlank()) appendLine("   • Note: ${job.notes}")
          appendLine()
        }
      }
    }
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Pending Jobs Report"))
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .imePadding(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(width = 58.dp, height = 38.dp)
            ) {
              androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_high5_logo),
                contentDescription = "High 5 Packaging Logo",
                modifier = Modifier.fillMaxSize()
              )
            }
            Column {
              Text(
                text = "High 5 Packaging",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Printing & Packaging Production Log",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { sharePendingJobsReport() },
            modifier = Modifier.testTag("share_report_button")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share Report",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { viewModel.openAddJobDialog() },
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("New Job", fontWeight = FontWeight.Bold) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_job_fab")
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // 1. Stats Counter Cards
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "Total Jobs",
          count = stats.totalJobs,
          bgColor = MaterialTheme.colorScheme.surfaceVariant,
          textColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        StatCard(
          title = "Pending ⏳",
          count = stats.pendingJobs,
          bgColor = StatusWarningContainer,
          textColor = StatusWarning
        )
        StatCard(
          title = "Completed ✓",
          count = stats.completedJobs,
          bgColor = StatusSuccessContainer,
          textColor = StatusSuccess
        )
        StatCard(
          title = "Arrived Today",
          count = stats.arrivedToday,
          bgColor = MaterialTheme.colorScheme.primaryContainer,
          textColor = MaterialTheme.colorScheme.primary
        )
      }

      // 2. Search Field with Clear Button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.onSearchQueryChanged(it) },
          placeholder = { Text("Search Job No, Client, Paper, Notes...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.primary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_jobs_input")
        )
      }

      // 3. Status Filter Chips (All, Pending, Completed, Die Awaited, Emboss Awaited)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = statusFilter == JobStatusFilter.ALL,
          onClick = { viewModel.onStatusFilterSelected(JobStatusFilter.ALL) },
          label = { Text("All (${stats.totalJobs})") },
          modifier = Modifier.testTag("filter_chip_all")
        )

        FilterChip(
          selected = statusFilter == JobStatusFilter.PENDING,
          onClick = { viewModel.onStatusFilterSelected(JobStatusFilter.PENDING) },
          label = { Text("Pending ⏳ (${stats.pendingJobs})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = StatusWarningContainer,
            selectedLabelColor = StatusWarning
          ),
          modifier = Modifier.testTag("filter_chip_pending")
        )

        FilterChip(
          selected = statusFilter == JobStatusFilter.COMPLETED,
          onClick = { viewModel.onStatusFilterSelected(JobStatusFilter.COMPLETED) },
          label = { Text("Completed ✓ (${stats.completedJobs})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = StatusSuccessContainer,
            selectedLabelColor = StatusSuccess
          ),
          modifier = Modifier.testTag("filter_chip_completed")
        )

        FilterChip(
          selected = statusFilter == JobStatusFilter.DIE_AWAITED,
          onClick = { viewModel.onStatusFilterSelected(JobStatusFilter.DIE_AWAITED) },
          label = { Text("Die Awaited") },
          modifier = Modifier.testTag("filter_chip_die_awaited")
        )

        FilterChip(
          selected = statusFilter == JobStatusFilter.EMBOSS_AWAITED,
          onClick = { viewModel.onStatusFilterSelected(JobStatusFilter.EMBOSS_AWAITED) },
          label = { Text("Emboss Awaited") },
          modifier = Modifier.testTag("filter_chip_emboss_awaited")
        )
      }

      // 4. Sort and Count Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Showing ${filteredJobs.size} ${if (filteredJobs.size == 1) "job" else "jobs"}",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { showSortMenu = true }
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = "Sort",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = sortOption.displayName,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
          ) {
            JobSortOption.values().forEach { option ->
              DropdownMenuItem(
                text = { Text(option.displayName) },
                onClick = {
                  viewModel.onSortOptionSelected(option)
                  showSortMenu = false
                }
              )
            }
          }
        }
      }

      // 5. Jobs List
      if (filteredJobs.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.size(64.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(32.dp)
                )
              }
            }
            Text(
              text = if (searchQuery.isNotBlank()) "No Jobs Found" else "No Jobs Added Yet",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (searchQuery.isNotBlank()) {
                "No jobs match '$searchQuery'. Try clearing the search or searching a different term."
              } else {
                "Tap the '+ New Job' button below to add your first printing job."
              },
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
            if (searchQuery.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              TextButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                Text("Clear Search")
              }
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("jobs_lazy_column"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(
            items = filteredJobs,
            key = { it.id }
          ) { job ->
            JobCard(
              job = job,
              onCardClick = { viewModel.selectJobForDetail(job) },
              onToggleComplete = { viewModel.toggleJobCompletion(job) },
              onToggleDie = { viewModel.toggleDieStatus(job) },
              onToggleEmboss = { viewModel.toggleEmbossStatus(job) },
              onEditClick = { viewModel.openEditJobDialog(job) },
              onDeleteClick = { jobPendingDelete = job }
            )
          }
        }
      }
    }
  }

  // Add / Edit Job Dialog
  if (showAddEditSheet) {
    AddEditJobDialog(
      initialJob = editingJob,
      onDismiss = { viewModel.closeAddEditSheet() },
      onSave = { jobNumber, clientName, quantity, jobArrivedDate, dieType, isDieArrived, dieArrivedDate, embossType, isEmbossArrived, embossArrivedDate, isCompleted, paperDetails, notes, isUrgent ->
        viewModel.saveJob(
          jobNumber = jobNumber,
          clientName = clientName,
          quantity = quantity,
          jobArrivedDate = jobArrivedDate,
          dieType = dieType,
          isDieArrived = isDieArrived,
          dieArrivedDate = dieArrivedDate,
          embossType = embossType,
          isEmbossArrived = isEmbossArrived,
          embossArrivedDate = embossArrivedDate,
          isCompleted = isCompleted,
          paperDetails = paperDetails,
          notes = notes,
          isUrgent = isUrgent
        )
      }
    )
  }

  // Job Details Dialog
  selectedDetailJob?.let { job ->
    JobDetailDialog(
      job = job,
      onDismiss = { viewModel.selectJobForDetail(null) },
      onToggleComplete = { viewModel.toggleJobCompletion(job) },
      onToggleDie = { viewModel.toggleDieStatus(job) },
      onToggleEmboss = { viewModel.toggleEmbossStatus(job) },
      onEdit = {
        viewModel.selectJobForDetail(null)
        viewModel.openEditJobDialog(job)
      },
      onDelete = {
        jobPendingDelete = job
        viewModel.selectJobForDetail(null)
      }
    )
  }

  // Delete Confirmation Dialog
  jobPendingDelete?.let { job ->
    AlertDialog(
      onDismissRequest = { jobPendingDelete = null },
      title = { Text("Delete Job #${job.jobNumber}?") },
      text = { Text("Are you sure you want to delete this job record? This action cannot be undone.") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteJob(job)
            jobPendingDelete = null
          }
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { jobPendingDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun StatCard(
  title: String,
  count: Int,
  bgColor: Color,
  textColor: Color
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = count.toString(),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = textColor
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = textColor.copy(alpha = 0.85f)
      )
    }
  }
}
