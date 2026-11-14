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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.SkincareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  viewModel: SkincareViewModel,
  product: SkincareProduct,
  onBack: () -> Unit,
  onNavigateToCart: () -> Unit
) {
  val context = LocalContext.current
  val wishlist by viewModel.repository.wishlist.collectAsState()
  val isWishlisted = wishlist.contains(product.id)
  var selectedVariant by remember { mutableStateOf(product.variants.first()) }
  var quantity by remember { mutableStateOf(1) }

  val sampleReviews = remember {
    listOf(
      ProductReview(
        id = "REV-1",
        author = "Neha K. (Verified Buyer)",
        rating = 5,
        verifiedSkinType = SkinType.OILY,
        date = "28 Oct 2026",
        headline = "Cleared my stubborn acne scars in 3 weeks!",
        comment = "I have oily, hyperpigmentation-prone skin. This serum did not pill, caused zero irritation, and visibly faded dark marks.",
        helpfulVotes = 58,
        sellerReply = "Luminia Dermatology Team: Thank you Neha! Pairing this with our Ceramide Barrier Cream provides optimal epidermal repair."
      ),
      ProductReview(
        id = "REV-2",
        author = "Dr. Rohan M. (Cosmetic Chemist)",
        rating = 5,
        verifiedSkinType = SkinType.COMBINATION,
        date = "15 Oct 2026",
        headline = "Impressive stability and bio-compatible formulation",
        comment = "The formulation pH is well-calibrated (pH 5.4). Zinc PCA synergizes well without being overly drying.",
        helpfulVotes = 84,
        sellerReply = null
      )
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(product.brand, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.repository.toggleWishlist(product.id) }) {
            Icon(
              imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = "Wishlist",
              tint = if (isWishlisted) Color(0xFFE04F5F) else MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(onClick = onNavigateToCart) {
            Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
          }
        }
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        val finalPrice = product.effectivePrice * selectedVariant.priceMultiplier * quantity
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
              text = "Total Price:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "₹${finalPrice.toInt()}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = {
                viewModel.repository.addToCart(product, selectedVariant, quantity)
                Toast.makeText(context, "Added to Bag", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add to Bag")
            }

            Button(
              onClick = {
                viewModel.repository.addToCart(product, selectedVariant, quantity)
                onNavigateToCart()
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Buy Now")
            }
          }
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Product Title & Brand
      item {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = product.category.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
            if (product.batchInfo.isNearExpiry) {
              Surface(
                color = Color(0xFFFFECEE),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "EXPIRY DISCOUNT ${product.batchInfo.autoDiscountPercent}% OFF",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE04F5F),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
          )

          Text(
            text = "Formulated by ${product.brand} • ${product.volume}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Rating & Reviews row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Star,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${product.rating}",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(
              text = "(${product.reviewCount} verified consumer ratings)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Variant Selector Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Select Size / Volume Variant:",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              product.variants.forEach { variant ->
                val isSelected = variant.variantId == selectedVariant.variantId
                val vPrice = product.effectivePrice * variant.priceMultiplier
                Card(
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedVariant = variant },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                  ),
                  border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                  Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = variant.name,
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                      text = "₹${vPrice.toInt()}",
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

      // Dermatological Actives Breakdown
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Biotech, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Clinical Actives & Dermatology Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(12.dp))

            product.dermatologyIngredients.forEach { item ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = item.name,
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Bold
                    )
                    Surface(
                      color = MaterialTheme.colorScheme.primary,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = item.concentration,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = item.clinicalRole,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // Batch, Shelf Life & Warehouse Allocation Traceability
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Batch Traceability & Expiry Engine",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Batch Number:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(product.batchInfo.batchNumber, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Mfg / Expiry Date:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${product.batchInfo.mfgDate}  ➔  ${product.batchInfo.expiryDate}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Multi-Warehouse Inventory Allocation:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              product.warehouseStock.forEach { (warehouse, stock) ->
                Surface(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                  Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = warehouse.replace("WH-", ""),
                      style = MaterialTheme.typography.labelSmall,
                      fontSize = 10.sp,
                      maxLines = 1
                    )
                    Text(
                      text = "$stock in stock",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = if (stock < 15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Usage Instructions & Patch Test Caution
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Directions For Clinical Application",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = product.usageInstructions,
              style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Patch test caution box
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top
              ) {
                Icon(
                  Icons.Default.Shield,
                  contentDescription = null,
                  tint = Color(0xFFC2410C),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Dermatologist Patch Test Protocol",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A3412)
                  )
                  Text(
                    text = product.patchTestWarning,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7C2D12)
                  )
                }
              }
            }
          }
        }
      }

      // Verified Buyer Reviews
      item {
        Column {
          Text(
            text = "Verified Buyer Clinical Feedback",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))

          sampleReviews.forEach { review ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(review.author, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Surface(
                      color = MaterialTheme.colorScheme.surfaceVariant,
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "Skin Type: ${review.verifiedSkinType.label}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Row {
                    repeat(review.rating) {
                      Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(review.headline, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text(review.comment, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (review.sellerReply != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  ) {
                    Text(
                      text = review.sellerReply,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(8.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
