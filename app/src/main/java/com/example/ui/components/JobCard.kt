package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PrintJobEntity
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusUrgent
import com.example.ui.theme.StatusUrgentContainer
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningContainer
import com.example.util.DateUtils

@Composable
fun JobCard(
  job: PrintJobEntity,
  onCardClick: () -> Unit,
  onToggleComplete: () -> Unit,
  onToggleDie: () -> Unit,
  onToggleEmboss: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val checkButtonBg by animateColorAsState(
    targetValue = if (job.isCompleted) StatusSuccess else MaterialTheme.colorScheme.surfaceVariant,
    label = "checkBg"
  )
  val checkIconColor by animateColorAsState(
    targetValue = if (job.isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
    label = "checkIcon"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onCardClick)
      .testTag("job_card_${job.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (job.isCompleted) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    border = BorderStroke(
      width = if (job.isCompleted) 1.dp else 1.5.dp,
      color = if (job.isCompleted) {
        StatusSuccess.copy(alpha = 0.35f)
      } else if (job.isUrgent) {
        StatusUrgent.copy(alpha = 0.5f)
      } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (job.isCompleted) 1.dp else 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top Row: Job Number Badge, Urgent Badge & Big Completion Tick Checkbox
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Job Number Badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (job.isCompleted) {
              StatusSuccessContainer
            } else {
              MaterialTheme.colorScheme.primaryContainer
            }
          ) {
            Text(
              text = "#${job.jobNumber}",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              ),
              color = if (job.isCompleted) {
                StatusSuccess
              } else {
                MaterialTheme.colorScheme.onPrimaryContainer
              }
            )
          }

          // Urgent badge
          if (job.isUrgent && !job.isCompleted) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = StatusUrgentContainer
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = "Urgent",
                  tint = StatusUrgent,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = "URGENT",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = StatusUrgent
                )
              }
            }
          }

          if (job.isCompleted) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = StatusSuccessContainer
            ) {
              Text(
                text = "DONE",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = StatusSuccess
              )
            }
          }
        }

        // Completion Tick Action Button
        Surface(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onToggleComplete)
            .testTag("tick_complete_button_${job.id}"),
          shape = CircleShape,
          color = checkButtonBg,
          border = if (!job.isCompleted) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
          } else null
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = if (job.isCompleted) "Job Finished" else "Mark Job Finished",
              tint = checkIconColor,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Client / Item Name
      if (job.clientName.isNotBlank()) {
        Text(
          text = job.clientName,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (job.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          ),
          color = if (job.isCompleted) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          } else {
            MaterialTheme.colorScheme.onSurface
          },
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
      }

      // Quantity Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "${DateUtils.formatQuantity(job.quantity)} Pcs / Copies",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3 Milestone Rows: Job Arrived, Die, Emboss
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Row 1: Job Arrived Date
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "Job Arrived:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Text(
              text = DateUtils.formatDate(job.jobArrivedDate),
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Row 2: Die (Repeat vs New with date)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCut,
                contentDescription = null,
                tint = when {
                  job.dieType == "REPEAT" -> MaterialTheme.colorScheme.primary
                  job.isDieArrived -> StatusSuccess
                  job.dieType == "NEW" -> StatusWarning
                  else -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "Die:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            when (job.dieType) {
              "REPEAT" -> {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = "Repeat (Available)",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
              "NEW" -> {
                if (job.isDieArrived) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusSuccessContainer
                  ) {
                    Text(
                      text = "New (Arrived: ${DateUtils.formatDate(job.dieArrivedDate)})",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = StatusSuccess
                    )
                  }
                } else {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusWarningContainer,
                    modifier = Modifier.clickable(onClick = onToggleDie)
                  ) {
                    Text(
                      text = "New (Awaited - Tap Arrived)",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = StatusWarning
                    )
                  }
                }
              }
              else -> {
                Text(
                  text = "Not Required",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Row 3: Emboss (Repeat vs New with date)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(14.dp)
                  .background(
                    when {
                      job.embossType == "REPEAT" -> MaterialTheme.colorScheme.primary
                      job.isEmbossArrived -> StatusSuccess
                      job.embossType == "NEW" -> StatusWarning
                      else -> MaterialTheme.colorScheme.outline
                    },
                    RoundedCornerShape(3.dp)
                  )
              )
              Text(
                text = "Emboss:",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            when (job.embossType) {
              "REPEAT" -> {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = "Repeat (Available)",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
              "NEW" -> {
                if (job.isEmbossArrived) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusSuccessContainer
                  ) {
                    Text(
                      text = "New (Arrived: ${DateUtils.formatDate(job.embossArrivedDate)})",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = StatusSuccess
                    )
                  }
                } else {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusWarningContainer,
                    modifier = Modifier.clickable(onClick = onToggleEmboss)
                  ) {
                    Text(
                      text = "New (Awaited - Tap Arrived)",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = StatusWarning
                    )
                  }
                }
              }
              else -> {
                Text(
                  text = "Not Required",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Notes preview if present
      if (job.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Note: ${job.notes}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Footer Row: Completion Date (if completed) and Edit/Delete action buttons
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (job.isCompleted && job.completedDate != null) {
          Text(
            text = "Completed: ${DateUtils.formatDateTime(job.completedDate)}",
            style = MaterialTheme.typography.labelSmall,
            color = StatusSuccess
          )
        } else {
          Text(
            text = "Tap card to view full details",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onEditClick,
            modifier = Modifier
              .size(36.dp)
              .testTag("edit_job_button_${job.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Job",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
              .size(36.dp)
              .testTag("delete_job_button_${job.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete Job",
              tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
