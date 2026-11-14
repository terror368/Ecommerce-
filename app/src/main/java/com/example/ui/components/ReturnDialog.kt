package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order

@Composable
fun ReturnDialog(
  order: Order,
  onConfirmReturn: (orderId: String, itemTitle: String, reason: String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val reasons = listOf(
    "Dermatological / allergic skin reaction",
    "Damaged packaging / seal broken upon delivery",
    "Wrong variant or expired batch received",
    "Defective pump dispenser / leak detected"
  )
  var selectedReason by remember { mutableStateOf(reasons.first()) }
  var hasUploadedPhoto by remember { mutableStateOf(false) }
  var selectedPickupSlot by remember { mutableStateOf("Tomorrow (10 AM - 2 PM)") }
  var refundToStoreCredit by remember { mutableStateOf(true) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Initiate Return / Exchange",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Order #${order.orderNumber}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Text(
          text = "Select Reason for Return:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )

        reasons.forEach { reason ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedReason = reason }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = selectedReason == reason,
              onClick = { selectedReason = reason }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = reason, style = MaterialTheme.typography.bodySmall)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Upload photo evidence box
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              hasUploadedPhoto = true
              Toast.makeText(context, "Proof image attached: bottle_inspection.jpg", Toast.LENGTH_SHORT).show()
            },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (hasUploadedPhoto) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.AddPhotoAlternate,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (hasUploadedPhoto) "✓ Evidence Attached: bottle_inspection.jpg" else "Attach Photo Proof (Clinical/Defect)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Required for quality assurance & expedited approval",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Refund preference
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { refundToStoreCredit = !refundToStoreCredit },
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = refundToStoreCredit,
            onCheckedChange = { refundToStoreCredit = it }
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "Instant GlowWallet Credit (+5% Extra Bonus)",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Settled instantly upon courier pickup scan",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            val itemTitle = order.items.firstOrNull()?.product?.title ?: "Skincare Formulation"
            onConfirmReturn(order.id, itemTitle, selectedReason)
            Toast.makeText(context, "Return pickup scheduled for tomorrow!", Toast.LENGTH_LONG).show()
            onDismiss()
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Schedule Doorstep Pickup")
        }
      }
    }
  }
}
