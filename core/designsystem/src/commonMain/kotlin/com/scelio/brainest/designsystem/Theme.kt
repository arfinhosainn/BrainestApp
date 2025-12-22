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
    primaryHover = Color(0xFFD64F23),          // Darker orange for hover
    primaryPressed = Color(0xFFC24620),         // Even darker for pressed
    destructiveHover = BrainestErrorDark,
    destructiveSecondaryOutline = BrainestErrorLight,
    disabledOutline = BrainestBase300,
    disabledFill = BrainestBase200,
    successOutline = BrainestSuccessLight,
    success = BrainestSuccess,
    onSuccess = BrainestBase0,
    secondaryFill = BrainestSecondary,          // Light beige for secondary buttons

    // Text variants - Based on your chat screens
    textPrimary = BrainestBase1000,             // Pure black for primary text
    textSecondary = BrainestBase700,            // Medium gray for secondary text
    textTertiary = BrainestBase600,             // Lighter gray for tertiary text
    textPlaceholder = BrainestBase500,          // Placeholder text
    textDisabled = BrainestBase400,             // Disabled text

    // Surface variants - Based on your designs
    surfaceLower = Color(0xFFF5F5F5),           // Light gray background
    surfaceHigher = BrainestBase0,              // White for elevated cards
    surfaceOutline = BrainestBase300,           // Light gray outline
    surfaceCard = BrainestTertiary,             // Very light beige for cards
    overlay = BrainestBase1000Alpha80,          // 80% black overlay

    // Chat bubbles - From your chat screen
    chatUserBubble = BrainestBase0,          // Light beige for user messages
    chatAIBubble = BrainestBase0,               // White for AI messages

    // Feature colors
    featureMath = BrainestMath,
    featureChat = BrainestChat,
    featureSummarize = BrainestSummarize,
    featureAskAnything = BrainestAskAnything,
    featureDraftEssay = BrainestDraftEssay,
    featureChangeTone = BrainestChangeTone,
    featureTranslate = BrainestTranslate,
    featureFixGrammar = BrainestFixGrammar,
    featureParaphrase = BrainestParaphrase,
    featureImproveText = BrainestImproveText,

    // Accent colors with alpha
    accentBlue = BrainestAccentBlue,
    accentPurple = BrainestAccentPurple,
    accentViolet = BrainestAccentViolet,
    accentPink = BrainestAccentPink,
    accentOrange = BrainestAccentOrange,
    accentYellow = BrainestAccentYellow,
    accentGreen = BrainestAccentGreen,
    accentTeal = BrainestAccentTeal,
    accentMint = BrainestAccentMint,
    accentGray = BrainestAccentGray,
)

// ============================================================================
// Dark Theme Extended Colors
// ============================================================================
val DarkExtendedColors = ExtendedColors(
    // Button states
    primaryHover = Color(0xFFFF6B3D),           // Lighter orange for hover in dark
    primaryPressed = Color(0xFFFF8559),         // Even lighter for pressed
    destructiveHover = BrainestErrorDark,
    destructiveSecondaryOutline = BrainestErrorLight,
    disabledOutline = BrainestBase800,
    disabledFill = BrainestBase900,
    successOutline = BrainestSuccessDark,
    success = BrainestSuccess,
    onSuccess = BrainestBase1000,
    secondaryFill = BrainestBase800,            // Dark gray for secondary buttons

    // Text variants - From your dark mode chat
    textPrimary = BrainestBase0,                // White for primary text
    textSecondary = BrainestBase400,            // Medium gray for secondary
    textTertiary = BrainestBase500,             // Lighter gray for tertiary
    textPlaceholder = BrainestBase600,          // Placeholder text
    textDisabled = BrainestBase700,             // Disabled text

    // Surface variants - Based on dark mode design
    surfaceLower = BrainestBase1000,            // Pure black background
    surfaceHigher = BrainestBase900,            // Dark gray for elevated surfaces
    surfaceOutline = BrainestBase800,           // Outline color
    surfaceCard = Color(0xFF1C1C1E),            // Card background (iOS dark surface)
    overlay = BrainestBase1000Alpha80,          // 80% black overlay

    // Chat bubbles - From your dark mode chat
    chatUserBubble = Color(0xFF2C2C2E),         // Dark gray for user messages
    chatAIBubble = Color(0xFF1C1C1E),           // Slightly lighter for AI messages

    // Feature colors (same as light mode but may need adjustment)
    featureMath = BrainestMath,
    featureChat = BrainestChat,
    featureSummarize = BrainestSummarize,
    featureAskAnything = BrainestAskAnything,
    featureDraftEssay = BrainestDraftEssay,
    featureChangeTone = BrainestChangeTone,
    featureTranslate = BrainestTranslate,
    featureFixGrammar = BrainestFixGrammar,
    featureParaphrase = BrainestParaphrase,
    featureImproveText = BrainestImproveText,

    // Accent colors with alpha
    accentBlue = BrainestAccentBlue,
    accentPurple = BrainestAccentPurple,
    accentViolet = BrainestAccentViolet,
    accentPink = BrainestAccentPink,
    accentOrange = BrainestAccentOrange,
    accentYellow = BrainestAccentYellow,
    accentGreen = BrainestAccentGreen,
    accentTeal = BrainestAccentTeal,
    accentMint = BrainestAccentMint,
    accentGray = BrainestAccentGray,
)

