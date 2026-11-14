package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.*
import com.example.ui.SkincareViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAndOpsScreen(
  viewModel: SkincareViewModel,
  onOpenOrder: (Order) -> Unit
) {
  val context = LocalContext.current
  val currentRole by viewModel.repository.currentRole.collectAsState()
  val products by viewModel.repository.products.collectAsState()
  val orders by viewModel.repository.orders.collectAsState()
  val returnRequests by viewModel.repository.returnRequests.collectAsState()
  val supportTickets by viewModel.repository.supportTickets.collectAsState()
  val auditLogs by viewModel.repository.auditLogs.collectAsState()

  // Tabs within Admin & Ops Console
  val tabs = listOf("Executive GMV", "Warehouse Hub", "Seller Catalog", "Delivery Partner", "Support Desk", "Audit Logs")
  var selectedTab by remember { mutableStateOf(0) }

  // Delivery Boy OTP Verification dialog state
  var verifyingOrder by remember { mutableStateOf<Order?>(null) }
  var otpInput by remember { mutableStateOf("") }

  // Executive Metrics
  val totalGmv = orders.sumOf { it.totalAmount }
  val totalOrdersCount = orders.size
  val avgOrderValue = if (totalOrdersCount > 0) totalGmv / totalOrdersCount else 0.0
  val returnRate = if (totalOrdersCount > 0) (returnRequests.size.toDouble() / totalOrdersCount * 100) else 0.0

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Console Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 4.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Enterprise Operations Center",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(currentRole.badgeColor))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Viewing as: ${currentRole.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(onClick = { viewModel.toggleRoleSwitcher(true) }) {
            Icon(Icons.Default.SwitchAccount, contentDescription = "Switch Persona")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          edgePadding = 0.dp,
          divider = {}
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = { Text(title, style = MaterialTheme.typography.labelMedium) }
            )
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // TAB 0: Executive GMV & Analytics Dashboard
          item {
            Text(
              text = "Real-Time Gross Merchandise Metrics",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AnalyticsKpiCard(
                title = "Total GMV Revenue",
                value = "₹${String.format(Locale.US, "%.0f", totalGmv)}",
                subtitle = "+24.8% vs last month",
                modifier = Modifier.weight(1f)
              )
              AnalyticsKpiCard(
                title = "Fulfillment Orders",
                value = "$totalOrdersCount",
                subtitle = "99.4% on-time dispatch",
                modifier = Modifier.weight(1f)
              )
            }
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AnalyticsKpiCard(
                title = "Average Order Value",
                value = "₹${String.format(Locale.US, "%.0f", avgOrderValue)}",
                subtitle = "Top tier skincare cart",
                modifier = Modifier.weight(1f)
              )
              AnalyticsKpiCard(
                title = "Return / Dispute Rate",
                value = "${String.format(Locale.US, "%.1f", returnRate)}%",
                subtitle = "Under 3.0% SLA threshold",
                modifier = Modifier.weight(1f)
              )
            }
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Top-Selling Clinical Formulations",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                products.take(3).forEach { prod ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(prod.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                      Text("Total Units in Warehouses: ${prod.totalStock}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                      text = "₹${prod.price.toInt()}",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }
            }
          }
        }

        1 -> {
          // TAB 1: Operations & Multi-Warehouse Hub
          item {
            Text(
              text = "Multi-Warehouse Stock & Batch Expiry Control",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          items(products) { product ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(product.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("Batch: ${product.batchInfo.batchNumber} | Expiry: ${product.batchInfo.expiryDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  if (product.batchInfo.isNearExpiry) {
                    Surface(
                      color = Color(0xFFFFECEE),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "NEAR EXPIRY ALERT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE04F5F),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Multi-Warehouse distribution row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  product.warehouseStock.forEach { (wh, count) ->
                    Surface(
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(8.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                      Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(wh.replace("WH-", ""), style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        Text("$count units", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      viewModel.repository.updateStock(product.id, "WH-Mumbai Central", (product.warehouseStock["WH-Mumbai Central"] ?: 0) + 50)
                      Toast.makeText(context, "Stock replenished +50 in Mumbai Hub", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("+50 Stock", style = MaterialTheme.typography.labelSmall)
                  }

                  Button(
                    onClick = {
                      viewModel.repository.triggerBatchDiscount(product.id, 30)
                      Toast.makeText(context, "Near-expiry 30% markdown triggered for SKU", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Auto-Discount 30%", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }

        2 -> {
          // TAB 2: Seller & Vendor Catalog Management
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Brand SKU Registry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              Button(
                onClick = {
                  val newSku = SkincareProduct(
                    id = "PROD-${System.currentTimeMillis().toString().takeLast(4)}",
                    title = "Vitamin C 15% Ethyl Ascorbic Glow Serum",
                    brand = "Luminia Laboratories",
                    category = SkincareCategory.SERUMS,
                    skinTypes = listOf(SkinType.ALL),
                    skinConcerns = listOf(SkinConcern.DULLNESS, SkinConcern.HYPERPIGMENTATION),
                    price = 799.0,
                    originalPrice = 1099.0,
                    rating = 4.9,
                    reviewCount = 1,
                    volume = "30 ml / 1.0 fl. oz.",
                    dermatologyIngredients = listOf(
                      IngredientDetail("Ethyl Ascorbic Acid (Vitamin C)", "15.0%", "Inhibits tyrosinase for crystal clear glow"),
                      IngredientDetail("Ferulic Acid", "0.5%", "Stabilizes and doubles antioxidant photoprotection")
                    ),
                    fullIngredientsText = "Aqua, 3-O-Ethyl Ascorbic Acid, Ferulic Acid, Propanediol, Sodium Hyaluronate.",
                    usageInstructions = "Apply 2-3 drops morning after cleansing. Follow with broad-spectrum SPF 50.",
                    patchTestWarning = "Formulated at stable pH 4.0. Patch test recommended.",
                    batchInfo = BatchInfo("BATCH-LUM-2026-VC15", "03/2026", "03/2028", 24, false, 0),
                    warehouseStock = mapOf("WH-Mumbai Central" to 50, "WH-Delhi NCR" to 50, "WH-Bengaluru South" to 50),
                    variants = listOf(ProductVariant("V1", "Amber Dropper 30ml", 30, 1.0)),
                    description = "Clinically stabilized Vitamin C active serum."
                  )
                  viewModel.repository.addNewProduct(newSku)
                  Toast.makeText(context, "New SKU published to catalog!", Toast.LENGTH_SHORT).show()
                }
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New SKU")
              }
            }
          }

          items(products) { prod ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(prod.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                  Text("Brand: ${prod.brand} • HSN Code: ${prod.hsnCode}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("Total Inventory: ${prod.totalStock} units available", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                Text("₹${prod.price.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        3 -> {
          // TAB 3: Delivery Partner Portal (Doorstep Executive View)
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "Glow Logistics Executive Dashboard",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                  text = "Assigned Delivery Fleet: Ramesh Kumar (Van #MH-02-CB-9102)",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
              }
            }
          }

          item {
            Text("Assigned Doorstep Deliveries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          items(orders) { order ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("Order #${order.orderNumber}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodySmall)
                    Text("Address: ${order.shippingAddress.street}, ${order.shippingAddress.city}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = order.milestone.title,
                      style = MaterialTheme.typography.labelSmall,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Payment Type:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                      text = "${order.paymentMethod.name} • ₹${order.totalAmount.toInt()}",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = if (order.paymentMethod == PaymentMethod.COD && !order.codCashCollected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                  }

                  if (order.milestone != DeliveryMilestone.DELIVERED) {
                    Button(
                      onClick = {
                        verifyingOrder = order
                        otpInput = ""
                      }
                    ) {
                      Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Verify OTP & Deliver")
                    }
                  } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E9A60))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Completed", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E9A60), fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }

        4 -> {
          // TAB 4: Customer Support Ticketing & Refund Overrides
          item {
            Text("Customer Care & Clinical Dispute Tickets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          items(supportTickets) { ticket ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(ticket.subject, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("From: ${ticket.customerEmail} • Category: ${ticket.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  Surface(
                    color = if (ticket.status == "OPEN") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = ticket.status,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (ticket.status == "OPEN") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(ticket.lastMessage, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      viewModel.repository.overrideRefundAsStoreCredit("ORD-98210", 699.0)
                      Toast.makeText(context, "Issued ₹699 Instant GlowWallet Credit to customer", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Issue Store Credit", style = MaterialTheme.typography.labelSmall)
                  }

                  Button(
                    onClick = {
                      viewModel.repository.resolveSupportTicket(ticket.id, "Customer inquiry resolved by clinical support.")
                      Toast.makeText(context, "Ticket marked as resolved", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Mark Resolved", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }

        5 -> {
          // TAB 5: Audit Logs & DPDP Privacy Compliance
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("DPDP & GDPR Compliance Shield", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("All administrative actions are cryptographically logged with zero-trust immutability.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          items(auditLogs) { log ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(log.action, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                  Text(log.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Actor: ${log.role.displayName} | Entity: ${log.entityType} [${log.entityId}]", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(log.metadata, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }
    }
  }

  // Delivery Boy OTP Verification Dialog
  if (verifyingOrder != null) {
    val order = verifyingOrder!!
    AlertDialog(
      onDismissRequest = { verifyingOrder = null },
      title = { Text("Customer Doorstep Handover Verification") },
      text = {
        Column {
          Text("Order #${order.orderNumber}")
          Text("Customer: ${order.customerName}")
          if (order.paymentMethod == PaymentMethod.COD) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              color = Color(0xFFFFECEE),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "COLLECT CASH: ₹${order.totalAmount.toInt()}",
                color = Color(0xFFE04F5F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Enter 4-Digit Customer OTP (Customer has: ${order.deliveryOtp}):")
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = otpInput,
            onValueChange = { if (it.length <= 4) otpInput = it },
            placeholder = { Text("e.g. ${order.deliveryOtp}") },
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val success = viewModel.repository.verifyDeliveryOtp(order.id, otpInput)
            if (success) {
              Toast.makeText(context, "OTP verified! Delivery marked complete & COD collected.", Toast.LENGTH_LONG).show()
              verifyingOrder = null
            } else {
              Toast.makeText(context, "Invalid OTP code. Please ask customer to check their GlowMatrix app.", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("Verify & Complete Delivery")
        }
      },
      dismissButton = {
        TextButton(onClick = { verifyingOrder = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun AnalyticsKpiCard(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(4.dp))
      Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
      Spacer(modifier = Modifier.height(2.dp))
      Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E9A60))
    }
  }
}
