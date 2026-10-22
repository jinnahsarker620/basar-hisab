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

private val DarkColorScheme = darkColorScheme(
  primary = GreenPrimaryDarkTheme,
  onPrimary = Color(0xFF00390E),
  primaryContainer = DarkContainer,
  onPrimaryContainer = Color(0xFFA5D6A7),
  secondary = GreenSecondaryDarkTheme,
  onSecondary = Color(0xFF103816),
  background = DarkSurface,
  surface = DarkSurfaceVariant,
  onBackground = Color(0xFFE2E3DF),
  onSurface = Color(0xFFE2E3DF),
  surfaceVariant = Color(0xFF283629),
  onSurfaceVariant = Color(0xFFC3C8C0),
  outline = Color(0xFF4C5D4E)
)

private val LightColorScheme = lightColorScheme(
  primary = GreenPrimary,
  onPrimary = Color.White,
  primaryContainer = GreenContainerLight,
  onPrimaryContainer = GreenPrimaryDark,
  secondary = GreenPrimaryLight,
  onSecondary = Color.White,
  secondaryContainer = GreenCardLight,
  onSecondaryContainer = GreenPrimaryDark,
  tertiary = GreenTertiary,
  background = GreenSurfaceLight,
  surface = Color.White,
  onBackground = Color(0xFF191C19),
  onSurface = Color(0xFF191C19),
  surfaceVariant = GreenCardLight,
  onSurfaceVariant = Color(0xFF384339),
  outline = GreenBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Use intentional green brand colors by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
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