// ============================================================================
// Material 3 Light Color Scheme
// ============================================================================
val LightColorScheme = lightColorScheme(
    // Primary colors - Orange brand
    primary = BrainestPrimary,
    onPrimary = BrainestBase0,
    primaryContainer = BrainestPrimaryAlpha10,
    onPrimaryContainer = BrainestPrimary,

    // Secondary colors - Beige
    secondary = BrainestBase600,
    onSecondary = BrainestBase0,
    secondaryContainer = BrainestSecondary,
    onSecondaryContainer = BrainestBase900,

    // Tertiary colors
    tertiary = BrainestPrimary,
    onTertiary = BrainestBase0,
    tertiaryContainer = BrainestTertiary,
    onTertiaryContainer = BrainestBase1000,

    // Error colors
    error = BrainestError,
    onError = BrainestBase0,
    errorContainer = BrainestErrorLight,
    onErrorContainer = BrainestErrorDark,

    // Background & Surface - From your light mode design
    background = BrainestTertiary,             // Light gray background
    onBackground = BrainestBase1000,
    surface = BrainestBase0,                    // White surface
    onSurface = BrainestBase1000,
    surfaceVariant = BrainestTertiary,          // Light beige
    onSurfaceVariant = BrainestBase800,

    // Outline
    outline = BrainestBase300,
    outlineVariant = BrainestBase200,

    // Scrim
    scrim = BrainestBase1000Alpha80,

    // Inverse
    inverseSurface = BrainestBase900,
    inverseOnSurface = BrainestBase0,
    inversePrimary = BrainestPrimary,
)

// ============================================================================
// Material 3 Dark Color Scheme
// ============================================================================
val DarkColorScheme = darkColorScheme(
    // Primary colors - Orange brand
    primary = BrainestPrimary,
    onPrimary = BrainestBase0,
    primaryContainer = BrainestPrimaryAlpha20,
    onPrimaryContainer = BrainestPrimary,

    // Secondary colors
    secondary = BrainestBase500,
    onSecondary = BrainestBase1000,
    secondaryContainer = BrainestBase800,
    onSecondaryContainer = BrainestBase200,

    // Tertiary colors
    tertiary = BrainestPrimary,
    onTertiary = BrainestBase1000,
    tertiaryContainer = BrainestBase800,
    onTertiaryContainer = BrainestPrimary,

    // Error colors
    error = BrainestError,
    onError = BrainestBase0,
    errorContainer = BrainestErrorDark,
    onErrorContainer = BrainestErrorLight,

    // Background & Surface - From your dark mode design
    background = BrainestBase1000,              // Pure black background
    onBackground = BrainestBase0,
    surface = Color(0xFF1C1C1E),                // Dark gray surface (iOS style)
    onSurface = BrainestBase0,
    surfaceVariant = Color(0xFF2C2C2E),         // Elevated surface
    onSurfaceVariant = BrainestBase300,

    // Outline
    outline = BrainestBase700,
    outlineVariant = BrainestBase800,

    // Scrim
    scrim = BrainestBase1000Alpha80,

    // Inverse
    inverseSurface = BrainestBase100,
    inverseOnSurface = BrainestBase1000,
    inversePrimary = BrainestPrimary,
)
