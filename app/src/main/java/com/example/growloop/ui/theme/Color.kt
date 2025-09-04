package com.yourpackage.ui.theme

import androidx.compose.ui.graphics.Color

//Color.kt
// ============= PRIMARY BRAND COLORS =============
// Main brand identity - vibrant and modern
val BrandGreen = Color(0xFF22C55E)        // Primary action color - vibrant, trustworthy
val BrandGreenDark = Color(0xFF16A34A)    // Darker shade for depth
val BrandGreenDeep = Color(0xFF15803D)    // Deepest shade for shadows
val BrandGreenLight = Color(0xFF4ADE80)   // Lighter for highlights
val BrandGreenSoft = Color(0xFFA7F3D0)    // Very light for backgrounds

// Secondary brand colors - sophisticated purple/blue
val BrandPurple = Color(0xFF6366F1)       // Secondary actions, modern tech feel
val BrandPurpleLight = Color(0xFF8B5CF6)  // Lighter variant
val BrandPurpleDeep = Color(0xFF7C3AED)   // Deeper variant
val BrandBlue = Color(0xFF3B82F6)         // Information and links
val BrandBlueSoft = Color(0xFF93C5FD)     // Light blue for accents

// ============= NEUTRAL FOUNDATION =============


val NeutralWhite = Color(0xFFFFFFFF)      // Pure white
val NeutralOffWhite = Color(0xFFE9FFE9)    // Warm off-white for backgrounds
val NeutralLight = Color(0xFFF8FAFC)      // Very light gray
val NeutralLighter = Color(0xFFF1F5F9)    // Light gray for surfaces
val NeutralMedium = Color(0xFFE2E8F0)     // Medium gray for borders
val NeutralGray = Color(0xFF94A3B8)       // Text gray for secondary content
val NeutralDark = Color(0xFF475569)       // Dark gray for primary text
val NeutralDeep = Color(0xFF334155)       // Deeper for headings
val NeutralBlack = Color(0xFF1E293B)      // Near black for emphasis

// ============= SEMANTIC COLORS =============
// Status and feedback colors
val SuccessGreen = Color(0xFF10B981)      // Success states
val SuccessLight = Color(0xFFD1FAE5)      // Success backgrounds
val SuccessBorder = Color(0xFFA7F3D0)     // Success borders

val ErrorRed = Color(0xFFEF4444)          // Error states
val ErrorLight = Color(0xFFFEE2E2)        // Error backgrounds
val ErrorBorder = Color(0xFFFECACA)       // Error borders

val WarningOrange = Color(0xFFF59E0B)     // Warning states
val WarningLight = Color(0xFFFEF3C7)      // Warning backgrounds
val WarningBorder = Color(0xFFFDE68A)     // Warning borders

val InfoBlue = Color(0xFF3B82F6)          // Information
val InfoLight = Color(0xFFDBEAFE)         // Info backgrounds
val InfoBorder = Color(0xFFBFDBFE)        // Info borders

// ============= SURFACE & BACKGROUND COLORS =============
// Layered surfaces for depth and hierarchy
val SurfacePrimary = NeutralWhite         // Primary surface (cards, sheets)
val SurfaceSecondary = NeutralLight       // Secondary surfaces
val SurfaceTertiary = NeutralLighter      // Tertiary surfaces
val BackgroundPrimary = NeutralOffWhite   // Main background
val BackgroundSecondary = NeutralLight    // Secondary background

// ============= COMPONENT-SPECIFIC COLORS =============
// Progress and interactive elements
val ProgressBackground = NeutralMedium
val ProgressForeground = BrandGreen
val ProgressComplete = SuccessGreen

// Buttons and interactive elements
val ButtonPrimary = BrandGreen
val ButtonSecondary = BrandPurple
val ButtonDisabled = NeutralGray
val ButtonText = NeutralWhite
val ButtonTextSecondary = NeutralDeep

// Cards and containers
val CardBackground = NeutralWhite
val CardBorder = NeutralMedium
val CardShadow = NeutralBlack

// Input fields
val InputBackground = NeutralLight
val InputBorder = NeutralMedium
val InputBorderFocused = BrandGreen
val InputText = NeutralDeep
val InputPlaceholder = NeutralGray

// ============= GRADIENTS =============
// Modern gradient combinations for visual appeal
object Gradients {
    val PrimaryGreen = listOf(BrandGreen, BrandGreenDark, BrandGreenDeep)
    val SecondaryPurple = listOf(BrandPurple, BrandPurpleLight, BrandPurpleDeep)
    val Success = listOf(SuccessGreen, BrandGreen)
    val Neutral = listOf(NeutralLight, NeutralLighter)
    val Subtle = listOf(NeutralOffWhite, NeutralLight)
    val Background = listOf(BackgroundPrimary, BackgroundSecondary)
}

// ============= CATEGORY COLORS =============
// For different clothing categories with modern palette
val CategoryBaby = Color(0xFFF472B6)      // Soft pink for baby items
val CategoryToddler = Color(0xFFA78BFA)   // Light purple for toddlers
val CategoryKids = Color(0xFF60A5FA)      // Blue for kids
val CategoryTeen = Color(0xFF34D399)      // Teal for teens
val CategoryAdult = Color(0xFF6366F1)     // Purple for adults

// Category background colors (very light versions)
val CategoryBabyBg = CategoryBaby.copy(alpha = 0.1f)
val CategoryToddlerBg = CategoryToddler.copy(alpha = 0.1f)
val CategoryKidsBg = CategoryKids.copy(alpha = 0.1f)
val CategoryTeenBg = CategoryTeen.copy(alpha = 0.1f)
val CategoryAdultBg = CategoryAdult.copy(alpha = 0.1f)

// ============= CONDITION COLORS =============
// For item condition status with clear hierarchy
val ConditionExcellent = Color(0xFF10B981)  // Like new - vibrant green
val ConditionGood = Color(0xFF059669)       // Good - medium green
val ConditionFair = Color(0xFFF59E0B)       // Fair - amber
val ConditionWorn = Color(0xFFEF4444)       // Worn - red

// Condition background colors
val ConditionExcellentBg = Color(0xFFD1FAE5)
val ConditionGoodBg = Color(0xFFA7F3D0)
val ConditionFairBg = Color(0xFFFEF3C7)
val ConditionWornBg = Color(0xFFFEE2E2)

// ============= SEASONAL COLORS =============
// Fresh seasonal palette for variety
val SeasonSpring = Color(0xFF22C55E)      // Fresh green
val SeasonSummer = Color(0xFFFBBF24)      // Sunny yellow
val SeasonAutumn = Color(0xFFF97316)      // Warm orange
val SeasonWinter = Color(0xFF3B82F6)      // Cool blue

// ============= ALPHA VARIANTS =============
// Commonly used transparency levels
object Alpha {
    const val Disabled = 0.38f
    const val Medium = 0.6f
    const val Light = 0.12f
    const val VeryLight = 0.05f
    const val Overlay = 0.8f
    const val Shadow = 0.15f
}