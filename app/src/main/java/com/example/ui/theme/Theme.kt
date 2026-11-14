package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RoseGoldPrimary,
    onPrimary = Color.White,
    primaryContainer = RoseGoldDark,
    onPrimaryContainer = RoseGoldLight,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF423518),
    onSecondaryContainer = GoldLight,
    tertiary = BotanicalSage,
    onTertiary = Color.White,
    background = DeepPlumCaviar,
    onBackground = TextPrimaryLight,
    surface = DarkSurfaceElevated,
    onSurface = TextPrimaryLight,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFF453F54),
    error = AlertRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RoseGoldPrimary,
    onPrimary = Color.White,
    primaryContainer = RoseGoldLight,
    onPrimaryContainer = RoseGoldDark,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Color(0xFF594509),
    tertiary = BotanicalSage,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryDark,
    surface = LightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFF2ECE8),
    onSurfaceVariant = TextSecondaryDark,
    outline = LightCardBorder,
    error = AlertRed
  )

@Composable
fun GlowMatrixTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve bespoke skincare brand aesthetics
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
