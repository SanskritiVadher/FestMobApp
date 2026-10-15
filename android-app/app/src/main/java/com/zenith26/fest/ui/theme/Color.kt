package com.zenith26.fest.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Zenith '26 Sketchbook Palette (Strictly NO blue, NO purple, NO violet)
// =========================================================================
val PaperBackground = Color(0xFFFBF6EA)  // page background
val PaperCard = Color(0xFFFFFCF5)        // crisp card surface
val PaperCardBacking = Color(0xFFF4EBD0) // layered paper backing
val InkPrimary = Color(0xFF2B2A28)       // primary ink text & 2px sketch outlines
val InkMuted = Color(0xFF6E6B65)         // secondary/meta text & dates
val InkLight = Color(0xFFE0D8C3)         // subtle sketch dividers & dotted lines
val InkBorder = Color(0xFF2B2A28)        // card and button borders

// Accent & Category Palette
val TomatoPrimary = Color(0xFFE5533D)    // primary buttons, FULL badges, highlights
val TomatoDark = Color(0xFFB83A26)
val TomatoLight = Color(0xFFFDEDE8)

val MustardYellow = Color(0xFFF2B84B)    // Technical category, hero marker highlight, tape
val MustardLight = Color(0xFFFEF7E6)

val CoralPink = Color(0xFFF28B82)        // Cultural category
val CoralLight = Color(0xFFFDEEED)

val SageGreen = Color(0xFF8DB580)        // Sports category, seats < 70% left
val SageLight = Color(0xFFEDF4EB)

val TangerineOrange = Color(0xFFF08A3C)  // Gaming category
val TangerineLight = Color(0xFFFDF2E8)

val ClayWarm = Color(0xFFD9A58F)         // Workshop category
val ClayLight = Color(0xFFF8EFEA)

// =========================================================================
// Backward-compatibility aliases for admin dashboard & legacy components
// (All blues and purples mapped to paper, ink, sage, and tomato)
// =========================================================================
val Brand50 = PaperCardBacking
val Brand100 = PaperCardBacking
val Brand200 = InkLight
val Brand500 = TomatoPrimary
val Brand600 = TomatoPrimary
val Brand700 = TomatoDark
val Brand800 = InkPrimary
val Brand900 = InkPrimary
val Brand950 = InkPrimary

val Rose50 = TomatoLight
val Rose100 = TomatoLight
val Rose200 = CoralPink
val Rose500 = TomatoPrimary
val Rose600 = TomatoPrimary
val Rose700 = TomatoDark
val Rose800 = TomatoDark

val Emerald50 = SageLight
val Emerald100 = SageLight
val Emerald200 = SageGreen
val Emerald600 = SageGreen
val Emerald700 = SageGreen

val Amber50 = MustardLight
val Amber100 = MustardLight
val Amber200 = MustardYellow
val Amber600 = MustardYellow
val Amber700 = TangerineOrange
val Amber800 = TangerineOrange

val Purple50 = MustardLight
val Purple100 = MustardLight
val Purple600 = TomatoPrimary
val Purple900 = InkPrimary

val Slate50 = PaperBackground
val Slate100 = PaperCard
val Slate200 = InkLight
val Slate300 = InkLight
val Slate400 = InkMuted
val Slate500 = InkMuted
val Slate600 = InkPrimary
val Slate700 = InkPrimary
val Slate800 = InkPrimary
val Slate900 = InkPrimary

