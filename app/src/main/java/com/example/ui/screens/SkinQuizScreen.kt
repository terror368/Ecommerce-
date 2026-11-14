package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.SkincareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinQuizScreen(
  viewModel: SkincareViewModel,
  onBack: () -> Unit,
  onNavigateToCart: () -> Unit
) {
  val context = LocalContext.current
  val products by viewModel.repository.products.collectAsState()

  var currentStep by remember { mutableStateOf(0) }
  var chosenSkinType by remember { mutableStateOf(SkinType.OILY) }
  var chosenConcern by remember { mutableStateOf(SkinConcern.ACNE) }
  var chosenSensitivity by remember { mutableStateOf("Moderate - occasional redness") }
  var chosenRoutineHabit by remember { mutableStateOf("Consistent daily AM & PM") }
  var isResultReady by remember { mutableStateOf(false) }

  val questions = listOf(
    "What is your predominant natural skin type?" to listOf(
      SkinType.OILY to "Excess shine throughout the day, enlarged pores",
      SkinType.DRY to "Feels tight after cleansing, prone to flaking",
      SkinType.COMBINATION to "Oily T-zone (forehead, nose) with normal/dry cheeks",
      SkinType.SENSITIVE to "Easily stings, reactive to weather or active ingredients"
    ),
    "What is your primary clinical skin goal?" to listOf(
      SkinConcern.ACNE to "Target breakouts, blackheads & congestion",
      SkinConcern.HYPERPIGMENTATION to "Fade dark spots, melasma & acne marks",
      SkinConcern.AGING to "Smooth fine lines, loss of firmness & wrinkles",
      SkinConcern.BARRIER_REPAIR to "Restore damaged epidermal barrier & stop redness"
    )
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("AI Skin Health Consultation", style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
    ) {
      if (!isResultReady) {
        // Progress Indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Step ${currentStep + 1} of 3",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
          LinearProgressIndicator(
            progress = { (currentStep + 1) / 3f },
            modifier = Modifier
              .width(160.dp)
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (currentStep) {
          0 -> {
            Text(
              text = "What is your predominant skin type?",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            questions[0].second.forEach { (type, description) ->
              val isSelected = chosenSkinType == type
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp)
                  .clickable { chosenSkinType = type as SkinType },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { chosenSkinType = type as SkinType }
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = (type as SkinType).label,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = description,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
          1 -> {
            Text(
              text = "What is your primary clinical concern?",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            questions[1].second.forEach { (concern, description) ->
              val isSelected = chosenConcern == concern
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp)
                  .clickable { chosenConcern = concern as SkinConcern },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { chosenConcern = concern as SkinConcern }
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = (concern as SkinConcern).label,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = description,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
          2 -> {
            Text(
              text = "How sensitive is your skin to active formulations?",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            val sensitivities = listOf(
              "High - Often red, burns easily, rosacea prone",
              "Moderate - occasional redness with strong acids",
              "Resilient - rarely stings, tolerates retinoids well"
            )
            sensitivities.forEach { sens ->
              val isSelected = chosenSensitivity == sens
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp)
                  .clickable { chosenSensitivity = sens },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
              ) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(selected = isSelected, onClick = { chosenSensitivity = sens })
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = sens, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          if (currentStep > 0) {
            OutlinedButton(onClick = { currentStep -= 1 }) {
              Text("Back")
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          Button(
            onClick = {
              if (currentStep < 2) {
                currentStep += 1
              } else {
                isResultReady = true
              }
            }
          ) {
            Text(if (currentStep == 2) "Generate AI Regimen" else "Next Step")
          }
        }
      } else {
        // Result Screen: Personalized Regimen + Bundle
        val recommendedSerum = products.find { it.category == SkincareCategory.SERUMS } ?: products.first()
        val recommendedCleanser = products.find { it.category == SkincareCategory.CLEANSERS } ?: products[1]
        val recommendedCream = products.find { it.category == SkincareCategory.MOISTURIZERS } ?: products[2]
        val recommendedSpf = products.find { it.category == SkincareCategory.SUNSCREENS } ?: products[1]

        val bundleItems = listOf(recommendedCleanser, recommendedSerum, recommendedCream)
        val bundlePrice = bundleItems.sumOf { it.effectivePrice } * 0.80 // 20% bundle discount

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Dermatologist Algorithmic Diagnosis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Custom Regimen for: ${chosenSkinType.label} • Priority: ${chosenConcern.label}",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                  text = "Formulated to rebuild stratum corneum while delivering non-irritating clinical actives.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
              }
            }
          }

          item {
            Text(
              text = "Your Personalized 4-Step Regimen",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          // Step 1: Cleanser
          item {
            RegimenStepCard(
              stepNumber = "01",
              stepTime = "AM / PM",
              title = "Gentle Cellular Clarification",
              productTitle = recommendedCleanser.title,
              benefit = "Removes impurities without stripping skin barrier"
            )
          }

          // Step 2: Treatment Serum
          item {
            RegimenStepCard(
              stepNumber = "02",
              stepTime = "AM / PM",
              title = "Targeted Molecular Treatment",
              productTitle = recommendedSerum.title,
              benefit = "Dermatological active for ${chosenConcern.label}"
            )
          }

          // Step 3: Moisturizer
          item {
            RegimenStepCard(
              stepNumber = "03",
              stepTime = "AM / PM",
              title = "Lipid Matrix Reconstruction",
              productTitle = recommendedCream.title,
              benefit = "5 Ceramides lock moisture & accelerate regeneration"
            )
          }

          // Step 4: Sunscreen
          item {
            RegimenStepCard(
              stepNumber = "04",
              stepTime = "AM EXCLUSIVE",
              title = "Photostable Broad Spectrum UV Defense",
              productTitle = recommendedSpf.title,
              benefit = "Zero white cast SPF 50+ PA++++ shields new skin cells"
            )
          }

          // 1-Click Routine Bundle Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Surface(
                      color = MaterialTheme.colorScheme.primary,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "20% ROUTINE BUNDLE DISCOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Complete Regimen Tri-Set",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Cleanser + Active Serum + Barrier Cream",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Text(
                    text = "₹${bundlePrice.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                  onClick = {
                    bundleItems.forEach { prod ->
                      viewModel.repository.addToCart(prod, prod.variants.first(), 1)
                    }
                    Toast.makeText(context, "Full 3-Piece Regimen Bundle added to cart with 20% discount!", Toast.LENGTH_LONG).show()
                    onNavigateToCart()
                  },
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Add Full Regimen to Bag (Save ₹420)")
                }
              }
            }
          }

          item {
            OutlinedButton(
              onClick = {
                isResultReady = false
                currentStep = 0
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Retake Skin Quiz")
            }
          }
        }
      }
    }
  }
}

@Composable
fun RegimenStepCard(
  stepNumber: String,
  stepTime: String,
  title: String,
  productTitle: String,
  benefit: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stepNumber,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          Text(text = stepTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = productTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = benefit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}
