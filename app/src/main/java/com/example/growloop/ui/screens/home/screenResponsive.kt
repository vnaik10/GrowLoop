package com.example.growloop.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun getResponsiveDimensions(): ResponsiveDimensions {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // Create size categories based on screen dimensions, not font scale
    val sizeCategory = when {
        screenWidthDp < 360.dp -> ResponsiveSizeCategory.EXTRA_SMALL
        screenWidthDp < 400.dp -> ResponsiveSizeCategory.SMALL
        screenWidthDp < 600.dp -> ResponsiveSizeCategory.MEDIUM
        screenWidthDp < 840.dp -> ResponsiveSizeCategory.LARGE
        else -> ResponsiveSizeCategory.EXTRA_LARGE
    }

    return ResponsiveDimensions(
        sizeCategory = sizeCategory,
        screenWidth = screenWidthDp,
        screenHeight = screenHeightDp
    )
}

enum class ResponsiveSizeCategory {
    EXTRA_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE
}

data class ResponsiveDimensions(
    val sizeCategory: ResponsiveSizeCategory,
    val screenWidth: Dp,
    val screenHeight: Dp
) {
    // Fixed text sizes that don't scale with system font
    val titleLarge: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 18.sp
        ResponsiveSizeCategory.SMALL -> 20.sp
        ResponsiveSizeCategory.MEDIUM -> 22.sp
        ResponsiveSizeCategory.LARGE -> 24.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 26.sp
    }

    val titleMedium: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 14.sp
        ResponsiveSizeCategory.SMALL -> 16.sp
        ResponsiveSizeCategory.MEDIUM -> 18.sp
        ResponsiveSizeCategory.LARGE -> 20.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 22.sp
    }

    val titleSmall: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 12.sp
        ResponsiveSizeCategory.SMALL -> 14.sp
        ResponsiveSizeCategory.MEDIUM -> 16.sp
        ResponsiveSizeCategory.LARGE -> 18.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 20.sp
    }

    val bodyLarge: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 14.sp
        ResponsiveSizeCategory.SMALL -> 16.sp
        ResponsiveSizeCategory.MEDIUM -> 18.sp
        ResponsiveSizeCategory.LARGE -> 20.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 22.sp
    }

    val bodyMedium: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 12.sp
        ResponsiveSizeCategory.SMALL -> 14.sp
        ResponsiveSizeCategory.MEDIUM -> 16.sp
        ResponsiveSizeCategory.LARGE -> 18.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 20.sp
    }

    val bodySmall: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 10.sp
        ResponsiveSizeCategory.SMALL -> 12.sp
        ResponsiveSizeCategory.MEDIUM -> 14.sp
        ResponsiveSizeCategory.LARGE -> 16.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 18.sp
    }

    val labelSmall: TextUnit = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 8.sp
        ResponsiveSizeCategory.SMALL -> 10.sp
        ResponsiveSizeCategory.MEDIUM -> 12.sp
        ResponsiveSizeCategory.LARGE -> 14.sp
        ResponsiveSizeCategory.EXTRA_LARGE -> 16.sp
    }

    // Responsive spacing
    val spacingXS: Dp = (screenWidth * 0.01f).coerceIn(2.dp, 4.dp)
    val spacingS: Dp = (screenWidth * 0.02f).coerceIn(4.dp, 8.dp)
    val spacingM: Dp = (screenWidth * 0.03f).coerceIn(8.dp, 16.dp)
    val spacingL: Dp = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)
    val spacingXL: Dp = (screenWidth * 0.05f).coerceIn(16.dp, 32.dp)
    val spacingXXL: Dp = (screenWidth * 0.06f).coerceIn(20.dp, 40.dp)

    // Responsive icon sizes
    val iconXS: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 12.dp
        ResponsiveSizeCategory.SMALL -> 16.dp
        ResponsiveSizeCategory.MEDIUM -> 18.dp
        ResponsiveSizeCategory.LARGE -> 20.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 24.dp
    }

    val iconS: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 16.dp
        ResponsiveSizeCategory.SMALL -> 20.dp
        ResponsiveSizeCategory.MEDIUM -> 24.dp
        ResponsiveSizeCategory.LARGE -> 28.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 32.dp
    }

    val iconM: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 24.dp
        ResponsiveSizeCategory.SMALL -> 28.dp
        ResponsiveSizeCategory.MEDIUM -> 32.dp
        ResponsiveSizeCategory.LARGE -> 36.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 40.dp
    }

    val iconL: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 32.dp
        ResponsiveSizeCategory.SMALL -> 36.dp
        ResponsiveSizeCategory.MEDIUM -> 40.dp
        ResponsiveSizeCategory.LARGE -> 48.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 56.dp
    }

    // Responsive corner radius
    val cornerRadiusS: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 8.dp
        ResponsiveSizeCategory.SMALL -> 12.dp
        ResponsiveSizeCategory.MEDIUM -> 16.dp
        ResponsiveSizeCategory.LARGE -> 20.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 24.dp
    }

    val cornerRadiusM: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 12.dp
        ResponsiveSizeCategory.SMALL -> 16.dp
        ResponsiveSizeCategory.MEDIUM -> 20.dp
        ResponsiveSizeCategory.LARGE -> 24.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 28.dp
    }

    val cornerRadiusL: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 16.dp
        ResponsiveSizeCategory.SMALL -> 20.dp
        ResponsiveSizeCategory.MEDIUM -> 24.dp
        ResponsiveSizeCategory.LARGE -> 28.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 32.dp
    }

    // Card dimensions
    val quickActionCardHeight: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 100.dp
        ResponsiveSizeCategory.SMALL -> 120.dp
        ResponsiveSizeCategory.MEDIUM -> 140.dp
        ResponsiveSizeCategory.LARGE -> 160.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 180.dp
    }

    val impactCardWidth: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 140.dp
        ResponsiveSizeCategory.SMALL -> 160.dp
        ResponsiveSizeCategory.MEDIUM -> 180.dp
        ResponsiveSizeCategory.LARGE -> 200.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 220.dp
    }

    val impactCardHeight: Dp = when (sizeCategory) {
        ResponsiveSizeCategory.EXTRA_SMALL -> 120.dp
        ResponsiveSizeCategory.SMALL -> 140.dp
        ResponsiveSizeCategory.MEDIUM -> 160.dp
        ResponsiveSizeCategory.LARGE -> 180.dp
        ResponsiveSizeCategory.EXTRA_LARGE -> 200.dp
    }

    // Horizontal padding based on screen width percentage
    val horizontalPadding: Dp = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)
}