package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoiceDialog(
  order: Order,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
  val invoiceDate = sdf.format(Date(order.placedTimestamp))

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .padding(8.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Verified,
              contentDescription = "Verified GST Tax Invoice",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "TAX INVOICE / BILL OF SUPPLY",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          item {
            // Seller & Buyer details card
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "SOLD BY / DISPATCHED FROM:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "GlowMatrix E-Commerce Logistics Ltd.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "Central Fulfillment Hub, Sector 18, MIDC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "GSTIN: 27AABCG9812M1ZM",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "FSSAI / FDA Reg: COS-MH-981240",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                  Text(
                    text = "INVOICE DETAILS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "INV-${order.orderNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "Date: $invoiceDate",
                    style = MaterialTheme.typography.bodySmall
                  )
                  Text(
                    text = "AWB: ${order.awbTrackingNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "Payment: ${order.paymentMethod.name}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bill To
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "BILL TO / DELIVER TO:",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = order.customerName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${order.shippingAddress.street}, ${order.shippingAddress.city}, ${order.shippingAddress.state} - ${order.shippingAddress.pincode}",
                  style = MaterialTheme.typography.bodySmall
                )
                Text(
                  text = "Contact: ${order.customerPhone}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "ITEMIZED DERMATOLOGY LINE ITEMS",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
          }

          items(order.items) { item ->
            val itemPrice = item.product.effectivePrice * item.selectedVariant.priceMultiplier * item.quantity
            val itemCgst = itemPrice * 0.09
            val itemSgst = itemPrice * 0.09
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.product.title,
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Variant: ${item.selectedVariant.name} | Batch: ${item.product.batchInfo.batchNumber}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "HSN: ${item.product.hsnCode} | GST Rate: 18% (9% CGST + 9% SGST)",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "₹${String.format(Locale.US, "%.2f", itemPrice)}",
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Qty: ${item.quantity}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Totals breakdown
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal (Taxable Value):", style = MaterialTheme.typography.bodySmall)
                Text("₹${String.format(Locale.US, "%.2f", order.subtotal)}", style = MaterialTheme.typography.bodySmall)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Central GST (CGST 9%):", style = MaterialTheme.typography.bodySmall)
                Text("₹${String.format(Locale.US, "%.2f", order.taxGst / 2)}", style = MaterialTheme.typography.bodySmall)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("State GST (SGST 9%):", style = MaterialTheme.typography.bodySmall)
                Text("₹${String.format(Locale.US, "%.2f", order.taxGst / 2)}", style = MaterialTheme.typography.bodySmall)
              }
              if (order.shippingFee > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Shipping & Handling:", style = MaterialTheme.typography.bodySmall)
                  Text("₹${String.format(Locale.US, "%.2f", order.shippingFee)}", style = MaterialTheme.typography.bodySmall)
                }
              }
              if (order.discountAmount > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Promotional Discount (${order.appliedCoupon ?: ""}):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                  Text("-₹${String.format(Locale.US, "%.2f", order.discountAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("GRAND TOTAL (INR):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                  "₹${String.format(Locale.US, "%.2f", order.totalAmount)}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Digital signature declaration
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Verified,
                  contentDescription = "Signature",
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Digitally signed by GlowMatrix Authorized Signatory",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "This is a computer-generated tax invoice compliant with Indian GST Law 2017.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = {
              Toast.makeText(context, "Invoice #${order.orderNumber} shared to email & WhatsApp", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Print / Share")
          }

          Button(
            onClick = {
              Toast.makeText(context, "PDF Invoice downloaded to device downloads folder", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Download PDF")
          }
        }
      }
    }
  }
}
