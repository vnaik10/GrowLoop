package com.example.growloop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.yourpackage.ui.theme.Alpha
import com.yourpackage.ui.theme.BackgroundPrimary
import com.yourpackage.ui.theme.BackgroundSecondary
import com.yourpackage.ui.theme.BrandBlue
import com.yourpackage.ui.theme.BrandBlueSoft
import com.yourpackage.ui.theme.BrandGreen
import com.yourpackage.ui.theme.BrandGreenDark
import com.yourpackage.ui.theme.BrandGreenDeep
import com.yourpackage.ui.theme.BrandGreenLight
import com.yourpackage.ui.theme.BrandGreenSoft
import com.yourpackage.ui.theme.BrandPurple
import com.yourpackage.ui.theme.BrandPurpleDeep
import com.yourpackage.ui.theme.CategoryAdult
import com.yourpackage.ui.theme.CategoryBaby
import com.yourpackage.ui.theme.CategoryKids
import com.yourpackage.ui.theme.CategoryTeen
import com.yourpackage.ui.theme.CategoryToddler
import com.yourpackage.ui.theme.ConditionExcellent
import com.yourpackage.ui.theme.ConditionFair
import com.yourpackage.ui.theme.ConditionGood
import com.yourpackage.ui.theme.ConditionWorn
import com.yourpackage.ui.theme.ErrorLight
import com.yourpackage.ui.theme.ErrorRed
import com.yourpackage.ui.theme.NeutralBlack
import com.yourpackage.ui.theme.NeutralDark
import com.yourpackage.ui.theme.NeutralDeep
import com.yourpackage.ui.theme.NeutralGray
import com.yourpackage.ui.theme.NeutralLighter
import com.yourpackage.ui.theme.NeutralMedium
import com.yourpackage.ui.theme.NeutralWhite
import com.yourpackage.ui.theme.ProgressBackground
import com.yourpackage.ui.theme.ProgressComplete
import com.yourpackage.ui.theme.ProgressForeground
import com.yourpackage.ui.theme.SeasonAutumn
import com.yourpackage.ui.theme.SeasonSpring
import com.yourpackage.ui.theme.SeasonSummer
import com.yourpackage.ui.theme.SeasonWinter
import com.yourpackage.ui.theme.SurfacePrimary
import com.yourpackage.ui.theme.SurfaceSecondary
import com.yourpackage.ui.theme.SurfaceTertiary
import com.yourpackage.ui.theme.WarningOrange

//THEME.kt
// ============= CUSTOM THEME COLORS =============
// Extended color palette for app-specific needs
data class GrowLoopColors(
    // Brand colors
    val brandGreen: Color = BrandGreen,
    val brandGreenDark: Color = BrandGreenDark,
    val brandPurple: Color = BrandPurple,

    // Progress colors
    val progressBackground: Color = ProgressBackground,
    val progressForeground: Color = ProgressForeground,
    val progressComplete: Color = ProgressComplete,

    // Category colors
    val categoryBaby: Color = CategoryBaby,
    val categoryToddler: Color = CategoryToddler,
    val categoryKids: Color = CategoryKids,
    val categoryTeen: Color = CategoryTeen,
    val categoryAdult: Color = CategoryAdult,

    // Condition colors
    val conditionExcellent: Color = ConditionExcellent,
    val conditionGood: Color = ConditionGood,
    val conditionFair: Color = ConditionFair,
    val conditionWorn: Color = ConditionWorn,

    // Seasonal colors
    val seasonSpring: Color = SeasonSpring,
    val seasonSummer: Color = SeasonSummer,
    val seasonAutumn: Color = SeasonAutumn,
    val seasonWinter: Color = SeasonWinter,

    // Surface variants
    val surfacePrimary: Color = SurfacePrimary,
    val surfaceSecondary: Color = SurfaceSecondary,
    val surfaceTertiary: Color = SurfaceTertiary,
    val backgroundPrimary: Color = BackgroundPrimary,
    val backgroundSecondary: Color = BackgroundSecondary
)

// Composition local for custom colors
val LocalGrowLoopColors = staticCompositionLocalOf { GrowLoopColors() }

