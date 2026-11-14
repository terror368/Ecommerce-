package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.AppScreen
import com.example.ui.SkincareViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorefrontScreen(
  viewModel: SkincareViewModel,
  onNavigateToQuiz: () -> Unit,
  onNavigateToProduct: (SkincareProduct) -> Unit,
  onNavigateToCart: () -> Unit
) {
  val products by viewModel.repository.products.collectAsState()
  val wishlist by viewModel.repository.wishlist.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedSkinType by viewModel.selectedSkinType.collectAsState()
  val selectedConcern by viewModel.selectedSkinConcern.collectAsState()
  val flashSaleSeconds by viewModel.flashSaleSeconds.collectAsState()

  // Format flash sale countdown
  val hours = flashSaleSeconds / 3600
  val minutes = (flashSaleSeconds % 3600) / 60
  val seconds = flashSaleSeconds % 60
  val countdownStr = String.format(Locale.US, "%02dh : %02dm : %02ds", hours, minutes, seconds)

  // Filter products
  val filteredProducts = remember(products, searchQuery, selectedCategory, selectedSkinType, selectedConcern) {
    products.filter { prod ->
      val matchesSearch = searchQuery.isBlank() ||
          prod.title.contains(searchQuery, ignoreCase = true) ||
          prod.brand.contains(searchQuery, ignoreCase = true) ||
          prod.fullIngredientsText.contains(searchQuery, ignoreCase = true)

      val matchesCategory = selectedCategory == SkincareCategory.ALL || prod.category == selectedCategory
      val matchesSkinType = selectedSkinType == SkinType.ALL || prod.skinTypes.contains(selectedSkinType) || prod.skinTypes.contains(SkinType.ALL)
      val matchesConcern = selectedConcern == null || prod.skinConcerns.contains(selectedConcern)

      matchesSearch && matchesCategory && matchesSkinType && matchesConcern
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 32.dp)
  ) {
    // Top Hero Editorial Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_skincare_hero),
          contentDescription = "GlowMatrix Editorial Campaign",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xCC16131D)),
                startY = 60f
              )
            )
        )

        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "DERMATOLOGIST CLINICAL GRADE",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Clean Science. Radiant Results.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
          Text(
            text = "Pure actives, cold-formulated with verified batch traceability.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
          )
        }
      }
    }

    // Search Box & Skin Consultation Quick Access
    item {
      Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("Search serums, SPF, ceramides or ingredients...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // AI Skin Quiz Banner
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToQuiz() },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Personalized AI Skin Regimen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "Take our 60-sec quiz to unlock tailored AM/PM routine & bundle discount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
              )
            }
            Icon(
              Icons.Default.ChevronRight,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }
    }

    // Flash Sale Countdown Bar
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF241B1F))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Timer,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "MIDNIGHT GLOW FLASH SALE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = "Up to 40% OFF Clinical Actives",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = countdownStr,
              style = MaterialTheme.typography.labelMedium,
              color = Color.White,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }
    }

    // Filter Section: Categories
    item {
      Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
          text = "Explore Formulations",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(SkincareCategory.values()) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setCategory(category) },
              label = { Text(category.label) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // Filter Section: Skin Type
    item {
      Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
          text = "Target Skin Type",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(SkinType.values()) { skinType ->
            val isSelected = skinType == selectedSkinType
            SuggestionChip(
              onClick = { viewModel.setSkinType(skinType) },
              label = { Text(skinType.label, style = MaterialTheme.typography.labelSmall) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
              ),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary) else null
            )
          }
        }
      }
    }

    // Results Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${filteredProducts.size} Clinical Formulations",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        if (selectedConcern != null || selectedSkinType != SkinType.ALL || selectedCategory != SkincareCategory.ALL) {
          TextButton(
            onClick = {
              viewModel.setCategory(SkincareCategory.ALL)
              viewModel.setSkinType(SkinType.ALL)
              viewModel.setSkinConcern(null)
            }
          ) {
            Text("Reset Filters", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }

    // Products List
    items(filteredProducts) { product ->
      val isWishlisted = wishlist.contains(product.id)
      ProductCard(
        product = product,
        isWishlisted = isWishlisted,
        onProductClick = { onNavigateToProduct(product) },
        onToggleWishlist = { viewModel.repository.toggleWishlist(product.id) },
        onAddToCart = { variant ->
          viewModel.repository.addToCart(product, variant, 1)
        }
      )
    }
  }
}

@Composable
fun ProductCard(
  product: SkincareProduct,
  isWishlisted: Boolean,
  onProductClick: () -> Unit,
  onToggleWishlist: () -> Unit,
  onAddToCart: (ProductVariant) -> Unit
) {
  var selectedVariant by remember { mutableStateOf(product.variants.first()) }
  var showAddedSnackbar by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .clickable { onProductClick() },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Badges row
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (product.isBestSeller) {
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "BESTSELLER",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            if (product.batchInfo.isNearExpiry) {
              Surface(
                color = Color(0xFFFFECEE),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "EXPIRY CLEARANCE ${product.batchInfo.autoDiscountPercent}% OFF",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE04F5F),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = product.volume,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = product.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          Text(
            text = "by ${product.brand}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = onToggleWishlist) {
          Icon(
            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Wishlist",
            tint = if (isWishlisted) Color(0xFFE04F5F) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Key actives tag row
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        product.dermatologyIngredients.take(2).forEach { active ->
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "${active.name} (${active.concentration})",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Variant Selector (if multiple variants)
      if (product.variants.size > 1) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Size:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          product.variants.forEach { variant ->
            val isSelected = variant.variantId == selectedVariant.variantId
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
              modifier = Modifier.clickable { selectedVariant = variant }
            ) {
              Text(
                text = variant.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

      Spacer(modifier = Modifier.height(10.dp))

      // Price and Add to Cart Row
      val calculatedPrice = product.effectivePrice * selectedVariant.priceMultiplier
      val calculatedOriginalPrice = product.originalPrice * selectedVariant.priceMultiplier

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${calculatedPrice.toInt()}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            if (calculatedOriginalPrice > calculatedPrice) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "₹${calculatedOriginalPrice.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Star,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${product.rating} (${product.reviewCount})",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "• Batch: ${product.batchInfo.batchNumber.takeLast(6)}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Button(
          onClick = {
            onAddToCart(selectedVariant)
            showAddedSnackbar = true
          },
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add to Bag", style = MaterialTheme.typography.labelMedium)
        }
      }

      if (showAddedSnackbar) {
        Text(
          text = "✓ Added ${product.title} to bag!",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }
  }
}
