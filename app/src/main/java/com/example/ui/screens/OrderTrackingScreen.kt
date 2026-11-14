package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryMilestone
import com.example.data.model.Order
import com.example.ui.SkincareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
  viewModel: SkincareViewModel,
  order: Order,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val simProgress by viewModel.deliverySimProgress.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Live Shipment Tracking", style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.openInvoice(order) }) {
            Icon(Icons.Default.Receipt, contentDescription = "Invoice")
          }
        }
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Order ID & Courier Waybill Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Order #${order.orderNumber}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Courier: ${order.courierPartner}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = order.milestone.title.uppercase(),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Air Waybill (AWB):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(order.awbTrackingNumber, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Warehouse Origin:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Mumbai Central Hub", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      // Live Map & Courier Route Canvas Simulation
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A29))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Navigation,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "GPS Route Telemetry",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
              Surface(
                color = Color(0xFF2E9A60),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "LIVE SATELLITE FEED",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas drawing for route map
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF14111C))
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = 60f
                val startY = size.height * 0.75f
                val endX = size.width - 60f
                val endY = size.height * 0.25f

                // Draw route line
                drawLine(
                  color = Color(0x33FFFFFF),
                  start = Offset(startX, startY),
                  end = Offset(endX, endY),
                  strokeWidth = 6f,
                  pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                )

                // Current vehicle animated position
                val currentX = startX + (endX - startX) * simProgress
                val currentY = startY + (endY - startY) * simProgress

                // Progress active line
                drawLine(
                  color = Color(0xFFD4AF37),
                  start = Offset(startX, startY),
                  end = Offset(currentX, currentY),
                  strokeWidth = 6f
                )

                // Origin warehouse node
                drawCircle(color = Color(0xFF8C5346), radius = 16f, center = Offset(startX, startY))
                drawCircle(color = Color.White, radius = 6f, center = Offset(startX, startY))

                // Destination customer node
                drawCircle(color = Color(0xFF2E9A60), radius = 18f, center = Offset(endX, endY))
                drawCircle(color = Color.White, radius = 8f, center = Offset(endX, endY))

                // Vehicle beacon pulse
                drawCircle(color = Color(0x55D4AF37), radius = 26f, center = Offset(currentX, currentY))
                drawCircle(color = Color(0xFFD4AF37), radius = 12f, center = Offset(currentX, currentY))
              }

              // Overlaid markers info
              Text(
                text = "Central Hub (Sector 18)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(8.dp)
              )

              Text(
                text = "${order.shippingAddress.city} Doorstep",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2E9A60),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(8.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Estimated Arrival: Today by 3:45 PM (Van is 2.8 km away)",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
        }
      }

      // Secure OTP & Delivery Executive Contact
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Delivery Security Passcode",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = order.deliveryOtp,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 4.sp
              )
              Text(
                text = "Share this 4-digit OTP only upon inspecting your parcel.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            OutlinedButton(
              onClick = {
                Toast.makeText(context, "Calling executive: ${order.deliveryExecutivePhone}", Toast.LENGTH_SHORT).show()
              }
            ) {
              Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Call Driver")
            }
          }
        }
      }

      // Step-by-Step Delivery Milestones
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Fulfillment Milestones",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            val milestones = listOf(
              DeliveryMilestone.PLACED,
              DeliveryMilestone.PACKED,
              DeliveryMilestone.DISPATCHED,
              DeliveryMilestone.OUT_FOR_DELIVERY,
              DeliveryMilestone.DELIVERED
            )

            milestones.forEachIndexed { index, m ->
              val isCompleted = order.milestone.stepIndex >= m.stepIndex
              val isCurrent = order.milestone == m

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                      if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = m.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = m.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              if (index < milestones.size - 1) {
                Box(
                  modifier = Modifier
                    .padding(start = 13.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(
                      if (order.milestone.stepIndex > m.stepIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
              }
            }
          }
        }
      }

      // Actions Card: Invoice & Return
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = { viewModel.openInvoice(order) },
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tax Invoice")
          }

          Button(
            onClick = { viewModel.openReturnDialog(order) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) {
            Icon(Icons.Default.KeyboardReturn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Return / Dispute")
          }
        }
      }
    }
  }
}
