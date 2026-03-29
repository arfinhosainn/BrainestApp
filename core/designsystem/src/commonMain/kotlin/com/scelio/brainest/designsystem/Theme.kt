package com.scelio.brainest.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// Extended Colors for Brainest
// ============================================================================
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val primaryPressed: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val surfaceCard: Color,
    val overlay: Color,

    // Chat bubble backgrounds (from user messages)
    val chatUserBubble: Color,
    val chatAIBubble: Color,

    // Feature colors (for icons and accents)
    val featureMath: Color,
    val featureChat: Color,
    val featureSummarize: Color,
    val featureAskAnything: Color,
    val featureDraftEssay: Color,
    val featureChangeTone: Color,
    val featureTranslate: Color,
    val featureFixGrammar: Color,
    val featureParaphrase: Color,
    val featureImproveText: Color,

    // Accent colors with alpha
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentMint: Color,
    val accentGray: Color,
)

// ============================================================================
// Light Theme Extended Colors
// ============================================================================
val LightExtendedColors = ExtendedColors(
    // Button states
    primaryHover = BrainestPrimaryAlpha80,
    primaryPressed = BrainestPrimaryAlpha60,
    destructiveHover = BrainestError,
    destructiveSecondaryOutline = BrainestErrorLight,
    disabledOutline = BrainestNeutral300,
    disabledFill = BrainestNeutral200,
    successOutline = BrainestPrimaryLight,
    success = BrainestSuccess,
    onSuccess = BrainestNeutral0,
    secondaryFill = BrainestSecondaryLight,

    // Text variants - Based on your chat screens
    textPrimary = BrainestTextPrimaryLight,
    textSecondary = BrainestTextSecondaryLight,
    textTertiary = BrainestNeutral300,
    textPlaceholder = BrainestNeutral300,
    textDisabled = BrainestNeutral200,

    // Surface variants - Based on your designs
    surfaceLower = BrainestBackground,
    surfaceHigher = BrainestSurfaceLight,
    surfaceOutline = BrainestNeutral200,
    surfaceCard = BrainestNeutral100,
    overlay = BrainestOverlay,

    // Chat bubbles - From your chat screen
    chatUserBubble = BrainestPrimaryLighter,
    chatAIBubble = BrainestSurfaceLight,

    // Feature colors
    featureMath = BrainestMath,
    featureChat = BrainestChat,
    featureSummarize = BrainestSecondary,
    featureAskAnything = BrainestAskAnything,
    featureDraftEssay = BrainestDraftEssay,
    featureChangeTone = BrainestSecondary,
    featureTranslate = BrainestTranslate,
    featureFixGrammar = BrainestWarning,
    featureParaphrase = BrainestPrimary,
    featureImproveText = BrainestSecondary,

    // Accent colors with alpha
    accentBlue = BrainestPrimary,
    accentPurple = BrainestBG1,
    accentViolet = BrainestPrimaryLight,
    accentPink = BrainestSecondaryLight,
    accentOrange = BrainestSecondary,
    accentYellow = BrainestWarning,
    accentGreen = BrainestPrimary,
    accentTeal = BrainestBG1,
    accentMint = BrainestPrimaryLight,
    accentGray = BrainestNeutral300,
)

