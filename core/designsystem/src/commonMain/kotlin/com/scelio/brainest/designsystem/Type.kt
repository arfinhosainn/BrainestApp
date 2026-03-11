package com.scelio.brainest.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.bricolage_grotesq_bold
import brainest.core.designsystem.generated.resources.bricolage_grotesq_extra_bold
import brainest.core.designsystem.generated.resources.bricolage_grotesq_medium
import brainest.core.designsystem.generated.resources.bricolage_grotesq_regular
import brainest.core.designsystem.generated.resources.bricolage_grotesq_semi_bold
import org.jetbrains.compose.resources.Font


val BricolageGrotesq
    @Composable get() = FontFamily(
        Font(Res.font.bricolage_grotesq_regular, FontWeight.Normal),
        Font(Res.font.bricolage_grotesq_medium, FontWeight.Medium),
        Font(Res.font.bricolage_grotesq_semi_bold, FontWeight.SemiBold),
        Font(Res.font.bricolage_grotesq_bold, FontWeight.Bold),
        Font(Res.font.bricolage_grotesq_extra_bold, FontWeight.ExtraBold),
    )




val Typography
    @Composable
    get() = Typography(

        displayLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            lineHeight = 41.sp
        ),

        // Title 1 — 28 / 34
        titleLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp
        ),

        // Title 2 — 22 / 28
        titleMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),

        // Title 3 — 20 / 25
        titleSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 25.sp
        ),

        // Headline — 17 / 22
        headlineSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 22.sp
        ),

        // Body — 17 / 22
        bodyLarge = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 22.sp
        ),

        bodyMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 21.sp
        ),

        bodySmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ),

        labelMedium = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),

        labelSmall = TextStyle(
            fontFamily = BricolageGrotesq,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
    )


val Typography.caption2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = BricolageGrotesq,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 13.sp
    )
