package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PrintJobEntity
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusUrgent
import com.example.ui.theme.StatusUrgentContainer
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningContainer
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailDialog(
  job: PrintJobEntity,
  onDismiss: () -> Unit,
  onToggleComplete: () -> Unit,
  onToggleDie: () -> Unit,
  onToggleEmboss: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  fun shareJobDetails() {
    val text = buildString {
      appendLine("📋 *HIGH 5 PACKAGING - JOB DETAILS*")
      appendLine("• *Job Number:* #${job.jobNumber}")
      if (job.clientName.isNotBlank()) appendLine("• *Client/Item:* ${job.clientName}")
      appendLine("• *Quantity:* ${DateUtils.formatQuantity(job.quantity)} pcs")
      appendLine("• *Status:* ${if (job.isCompleted) "✅ COMPLETED" else "⏳ PENDING"}")
      appendLine("• *Job Arrived:* ${DateUtils.formatDate(job.jobArrivedDate)}")
      val dieText = when (job.dieType) {
        "REPEAT" -> "Repeat Die (Available in Press)"
        "NEW" -> if (job.isDieArrived) "New Die - Arrived (${DateUtils.formatDate(job.dieArrivedDate)})" else "New Die - Awaited ⏳"
        else -> "Not Required"
      }
      appendLine("• *Die:* $dieText")
      val embossText = when (job.embossType) {
        "REPEAT" -> "Repeat Emboss (Available in Press)"
        "NEW" -> if (job.isEmbossArrived) "New Emboss - Arrived (${DateUtils.formatDate(job.embossArrivedDate)})" else "New Emboss - Awaited ⏳"
        else -> "Not Required"
      }
      appendLine("• *Emboss:* $embossText")
      if (job.paperDetails.isNotBlank()) appendLine("• *Paper:* ${job.paperDetails}")
      if (job.notes.isNotBlank()) appendLine("• *Notes:* ${job.notes}")
    }
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Job #${job.jobNumber}")
    context.startActivity(shareIntent)
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    modifier = Modifier
      .fillMaxWidth()
      .imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 24.dp)
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (job.isCompleted) StatusSuccessContainer else MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = "#${job.jobNumber}",
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = if (job.isCompleted) StatusSuccess else MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
          if (job.isUrgent) {
            Surface(shape = RoundedCornerShape(8.dp), color = StatusUrgentContainer) {
              Text(
                text = "URGENT",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = StatusUrgent
              )
            }
          }
        }

        Row {
          IconButton(onClick = { shareJobDetails() }) {
            Icon(Icons.Default.Share, contentDescription = "Share Job", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Big Completion Status Banner with Clickable Tick
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (job.isCompleted) StatusSuccessContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onToggleComplete)
          .testTag("detail_toggle_complete")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (job.isCompleted) "Job Finished (Completed) ✓" else "Job In-Progress (Pending)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = if (job.isCompleted) StatusSuccess else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (job.isCompleted) {
                "Finished on: ${DateUtils.formatDateTime(job.completedDate)}"
              } else {
                "Tap here to mark job as finished"
              },
              style = MaterialTheme.typography.bodySmall,
              color = if (job.isCompleted) StatusSuccess.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = if (job.isCompleted) StatusSuccess else MaterialTheme.colorScheme.surface,
            border = if (!job.isCompleted) BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Status",
                tint = if (job.isCompleted) Color.White else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Client & Quantity Information
      if (job.clientName.isNotBlank()) {
        Text(
          text = job.clientName,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
              Text("Quantity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "${DateUtils.formatQuantity(job.quantity)} pcs",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        if (job.paperDetails.isNotBlank()) {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Paper / Board", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                job.paperDetails,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Key Milestones & Tracking
      Text(
        text = "Key Milestones & Tracking",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      // 1. Job Arrived Date
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
              Text("Job Arrived Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(DateUtils.formatDate(job.jobArrivedDate), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
          }
          Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Text("Recorded", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Die Status Card (Repeat vs New)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(enabled = job.dieType == "NEW", onClick = onToggleDie)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
              Icons.Default.ContentCut,
              contentDescription = null,
              tint = when {
                job.dieType == "REPEAT" -> MaterialTheme.colorScheme.primary
                job.isDieArrived -> StatusSuccess
                job.dieType == "NEW" -> StatusWarning
                else -> MaterialTheme.colorScheme.outline
              }
            )
            Column {
              Text(
                text = "Die Status (${if (job.dieType == "REPEAT") "Repeat Die" else if (job.dieType == "NEW") "New Die" else "None"})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              when (job.dieType) {
                "REPEAT" -> {
                  Text(
                    text = "Repeat Die (Already available - No date needed)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                  )
                }
                "NEW" -> {
                  Text(
                    text = if (job.isDieArrived) "Arrived: ${DateUtils.formatDate(job.dieArrivedDate)}" else "Awaited (Tap to Mark Arrived)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (job.isDieArrived) StatusSuccess else StatusWarning
                    )
                  )
                }
                else -> {
                  Text(text = "Die Not Required", style = MaterialTheme.typography.bodyMedium)
                }
              }
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = when {
              job.dieType == "REPEAT" -> MaterialTheme.colorScheme.primaryContainer
              job.isDieArrived -> StatusSuccessContainer
              job.dieType == "NEW" -> StatusWarningContainer
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          ) {
            Text(
              text = when {
                job.dieType == "REPEAT" -> "Ready ✓"
                job.isDieArrived -> "Arrived ✓"
                job.dieType == "NEW" -> "Awaited ⏳"
                else -> "N/A"
              },
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = when {
                job.dieType == "REPEAT" -> MaterialTheme.colorScheme.onPrimaryContainer
                job.isDieArrived -> StatusSuccess
                job.dieType == "NEW" -> StatusWarning
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Emboss Status Card (Repeat vs New)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(enabled = job.embossType == "NEW", onClick = onToggleEmboss)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .background(
                  when {
                    job.embossType == "REPEAT" -> MaterialTheme.colorScheme.primary
                    job.isEmbossArrived -> StatusSuccess
                    job.embossType == "NEW" -> StatusWarning
                    else -> MaterialTheme.colorScheme.outline
                  },
                  RoundedCornerShape(4.dp)
                )
            )
            Column {
              Text(
                text = "Emboss Status (${if (job.embossType == "REPEAT") "Repeat Emboss" else if (job.embossType == "NEW") "New Emboss" else "None"})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              when (job.embossType) {
                "REPEAT" -> {
                  Text(
                    text = "Repeat Emboss (Already available - No date needed)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                  )
                }
                "NEW" -> {
                  Text(
                    text = if (job.isEmbossArrived) "Arrived: ${DateUtils.formatDate(job.embossArrivedDate)}" else "Awaited (Tap to Mark Arrived)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (job.isEmbossArrived) StatusSuccess else StatusWarning
                    )
                  )
                }
                else -> {
                  Text(text = "Emboss Not Required", style = MaterialTheme.typography.bodyMedium)
                }
              }
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = when {
              job.embossType == "REPEAT" -> MaterialTheme.colorScheme.primaryContainer
              job.isEmbossArrived -> StatusSuccessContainer
              job.embossType == "NEW" -> StatusWarningContainer
              else -> MaterialTheme.colorScheme.surfaceVariant
            }
          ) {
            Text(
              text = when {
                job.embossType == "REPEAT" -> "Ready ✓"
                job.isEmbossArrived -> "Arrived ✓"
                job.embossType == "NEW" -> "Awaited ⏳"
                else -> "N/A"
              },
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = when {
                job.embossType == "REPEAT" -> MaterialTheme.colorScheme.onPrimaryContainer
                job.isEmbossArrived -> StatusSuccess
                job.embossType == "NEW" -> StatusWarning
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              }
            )
          }
        }
      }

      if (job.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
              Text("Notes / Remarks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(job.notes, style = MaterialTheme.typography.bodyMedium)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onEdit,
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Edit")
        }

        Button(
          onClick = onDelete,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Delete")
        }
      }
    }
  }
}
