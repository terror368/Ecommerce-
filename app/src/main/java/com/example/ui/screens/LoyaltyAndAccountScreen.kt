package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.ReturnRequest
import com.example.ui.SkincareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyAndAccountScreen(
  viewModel: SkincareViewModel,
  onOpenOrder: (Order) -> Unit
) {
  val context = LocalContext.current
  val loyaltyPoints by viewModel.repository.loyaltyPoints.collectAsState()
  val orders by viewModel.repository.orders.collectAsState()
  val returnRequests by viewModel.repository.returnRequests.collectAsState()
  val savedAddresses by viewModel.repository.savedAddresses.collectAsState()

  var consentPersonalizedOffers by remember { mutableStateOf(true) }
  var consentClinicalTelemetry by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // User Profile Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text("Aditi Sharma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("aditi.sharma@example.com • +91 98201 44521", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Text(
                text = "SILVER VIP MEMBER",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // GlowRewards Wallet Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D19))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
              Spacer(modifier = Modifier.width(8.dp))
              Text("GlowRewards Balance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("₹${(loyaltyPoints * 0.5).toInt()} Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "$loyaltyPoints Points",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary
          )

          Text(
            text = "Earn 5% points on every clinical order. Redeem at 1-click checkout.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          LinearProgressIndicator(
            progress = { 0.65f },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.secondary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "580 points to Gold VIP (Unlocks FREE Same-Day Shipping)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Referral Program Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Invite Friends & Earn ₹200", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("Share code GLOW-982. Friend gets 20% off and you get ₹200 wallet credits.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          OutlinedButton(
            onClick = {
              Toast.makeText(context, "Referral code GLOW-982 copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Share")
          }
        }
      }
    }

    // Order History Section
    item {
      Text("Order History & Invoices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }

    items(orders) { order ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenOrder(order) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Order #${order.orderNumber}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = order.milestone.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text("${order.items.size} item(s) • Total: ₹${order.totalAmount.toInt()}", style = MaterialTheme.typography.bodySmall)
          Text("AWB: ${order.awbTrackingNumber}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onOpenOrder(order) },
              modifier = Modifier.weight(1f)
            ) {
              Text("Track Courier", style = MaterialTheme.typography.labelSmall)
            }

            OutlinedButton(
              onClick = { viewModel.openInvoice(order) },
              modifier = Modifier.weight(1f)
            ) {
              Text("View Invoice", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    }

    // Return & Refund Status Pipeline
    if (returnRequests.isNotEmpty()) {
      item {
        Text("Active Return & Refund Pipelines", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }

      items(returnRequests) { ret ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(ret.id, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              Text(ret.status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Text(ret.itemTitle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("Reason: ${ret.reason}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Refund Value: ₹${ret.refundAmount.toInt()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
          }
        }
      }
    }

    // DPDP & Privacy Consent Manager
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("DPDP Act 2023 & GDPR Privacy Preferences", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Personalized Active Recommendations", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
              Text("Allows AI quiz to remember skin concerns", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = consentPersonalizedOffers, onCheckedChange = { consentPersonalizedOffers = it })
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Anonymous Clinical Telemetry", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
              Text("Share formulation feedback with lab chemists", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = consentClinicalTelemetry, onCheckedChange = { consentClinicalTelemetry = it })
          }
        }
      }
    }
  }
}
