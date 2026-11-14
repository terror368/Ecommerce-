package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.SkincareViewModel
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.ReturnDialog
import com.example.ui.components.RoleSwitchModal
import com.example.ui.screens.*
import com.example.ui.theme.GlowMatrixTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      GlowMatrixTheme {
        GlowMatrixApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlowMatrixApp(viewModel: SkincareViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentRole by viewModel.repository.currentRole.collectAsState()
  val cart by viewModel.repository.cart.collectAsState()
  val selectedProduct by viewModel.selectedProduct.collectAsState()
  val selectedOrder by viewModel.selectedOrder.collectAsState()

  val showRoleSwitcher by viewModel.showRoleSwitcher.collectAsState()
  val showInvoiceDialog by viewModel.showInvoiceDialog.collectAsState()
  val invoiceOrder by viewModel.invoiceOrder.collectAsState()
  val showReturnDialog by viewModel.showReturnDialog.collectAsState()
  val returnTargetOrder by viewModel.returnTargetOrder.collectAsState()

  // Handle back button for secondary screens
  BackHandler(enabled = currentScreen != AppScreen.STOREFRONT) {
    when (currentScreen) {
      AppScreen.PRODUCT_DETAIL, AppScreen.SKIN_QUIZ, AppScreen.CART_CHECKOUT, AppScreen.ORDER_TRACKING -> {
        viewModel.navigateTo(AppScreen.STOREFRONT)
      }
      AppScreen.ADMIN_OPS, AppScreen.LOYALTY_ACCOUNT -> {
        viewModel.navigateTo(AppScreen.STOREFRONT)
      }
      else -> {}
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Spa,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "GlowMatrix",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Clinical Skincare Ecosystem",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Persona Switcher Chip in App Bar
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            onClick = { viewModel.toggleRoleSwitcher(true) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(currentRole.badgeColor))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = when (currentRole) {
                  UserRole.CUSTOMER -> "Customer"
                  UserRole.SUPER_ADMIN -> "SuperAdmin"
                  UserRole.OPERATIONS_ADMIN -> "Ops Admin"
                  UserRole.SELLER_VENDOR -> "Seller"
                  UserRole.DELIVERY_PARTNER -> "Delivery"
                  UserRole.CUSTOMER_SUPPORT -> "Support"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
              Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Switch Persona",
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Cart icon with badge
          IconButton(onClick = { viewModel.navigateTo(AppScreen.CART_CHECKOUT) }) {
            BadgedBox(
              badge = {
                val totalQty = cart.sumOf { it.quantity }
                if (totalQty > 0) {
                  Badge {
                    Text("$totalQty")
                  }
                }
              }
            ) {
              Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
            }
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        NavigationBarItem(
          selected = currentScreen == AppScreen.STOREFRONT,
          onClick = { viewModel.navigateTo(AppScreen.STOREFRONT) },
          icon = { Icon(Icons.Default.Storefront, contentDescription = "Shop") },
          label = { Text("Discover") }
        )
        NavigationBarItem(
          selected = currentScreen == AppScreen.SKIN_QUIZ,
          onClick = { viewModel.navigateTo(AppScreen.SKIN_QUIZ) },
          icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Skin Quiz") },
          label = { Text("Skin Quiz") }
        )
        NavigationBarItem(
          selected = currentScreen == AppScreen.ADMIN_OPS,
          onClick = { viewModel.navigateTo(AppScreen.ADMIN_OPS) },
          icon = { Icon(Icons.Default.DashboardCustomize, contentDescription = "Ops Hub") },
          label = { Text("Ops Hub") }
        )
        NavigationBarItem(
          selected = currentScreen == AppScreen.LOYALTY_ACCOUNT,
          onClick = { viewModel.navigateTo(AppScreen.LOYALTY_ACCOUNT) },
          icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
          label = { Text("Account") }
        )
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      when (currentScreen) {
        AppScreen.STOREFRONT -> {
          StorefrontScreen(
            viewModel = viewModel,
            onNavigateToQuiz = { viewModel.navigateTo(AppScreen.SKIN_QUIZ) },
            onNavigateToProduct = { product -> viewModel.openProductDetail(product) },
            onNavigateToCart = { viewModel.navigateTo(AppScreen.CART_CHECKOUT) }
          )
        }
        AppScreen.PRODUCT_DETAIL -> {
          if (selectedProduct != null) {
            ProductDetailScreen(
              viewModel = viewModel,
              product = selectedProduct!!,
              onBack = { viewModel.navigateTo(AppScreen.STOREFRONT) },
              onNavigateToCart = { viewModel.navigateTo(AppScreen.CART_CHECKOUT) }
            )
          } else {
            viewModel.navigateTo(AppScreen.STOREFRONT)
          }
        }
        AppScreen.SKIN_QUIZ -> {
          SkinQuizScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.STOREFRONT) },
            onNavigateToCart = { viewModel.navigateTo(AppScreen.CART_CHECKOUT) }
          )
        }
        AppScreen.CART_CHECKOUT -> {
          CartAndCheckoutScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.STOREFRONT) },
            onNavigateToTracking = { order -> viewModel.openOrderTracking(order) }
          )
        }
        AppScreen.ORDER_TRACKING -> {
          if (selectedOrder != null) {
            OrderTrackingScreen(
              viewModel = viewModel,
              order = selectedOrder!!,
              onBack = { viewModel.navigateTo(AppScreen.STOREFRONT) }
            )
          } else {
            viewModel.navigateTo(AppScreen.STOREFRONT)
          }
        }
        AppScreen.ADMIN_OPS -> {
          AdminAndOpsScreen(
            viewModel = viewModel,
            onOpenOrder = { order -> viewModel.openOrderTracking(order) }
          )
        }
        AppScreen.LOYALTY_ACCOUNT -> {
          LoyaltyAndAccountScreen(
            viewModel = viewModel,
            onOpenOrder = { order -> viewModel.openOrderTracking(order) }
          )
        }
      }
    }
  }

  // Role Switcher Modal
  if (showRoleSwitcher) {
    RoleSwitchModal(
      currentRole = currentRole,
      onRoleSelected = { role -> viewModel.repository.switchRole(role) },
      onDismiss = { viewModel.toggleRoleSwitcher(false) }
    )
  }

  // GST Tax Invoice Dialog
  if (showInvoiceDialog && invoiceOrder != null) {
    InvoiceDialog(
      order = invoiceOrder!!,
      onDismiss = { viewModel.closeInvoice() }
    )
  }

  // Self-Service Return Dialog
  if (showReturnDialog && returnTargetOrder != null) {
    ReturnDialog(
      order = returnTargetOrder!!,
      onConfirmReturn = { orderId, itemTitle, reason ->
        viewModel.repository.initiateReturn(orderId, itemTitle, reason)
      },
      onDismiss = { viewModel.closeReturnDialog() }
    )
  }
}
