package com.scelio.brainest.designsystem

import androidx.compose.ui.graphics.Color

// --- Brand Colors (From PIT Studio Image) ---
val BrainestPrimary = Color(0xFF19C472)           // Primary 700
val BrainestPrimaryLight = Color(0xFFD7F8EC)      // Primary 200
val BrainestPrimaryLighter = Color(0xFFEDFCF6)    // Primary 100

val BrainestSecondary = Color(0xFFFF8B2D)         // Secondary 500 (Orange)
val BrainestSecondaryLight = Color(0xFFFFF7F0)    // Secondary 100

// --- Support & Background ---
val BrainestBG1 = Color(0xFF2F6846)               // Support BG1 (Deep Green)
val BrainestBackground = Color(0xFFF8FAF7)        // Support BG (Off-white)
val BrainestOverlay = Color(0xA61F1F1F)           // Overlay #1F1F1FA6 (Alpha included)

// --- Neutral Scale ---
val BrainestNeutral500 = Color(0xFF273430)        // Deep Charcoal/Green
val BrainestNeutral400 = Color(0xFF646A69)        // Dark Gray
val BrainestNeutral300 = Color(0xFFA5ADAD)        // Medium Gray
val BrainestNeutral200 = Color(0xFFEAEBED)        // Light Gray
val BrainestNeutral100 = Color(0xFFF4F7F6)        // Extra Light Gray
val BrainestNeutral0 = Color(0xFFF6F8F9)          // Surface/White

// --- Semantic Colors ---
val BrainestSuccess = Color(0xFF19C472)
val BrainestWarning = Color(0xFFFFAA20)           // Warning 500
val BrainestWarningLight = Color(0xFFFBF4DC)      // Warning 100
val BrainestError = Color(0xFFF04E40)             // Danger 500
val BrainestErrorLight = Color(0xFFFDF3F2)        // Danger 100

// --- Primary variations with alpha (Updated to new Primary #19C472) ---
val BrainestPrimaryAlpha10 = Color(0x1A19C472)    // 10% alpha
val BrainestPrimaryAlpha20 = Color(0x3319C472)    // 20% alpha
val BrainestPrimaryAlpha40 = Color(0x6619C472)    // 40% alpha
val BrainestPrimaryAlpha60 = Color(0x9919C472)    // 60% alpha
val BrainestPrimaryAlpha80 = Color(0xCC19C472)    // 80% alpha

// --- Feature Colors (Kept for compatibility, updated where possible) ---
// Note: You can align these to the brand colors above if you want a tighter look.

val BrainestMath = Color(0xFF2F6846)              // Aligned with BG1
val BrainestChat = Color(0xFF19C472)              // Aligned with Primary
val BrainestAskAnything = Color(0xFFFFAA20)       // Aligned with Warning 500
val BrainestDraftEssay = Color(0xFF646A69)        // Aligned with Neutral 400
val BrainestTranslate = Color(0xFF19C472)         // Aligned with Primary

// --- Surface & Text (Using Neutral Scale) ---
val BrainestSurfaceLight = BrainestNeutral0
val BrainestSurfaceDark = BrainestNeutral500

val BrainestTextPrimaryLight = BrainestNeutral500
val BrainestTextPrimaryDark = BrainestNeutral0

val BrainestTextSecondaryLight = BrainestNeutral400
val BrainestTextSecondaryDark = BrainestNeutral200

val BrainestDividerLight = Color(0x14273430)      // 8% Neutral 500
val BrainestDividerDark = Color(0x29F6F8F9)       // 16% Neutral 0