package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.PrintJobEntity
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningContainer
import com.example.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobDialog(
  initialJob: PrintJobEntity?,
  onDismiss: () -> Unit,
  onSave: (
    jobNumber: String,
    clientName: String,
    quantity: Int,
    jobArrivedDate: Long,
    dieType: String,
    isDieArrived: Boolean,
    dieArrivedDate: Long?,
    embossType: String,
    isEmbossArrived: Boolean,
    embossArrivedDate: Long?,
    isCompleted: Boolean,
    paperDetails: String,
    notes: String,
    isUrgent: Boolean
  ) -> Unit
) {
  val context = LocalContext.current
  val isEditing = initialJob != null
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var jobNumber by remember { mutableStateOf(initialJob?.jobNumber ?: "") }
  var jobNumberError by remember { mutableStateOf<String?>(null) }
  var clientName by remember { mutableStateOf(initialJob?.clientName ?: "") }
  var quantityText by remember { mutableStateOf(initialJob?.quantity?.toString() ?: "1000") }
  var jobArrivedDate by remember { mutableLongStateOf(initialJob?.jobArrivedDate ?: System.currentTimeMillis()) }

  // Die Options: "REPEAT", "NEW", "NOT_REQUIRED"
  var dieType by remember { mutableStateOf(initialJob?.dieType ?: "REPEAT") }
  var isDieArrived by remember { mutableStateOf(initialJob?.isDieArrived ?: false) }
  var dieArrivedDate by remember { mutableLongStateOf(initialJob?.dieArrivedDate ?: System.currentTimeMillis()) }

  // Emboss Options: "REPEAT", "NEW", "NOT_REQUIRED"
  var embossType by remember { mutableStateOf(initialJob?.embossType ?: "REPEAT") }
  var isEmbossArrived by remember { mutableStateOf(initialJob?.isEmbossArrived ?: false) }
  var embossArrivedDate by remember { mutableLongStateOf(initialJob?.embossArrivedDate ?: System.currentTimeMillis()) }

  var isCompleted by remember { mutableStateOf(initialJob?.isCompleted ?: false) }
  var isUrgent by remember { mutableStateOf(initialJob?.isUrgent ?: false) }
  var paperDetails by remember { mutableStateOf(initialJob?.paperDetails ?: "") }
  var notes by remember { mutableStateOf(initialJob?.notes ?: "") }

  fun showDatePicker(currentDateMillis: Long, onDateSelected: (Long) -> Unit) {
    val cal = Calendar.getInstance().apply { timeInMillis = currentDateMillis }
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val selected = Calendar.getInstance().apply {
          set(Calendar.YEAR, year)
          set(Calendar.MONTH, month)
          set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        onDateSelected(selected.timeInMillis)
      },
      cal.get(Calendar.YEAR),
      cal.get(Calendar.MONTH),
      cal.get(Calendar.DAY_OF_MONTH)
    ).show()
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
        Column {
          Text(
            text = if (isEditing) "Edit Print Job" else "New Printing Job",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (isEditing) "Job #${initialJob?.jobNumber}" else "Enter job details",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Job Number Field
      OutlinedTextField(
        value = jobNumber,
        onValueChange = {
          jobNumber = it
          if (it.isNotBlank()) jobNumberError = null
        },
        label = { Text("Job Number *") },
        placeholder = { Text("e.g. 1045 or JOB-202") },
        isError = jobNumberError != null,
        supportingText = {
          if (jobNumberError != null) {
            Text(jobNumberError ?: "", color = MaterialTheme.colorScheme.error)
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_job_number")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Client / Job Description
      OutlinedTextField(
        value = clientName,
        onValueChange = { clientName = it },
        label = { Text("Client / Item Description") },
        placeholder = { Text("e.g. Sweet Box 1kg, Tea Carton, Flyer") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_client_name")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Quantity Field
      OutlinedTextField(
        value = quantityText,
        onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
        label = { Text("Job Quantity (Pcs / Sheets)") },
        placeholder = { Text("e.g. 5000") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_job_quantity")
      )

      // Quick quantity boost chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(1000, 2500, 5000, 10000).forEach { qty ->
          FilterChip(
            selected = quantityText == qty.toString(),
            onClick = { quantityText = qty.toString() },
            label = { Text("+${DateUtils.formatQuantity(qty)}") }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. Job Arrived Date Picker
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker(jobArrivedDate) { jobArrivedDate = it } }
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Job Arrived Date",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = DateUtils.formatDate(jobArrivedDate),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Pick Date",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. DIE SECTION: Repeat vs New vs None
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Die Option",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Chips for Repeat / New / Not Required
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = dieType == "REPEAT",
              onClick = { dieType = "REPEAT" },
              label = { Text("Repeat Die") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )

            FilterChip(
              selected = dieType == "NEW",
              onClick = { dieType = "NEW" },
              label = { Text("New Die") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = StatusWarningContainer,
                selectedLabelColor = StatusWarning
              )
            )

            FilterChip(
              selected = dieType == "NOT_REQUIRED",
              onClick = { dieType = "NOT_REQUIRED" },
              label = { Text("No Die") }
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          when (dieType) {
            "REPEAT" -> {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = StatusSuccessContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "✓ Repeat Die: Available in press (No date required)",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = StatusSuccess,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
            "NEW" -> {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "New Die Arrived?",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                      text = if (isDieArrived) "Die has arrived" else "Die awaited from vendor",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = isDieArrived,
                    onCheckedChange = { isDieArrived = it }
                  )
                }

                // If arrived or selecting arrival date
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(dieArrivedDate) { dieArrivedDate = it } }
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = if (isDieArrived) "Die Arrived Date" else "Expected / Arrival Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = DateUtils.formatDate(dieArrivedDate),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.CalendarToday,
                      contentDescription = "Pick Date",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
            "NOT_REQUIRED" -> {
              Text(
                text = "Die cutting is not required for this job.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. EMBOSS SECTION: Repeat vs New vs None
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Emboss Option",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Chips for Repeat / New / Not Required
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = embossType == "REPEAT",
              onClick = { embossType = "REPEAT" },
              label = { Text("Repeat Emboss") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )

            FilterChip(
              selected = embossType == "NEW",
              onClick = { embossType = "NEW" },
              label = { Text("New Emboss") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = StatusWarningContainer,
                selectedLabelColor = StatusWarning
              )
            )

            FilterChip(
              selected = embossType == "NOT_REQUIRED",
              onClick = { embossType = "NOT_REQUIRED" },
              label = { Text("No Emboss") }
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          when (embossType) {
            "REPEAT" -> {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = StatusSuccessContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "✓ Repeat Emboss: Available in press (No date required)",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = StatusSuccess,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
            "NEW" -> {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "New Emboss Block Arrived?",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                      text = if (isEmbossArrived) "Block has arrived" else "Block awaited from vendor",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = isEmbossArrived,
                    onCheckedChange = { isEmbossArrived = it }
                  )
                }

                // If arrived or selecting arrival date
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(embossArrivedDate) { embossArrivedDate = it } }
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = if (isEmbossArrived) "Emboss Arrived Date" else "Expected / Arrival Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = DateUtils.formatDate(embossArrivedDate),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.CalendarToday,
                      contentDescription = "Pick Date",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
            "NOT_REQUIRED" -> {
              Text(
                text = "Embossing is not required for this job.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Job Completion Status Switch
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Job Completed (Tick)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = if (isCompleted) "Marked as Finished ✓" else "Marked as In-Progress ⏳",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = isCompleted,
            onCheckedChange = { isCompleted = it }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Urgent flag
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isUrgent = !isUrgent }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Mark as High Priority / Urgent Job",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        Switch(
          checked = isUrgent,
          onCheckedChange = { isUrgent = it }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Paper / Board info
      OutlinedTextField(
        value = paperDetails,
        onValueChange = { paperDetails = it },
        label = { Text("Paper / Board Specs") },
        placeholder = { Text("e.g. 350 GSM Art Card, ITC Sapphire") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Notes / Instructions
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text("Special Notes / Instructions") },
        placeholder = { Text("e.g. Spot UV, Gloss Lamination, Delivery urgency...") },
        maxLines = 3,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.weight(1f)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            if (jobNumber.isBlank()) {
              jobNumberError = "Job Number cannot be empty"
              return@Button
            }
            val parsedQty = quantityText.toIntOrNull() ?: 0
            onSave(
              jobNumber,
              clientName,
              parsedQty,
              jobArrivedDate,
              dieType,
              isDieArrived,
              dieArrivedDate,
              embossType,
              isEmbossArrived,
              embossArrivedDate,
              isCompleted,
              paperDetails,
              notes,
              isUrgent
            )
          },
          modifier = Modifier
            .weight(1f)
            .testTag("save_job_button")
        ) {
          Text("Save Job")
        }
      }
    }
  }
}
