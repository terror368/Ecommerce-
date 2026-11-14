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
fun CartAndCheckoutScreen(
  viewModel: SkincareViewModel,
  onBack: () -> Unit,
  onNavigateToTracking: (Order) -> Unit
) {
  val context = LocalContext.current
  val cart by viewModel.repository.cart.collectAsState()
  val savedAddresses by viewModel.repository.savedAddresses.collectAsState()
  val selectedAddress by viewModel.repository.selectedAddress.collectAsState()
  val appliedCoupon by viewModel.repository.appliedCoupon.collectAsState()

  var currentCheckoutStep by remember { mutableStateOf(0) } // 0: Cart review, 1: Address & Speed, 2: Payment
  var selectedSpeed by remember { mutableStateOf(DeliverySpeed.EXPRESS) }
  var selectedPayment by remember { mutableStateOf(PaymentMethod.UPI) }
  var couponInput by remember { mutableStateOf("") }
  var placedOrderResult by remember { mutableStateOf<Order?>(null) }
  var codOtpInput by remember { mutableStateOf("") }
  var showCodOtpModal by remember { mutableStateOf(false) }

  val subtotal = cart.sumOf { it.product.effectivePrice * it.selectedVariant.priceMultiplier * it.quantity }
  val freeShippingThreshold = 499.0
  val remainingForFreeShipping = maxOf(0.0, freeShippingThreshold - subtotal)
  val freeShipProgress = if (subtotal >= freeShippingThreshold) 1f else (subtotal / freeShippingThreshold).toFloat()

  val effectiveShippingFee = if (subtotal >= freeShippingThreshold || appliedCoupon?.code == "FREESHIP") 0.0 else selectedSpeed.fee
  val discountAmount = if (appliedCoupon != null && appliedCoupon?.code != "FREESHIP") {
    minOf(subtotal * (appliedCoupon!!.discountPercent / 100.0), appliedCoupon!!.maxDiscountAmount)
  } else 0.0
  val taxGst = subtotal * 0.18
  val grandTotal = subtotal + taxGst + effectiveShippingFee - discountAmount

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = when (currentCheckoutStep) {
              0 -> "Shopping Bag (${cart.sumOf { it.quantity }})"
              1 -> "Select Delivery & Speed"
              2 -> "Payment Gateway"
              else -> "Checkout"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = {
            if (currentCheckoutStep > 0) currentCheckoutStep -= 1 else onBack()
          }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    bottomBar = {
      if (cart.isNotEmpty() && placedOrderResult == null) {
        Surface(
          tonalElevation = 8.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .windowInsetsPadding(WindowInsets.navigationBars)
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Total Payable (incl. GST):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "₹${String.format(Locale.US, "%.2f", grandTotal)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Button(
              onClick = {
                when (currentCheckoutStep) {
                  0 -> currentCheckoutStep = 1
                  1 -> currentCheckoutStep = 2
                  2 -> {
                    if (selectedPayment == PaymentMethod.COD) {
                      showCodOtpModal = true
                    } else {
                      val order = viewModel.repository.placeOrder(selectedSpeed, selectedPayment)
                      placedOrderResult = order
                    }
                  }
                }
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = when (currentCheckoutStep) {
                  0 -> "Proceed to Delivery"
                  1 -> "Proceed to Payment"
                  2 -> if (selectedPayment == PaymentMethod.COD) "Verify & Place COD Order" else "Authorize & Pay ₹${grandTotal.toInt()}"
                  else -> "Next"
                }
              )
            }
          }
        }
      }
    }
  ) { padding ->
    if (placedOrderResult != null) {
      // Order Placed Success View
      val order = placedOrderResult!!
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Order Confirmed!",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Order #${order.orderNumber}",
          style = MaterialTheme.typography.titleMedium,
          fontFamily = FontFamily.Monospace,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Allocated to ${order.warehouseAllocated}. Encrypted batch seal verified. Broad-spectrum delivery tracking enabled.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Courier Partner:", style = MaterialTheme.typography.labelSmall)
              Text(order.courierPartner, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("AWB Waybill Number:", style = MaterialTheme.typography.labelSmall)
              Text(order.awbTrackingNumber, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Secure Delivery OTP:", style = MaterialTheme.typography.labelSmall)
              Text(order.deliveryOtp, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = {
            onNavigateToTracking(order)
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Track Live Courier Delivery")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = {
            viewModel.openInvoice(order)
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("View Official GST Tax Invoice")
        }
      }
    } else if (cart.isEmpty()) {
      // Empty Cart View
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          Icons.Default.RemoveShoppingCart,
          contentDescription = null,
          modifier = Modifier.size(64.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Your Skincare Bag is Empty",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Explore our dermatologist clinical actives and build your glass skin regimen.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBack) {
          Text("Explore Formulations")
        }
      }
    } else {
      // Normal Multi-Step Checkout Flow
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Free shipping progress bar
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (remainingForFreeShipping <= 0) "✓ FREE Express Delivery Unlocked!" else "Add ₹${remainingForFreeShipping.toInt()} more for FREE Express Shipping",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { freeShipProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        if (currentCheckoutStep == 0) {
          // STEP 0: Cart Review
          items(cart) { item ->
            val itemPrice = item.product.effectivePrice * item.selectedVariant.priceMultiplier * item.quantity
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(item.product.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Text("Size: ${item.selectedVariant.name}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("Batch: ${item.product.batchInfo.batchNumber.takeLast(7)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                  Text("₹${itemPrice.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  IconButton(
                    onClick = { viewModel.repository.updateCartQuantity(item.product.id, item.selectedVariant.variantId, item.quantity - 1) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                  }
                  Text(text = "${item.quantity}", fontWeight = FontWeight.Bold)
                  IconButton(
                    onClick = { viewModel.repository.updateCartQuantity(item.product.id, item.selectedVariant.variantId, item.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                  }
                }
              }
            }
          }

          // Coupon Code Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Promotional Vouchers & Coupons", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedTextField(
                    value = couponInput,
                    onValueChange = { couponInput = it },
                    placeholder = { Text("Enter GLOW20 or FREESHIP") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                  )
                  Button(
                    onClick = {
                      val result = viewModel.repository.applyCoupon(couponInput)
                      Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                      if (result.first) couponInput = ""
                    }
                  ) {
                    Text("Apply")
                  }
                }

                if (appliedCoupon != null) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "✓ ${appliedCoupon!!.code} applied (${appliedCoupon!!.discountPercent}% OFF)",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.repository.removeCoupon() }) {
                      Text("Remove", style = MaterialTheme.typography.labelSmall)
                    }
                  }
                }
              }
            }
          }
        } else if (currentCheckoutStep == 1) {
          // STEP 1: Address & Delivery Speed
          item {
            Text("Shipping Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          items(savedAddresses) { address ->
            val isSelected = address.id == selectedAddress?.id
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Select address */ },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
              ),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
              ) {
                RadioButton(selected = isSelected, onClick = {})
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(address.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      color = MaterialTheme.colorScheme.primary,
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "PINCODE: ${address.pincode} (Serviceable)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text("${address.street}, ${address.city}, ${address.state}", style = MaterialTheme.typography.bodySmall)
                  Text("Phone: ${address.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("Instructions: ${address.deliveryInstructions}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Select Delivery Speed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          items(DeliverySpeed.values()) { speed ->
            val isSelected = speed == selectedSpeed
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedSpeed = speed },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
              ),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(selected = isSelected, onClick = { selectedSpeed = speed })
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(speed.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Text(speed.duration, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                  text = if (effectiveShippingFee == 0.0) "FREE" else "₹${speed.fee.toInt()}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        } else if (currentCheckoutStep == 2) {
          // STEP 2: Payment Method
          item {
            Text("Select Payment Gateway", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          items(PaymentMethod.values()) { method ->
            val isSelected = method == selectedPayment
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedPayment = method },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
              ),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(selected = isSelected, onClick = { selectedPayment = method })
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(method.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Text(method.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }

        // Price Summary Breakdown
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("Order Financial Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                Text("₹${String.format(Locale.US, "%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("GST Tax (18% Clinical)", style = MaterialTheme.typography.bodySmall)
                Text("₹${String.format(Locale.US, "%.2f", taxGst)}", style = MaterialTheme.typography.bodySmall)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Shipping Fee", style = MaterialTheme.typography.bodySmall)
                Text(if (effectiveShippingFee == 0.0) "FREE" else "₹${effectiveShippingFee.toInt()}", style = MaterialTheme.typography.bodySmall)
              }
              if (discountAmount > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Promo Discount", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                  Text("-₹${String.format(Locale.US, "%.2f", discountAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Grand Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                  "₹${String.format(Locale.US, "%.2f", grandTotal)}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }
  }

  // COD OTP Verification Modal
  if (showCodOtpModal) {
    AlertDialog(
      onDismissRequest = { showCodOtpModal = false },
      title = { Text("Doorstep Cash On Delivery OTP Security") },
      text = {
        Column {
          Text("For anti-fraud verification and safe delivery, a 4-digit verification code will be generated for your order. Confirm your mobile number (+91 98201 44521):")
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Estimated delivery cash to be paid: ₹${grandTotal.toInt()}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showCodOtpModal = false
            val order = viewModel.repository.placeOrder(selectedSpeed, selectedPayment)
            placedOrderResult = order
          }
        ) {
          Text("Confirm COD Order")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCodOtpModal = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
