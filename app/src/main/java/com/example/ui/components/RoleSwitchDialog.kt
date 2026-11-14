package com.example.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitchModal(
  currentRole: UserRole,
  onRoleSelected: (UserRole) -> Unit,
  onDismiss: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Enterprise Role Switcher",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Test all 6 Amazon-scale operational personas & portals",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(UserRole.values()) { role ->
          val isSelected = role == currentRole
          val icon: ImageVector = when (role) {
            UserRole.CUSTOMER -> Icons.Default.ShoppingBag
            UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
            UserRole.OPERATIONS_ADMIN -> Icons.Default.Warehouse
            UserRole.SELLER_VENDOR -> Icons.Default.Storefront
            UserRole.DELIVERY_PARTNER -> Icons.Default.LocalShipping
            UserRole.CUSTOMER_SUPPORT -> Icons.Default.SupportAgent
          }
          val roleDesc = when (role) {
            UserRole.CUSTOMER -> "Browse catalog, Skin Quiz AI, checkout & track live courier"
            UserRole.SUPER_ADMIN -> "Executive GMV analytics, audit trails & DPDP privacy consent"
            UserRole.OPERATIONS_ADMIN -> "Multi-warehouse allocation, batch expiries & packing manifests"
            UserRole.SELLER_VENDOR -> "Product catalog CRUD, batch number manager & stock levels"
            UserRole.DELIVERY_PARTNER -> "Doorstep deliveries, OTP verification & COD cash collection"
            UserRole.CUSTOMER_SUPPORT -> "Live customer tickets, instant store credit refunds & resolution"
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onRoleSelected(role)
                onDismiss()
              },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Color(role.badgeColor)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = role.displayName,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = role.displayName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = roleDesc,
                  style = MaterialTheme.typography.bodySmall,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              if (isSelected) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = "Active",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }
  }
}