// ============= MATERIAL 3 COLOR SCHEME =============
// Single theme that works in both light and dark modes
private val GrowLoopColorScheme = lightColorScheme(
    // Primary colors - Main brand identity
    primary = BrandGreen,
    onPrimary = NeutralWhite,
    primaryContainer = BrandGreenSoft,
    onPrimaryContainer = BrandGreenDeep,

    // Secondary colors - Supporting actions
    secondary = BrandPurple,
    onSecondary = NeutralWhite,
    secondaryContainer = BrandPurple.copy(alpha = 0.12f),
    onSecondaryContainer = BrandPurpleDeep,

    // Tertiary colors - Accent elements
    tertiary = BrandBlue,
    onTertiary = NeutralWhite,
    tertiaryContainer = BrandBlueSoft.copy(alpha = 0.12f),
    onTertiaryContainer = BrandBlue,

    // Error colors - Error states and validation
    error = ErrorRed,
    onError = NeutralWhite,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorRed,

    // Background colors - Main app backgrounds
    background = BackgroundPrimary,
    onBackground = NeutralDeep,

    // Surface colors - Cards, sheets, dialogs
    surface = SurfacePrimary,
    onSurface = NeutralDeep,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = NeutralDark,

    // Container surfaces - Different elevation levels
    surfaceContainer = SurfacePrimary,
    surfaceContainerHigh = SurfaceSecondary,
    surfaceContainerHighest = SurfaceTertiary,
    surfaceContainerLow = BackgroundPrimary,
    surfaceContainerLowest = NeutralWhite,

    // Outline colors - Borders and dividers
    outline = NeutralMedium,
    outlineVariant = NeutralMedium.copy(alpha = 0.5f),

    // Inverse colors - For contrast elements
    inversePrimary = BrandGreenLight,
    inverseSurface = NeutralDeep,
    inverseOnSurface = NeutralWhite,

    // Utility colors
    scrim = NeutralBlack.copy(alpha = Alpha.Overlay),
    surfaceTint = BrandGreen
)

// ============= THEME COMPOSABLE =============
@Composable
fun GrowLoopTheme(
    content: @Composable () -> Unit
) {
    val customColors = GrowLoopColors()

    CompositionLocalProvider(
        LocalGrowLoopColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = GrowLoopColorScheme,
            typography = Typography,
            content = content
        )
    }
}

// ============= THEME EXTENSIONS =============
// Extension property to access custom colors easily
val MaterialTheme.growLoopColors: GrowLoopColors
    @Composable get() = LocalGrowLoopColors.current

// ============= THEME UTILITIES =============
// Helper object for common theme-related functions
object ThemeUtils {
    /**
     * Get category color based on category type
     */
    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "baby" -> CategoryBaby
            "toddler" -> CategoryToddler
            "kids" -> CategoryKids
            "teen" -> CategoryTeen
            "adult" -> CategoryAdult
            else -> BrandPurple
        }
    }

    /**
     * Get condition color based on condition type
     */
    fun getConditionColor(condition: String): Color {
        return when (condition.lowercase()) {
            "excellent", "like new" -> ConditionExcellent
            "good" -> ConditionGood
            "fair" -> ConditionFair
            "worn", "well worn" -> ConditionWorn
            else -> NeutralGray
        }
    }

    /**
     * Get seasonal color based on season
     */
    fun getSeasonalColor(season: String): Color {
        return when (season.lowercase()) {
            "spring" -> SeasonSpring
            "summer" -> SeasonSummer
            "autumn", "fall" -> SeasonAutumn
            "winter" -> SeasonWinter
            else -> BrandGreen
        }
    }

    /**
     * Get progress color based on completion percentage
     */
    @Composable
    fun getProgressColor(progress: Float): Color {
        return when {
            progress >= 1.0f -> MaterialTheme.growLoopColors.progressComplete
            progress >= 0.7f -> MaterialTheme.growLoopColors.progressForeground
            progress >= 0.3f -> WarningOrange
            else -> MaterialTheme.growLoopColors.progressForeground
        }
    }

    /**
     * Get surface elevation color for depth
     */
    fun getSurfaceElevation(level: Int): Color {
        return when (level) {
            0 -> SurfacePrimary
            1 -> SurfaceSecondary
            2 -> SurfaceTertiary
            else -> NeutralLighter
        }
    }
}

// ============= PREVIEW HELPERS =============
// Helper composables for theme previews
@Composable
fun ThemePreview(content: @Composable () -> Unit) {
    GrowLoopTheme(content = content)
}