// ============================================================================
// Dark Theme Extended Colors
// ============================================================================
val DarkExtendedColors = ExtendedColors(
    // Button states
    primaryHover = BrainestPrimaryAlpha80,
    primaryPressed = BrainestPrimaryAlpha60,
    destructiveHover = BrainestError,
    destructiveSecondaryOutline = BrainestError,
    disabledOutline = BrainestNeutral400,
    disabledFill = BrainestNeutral500,
    successOutline = BrainestPrimaryLight,
    success = BrainestSuccess,
    onSuccess = BrainestNeutral0,
    secondaryFill = BrainestBG1,

    // Text variants - From your dark mode chat
    textPrimary = BrainestTextPrimaryDark,
    textSecondary = BrainestTextSecondaryDark,
    textTertiary = BrainestNeutral300,
    textPlaceholder = BrainestNeutral300,
    textDisabled = BrainestNeutral400,

    // Surface variants - Based on dark mode design
    surfaceLower = BrainestNeutral500,
    surfaceHigher = BrainestBG1,
    surfaceOutline = BrainestNeutral400,
    surfaceCard = BrainestBG1,
    overlay = BrainestOverlay,

    // Chat bubbles - From your dark mode chat
    chatUserBubble = BrainestBG1,
    chatAIBubble = BrainestNeutral500,

    // Feature colors (same as light mode but may need adjustment)
    featureMath = BrainestMath,
    featureChat = BrainestChat,
    featureSummarize = BrainestSecondary,
    featureAskAnything = BrainestAskAnything,
    featureDraftEssay = BrainestDraftEssay,
    featureChangeTone = BrainestSecondary,
    featureTranslate = BrainestTranslate,
    featureFixGrammar = BrainestWarning,
    featureParaphrase = BrainestPrimary,
    featureImproveText = BrainestSecondary,

    // Accent colors with alpha
    accentBlue = BrainestPrimary,
    accentPurple = BrainestBG1,
    accentViolet = BrainestPrimaryLight,
    accentPink = BrainestSecondaryLight,
    accentOrange = BrainestSecondary,
    accentYellow = BrainestWarning,
    accentGreen = BrainestPrimary,
    accentTeal = BrainestBG1,
    accentMint = BrainestPrimaryLight,
    accentGray = BrainestNeutral300,
)

// ============================================================================
// Material 3 Light Color Scheme
// ============================================================================
val LightColorScheme = lightColorScheme(
    // Primary colors - Green brand
    primary = BrainestPrimary,
    onPrimary = BrainestNeutral0,
    primaryContainer = BrainestPrimaryLight,
    onPrimaryContainer = BrainestBG1,

    // Secondary colors - Orange
    secondary = BrainestSecondary,
    onSecondary = BrainestNeutral0,
    secondaryContainer = BrainestSecondaryLight,
    onSecondaryContainer = BrainestSecondary,

    // Tertiary colors
    tertiary = BrainestBG1,
    onTertiary = BrainestNeutral0,
    tertiaryContainer = BrainestPrimaryLighter,
    onTertiaryContainer = BrainestBG1,

    // Error colors
    error = BrainestError,
    onError = BrainestNeutral0,
    errorContainer = BrainestErrorLight,
    onErrorContainer = BrainestError,

    // Background & Surface - From your light mode design
    background = BrainestBackground,
    onBackground = BrainestTextPrimaryLight,
    surface = BrainestSurfaceLight,
    onSurface = BrainestTextPrimaryLight,
    surfaceVariant = BrainestNeutral100,
    onSurfaceVariant = BrainestNeutral400,

    // Outline
    outline = BrainestNeutral200,
    outlineVariant = BrainestNeutral100,

    // Scrim
    scrim = BrainestOverlay,

    // Inverse
    inverseSurface = BrainestNeutral500,
    inverseOnSurface = BrainestNeutral0,
    inversePrimary = BrainestPrimary,
)

// ============================================================================
// Material 3 Dark Color Scheme
// ============================================================================
val DarkColorScheme = darkColorScheme(
    // Primary colors - Green brand
    primary = BrainestPrimary,
    onPrimary = BrainestNeutral0,
    primaryContainer = BrainestBG1,
    onPrimaryContainer = BrainestPrimaryLight,

    // Secondary colors
    secondary = BrainestSecondary,
    onSecondary = BrainestNeutral0,
    secondaryContainer = BrainestSecondaryLight,
    onSecondaryContainer = BrainestNeutral500,

    // Tertiary colors
    tertiary = BrainestBG1,
    onTertiary = BrainestNeutral0,
    tertiaryContainer = BrainestBG1,
    onTertiaryContainer = BrainestPrimaryLight,

    // Error colors
    error = BrainestError,
    onError = BrainestNeutral0,
    errorContainer = BrainestError,
    onErrorContainer = BrainestNeutral0,

    // Background & Surface - From your dark mode design
    background = BrainestNeutral500,
    onBackground = BrainestNeutral0,
    surface = BrainestNeutral500,
    onSurface = BrainestNeutral0,
    surfaceVariant = BrainestBG1,
    onSurfaceVariant = BrainestNeutral200,

    // Outline
    outline = BrainestNeutral400,
    outlineVariant = BrainestNeutral500,

    // Scrim
    scrim = BrainestOverlay,

    // Inverse
    inverseSurface = BrainestNeutral100,
    inverseOnSurface = BrainestNeutral500,
    inversePrimary = BrainestPrimary,
)
