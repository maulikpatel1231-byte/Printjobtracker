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

private val LightColorScheme = lightColorScheme(
  primary = PrintPrimary,
  onPrimary = PrintOnPrimary,
  primaryContainer = PrintPrimaryContainer,
  onPrimaryContainer = PrintOnPrimaryContainer,
  secondary = PrintSecondary,
  onSecondary = PrintOnSecondary,
  secondaryContainer = PrintSecondaryContainer,
  onSecondaryContainer = PrintOnSecondaryContainer,
  tertiary = PrintTertiary,
  onTertiary = PrintOnTertiary,
  tertiaryContainer = PrintTertiaryContainer,
  onTertiaryContainer = PrintOnTertiaryContainer,
  background = LightBackground,
  onBackground = Color(0xFF0F172A),
  surface = LightSurface,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = Color(0xFF334155),
  outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF93C5FD),
  onPrimary = Color(0xFF1E3A8A),
  primaryContainer = Color(0xFF1E40AF),
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = Color(0xFF5EEAD4),
  onSecondary = Color(0xFF134E4A),
  secondaryContainer = Color(0xFF115E59),
  onSecondaryContainer = Color(0xFFCCFBF1),
  tertiary = Color(0xFFFCD34D),
  onTertiary = Color(0xFF78350F),
  tertiaryContainer = Color(0xFF92400E),
  onTertiaryContainer = Color(0xFFFEF3C7),
  background = DarkBackground,
  onBackground = Color(0xFFF8FAFC),
  surface = DarkSurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFE2E8F0),
  outline = DarkOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding
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
