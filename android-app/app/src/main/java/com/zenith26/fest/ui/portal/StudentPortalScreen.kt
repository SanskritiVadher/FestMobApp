package com.zenith26.fest.ui.portal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.EventItem
import com.zenith26.fest.ui.theme.*

// Helper: Category colors matching the sketchbook palette
fun getCategoryColor(category: String): Color {
    return when (category.lowercase().trim()) {
        "technical" -> MustardYellow
        "cultural" -> CoralPink
        "sports" -> SageGreen
        "gaming" -> TangerineOrange
        "workshop" -> ClayWarm
        else -> MustardYellow
    }
}

// Helper: Format date strings matching "Sat, 14 Nov · 10:00 AM"
fun formatEventDateTime(dateStr: String?, timeStr: String?): String {
    if (dateStr.isNullOrBlank()) return timeStr ?: ""
    val parts = dateStr.split("-")
    if (parts.size == 3) {
        val month = parts[1].toIntOrNull() ?: 11
        val day = parts[2].toIntOrNull() ?: 14
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthName = monthNames.getOrElse(month - 1) { "Nov" }
        val dayName = when (dateStr) {
            "2026-11-14" -> "Sat"
            "2026-11-15" -> "Sun"
            else -> ""
        }
        val dateFormatted = if (dayName.isNotEmpty()) "$dayName, $day $monthName" else "$day $monthName"
        return if (!timeStr.isNullOrBlank()) "$dateFormatted · $timeStr" else dateFormatted
    }
    return if (!timeStr.isNullOrBlank()) "$dateStr · $timeStr" else dateStr
}

@Composable
fun StudentPortalScreen(
    viewModel: StudentPortalViewModel,
    onNavigateToAdmin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedEventForModal by viewModel.selectedEventForModal.collectAsState()
    val registrationState by viewModel.registrationState.collectAsState()

    val categories = listOf(
        "All" to "All",
        "Technical" to "Technical",
        "Cultural" to "Cultural",
        "Sports" to "Sports",
        "Gaming" to "Gaming",
        "Workshop" to "Workshop"
    )

    Scaffold(
        topBar = {
            ZenithTopAppBar(
                currentScreen = "Events",
                onEventsClick = { /* Already on events */ },
                onAdminClick = onNavigateToAdmin
            )
        },
        containerColor = PaperBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hand-Drawn Hero Section
            HeroSection()

            Spacer(modifier = Modifier.height(12.dp))

            // Sticky / Prominent Toolbar (Search & Category Chips)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Input with 2px Ink Outline
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PaperCard,
                    border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .sketchShadow(offsetX = 3.dp, offsetY = 3.dp, radius = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = InkMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = {
                                Text(
                                    "Search events by title...",
                                    fontSize = 14.sp,
                                    color = InkMuted
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = TomatoPrimary,
                                focusedTextColor = InkPrimary,
                                unfocusedTextColor = InkPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Chips as "Stickers"
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (key, label) ->
                        val isSelected = selectedCategory == key
                        val chipColor = if (isSelected) TomatoPrimary else PaperCard
                        val chipRotation = if (isSelected) -2f else 0f
                        val chipShadow = if (isSelected) 3.5.dp else 2.dp

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = chipColor,
                            border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
                            modifier = Modifier
                                .rotate(chipRotation)
                                .sketchShadow(offsetX = chipShadow, offsetY = chipShadow, radius = 14.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setCategory(key) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else InkPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content Area: Loading / Empty / Error / Day Grouped Cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (val state = uiState) {
                    is PortalUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Tiny Dragon mascot for loading state
                                Box(modifier = Modifier.size(70.dp)) {
                                    SvgCharacter(
                                        assetName = "character5.svg",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                CircularProgressIndicator(color = TomatoPrimary, strokeWidth = 2.5.dp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Loading fest sketchbook...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = InkMuted
                                )
                            }
                        }
                    }

                    is PortalUiState.Empty -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PaperCard,
                            border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                                .sketchShadow(offsetX = 4.dp, offsetY = 4.dp, radius = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                // Scarecrow Mascot with Megaphone for Empty State
                                Box(modifier = Modifier.size(80.dp)) {
                                    SvgCharacter(
                                        assetName = "character6.svg",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No events match your criteria",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = InkPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Try adjusting your search query or selecting a different category sticker.",
                                    fontSize = 12.sp,
                                    color = InkMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    is PortalUiState.Error -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = TomatoLight,
                            border = androidx.compose.foundation.BorderStroke(2.dp, TomatoPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .sketchShadow(offsetX = 3.dp, offsetY = 3.dp, color = TomatoDark, radius = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text("⚠️", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Failed to load events",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TomatoDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.message,
                                    fontSize = 12.sp,
                                    color = TomatoPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.loadEvents() },
                                    colors = ButtonDefaults.buttonColors(containerColor = TomatoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder)
                                ) {
                                    Text("Try Again", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    is PortalUiState.Success -> {
                        val events = state.events

                        // Display grouping: Day 1 (2026-11-14) and Day 2 (2026-11-15)
                        val day1Events = remember(events) {
                            events.filter { it.eventDate == "2026-11-14" }.sortedBy { it.eventTime }
                        }
                        val day2Events = remember(events) {
                            events.filter { it.eventDate == "2026-11-15" }.sortedBy { it.eventTime }
                        }
                        val otherEvents = remember(events) {
                            events.filter { it.eventDate != "2026-11-14" && it.eventDate != "2026-11-15" }.sortedBy { it.eventTime }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                            // Day 1 Section
                            if (day1Events.isNotEmpty()) {
                                DayBanner(
                                    title = "Day 1 — Sat 14 Nov",
                                    count = day1Events.size,
                                    accentColor = MustardYellow
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    day1Events.forEach { event ->
                                        SketchEventCard(
                                            event = event,
                                            onViewDetails = { viewModel.openEventDetails(event) }
                                        )
                                    }
                                }
                            }

                            // Day 2 Section
                            if (day2Events.isNotEmpty()) {
                                DayBanner(
                                    title = "Day 2 — Sun 15 Nov",
                                    count = day2Events.size,
                                    accentColor = CoralPink
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    day2Events.forEach { event ->
                                        SketchEventCard(
                                            event = event,
                                            onViewDetails = { viewModel.openEventDetails(event) }
                                        )
                                    }
                                }
                            }

                            // Other Sessions (if any)
                            if (otherEvents.isNotEmpty()) {
                                DayBanner(
                                    title = "Special Sessions & Workshops",
                                    count = otherEvents.size,
                                    accentColor = ClayWarm
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    otherEvents.forEach { event ->
                                        SketchEventCard(
                                            event = event,
                                            onViewDetails = { viewModel.openEventDetails(event) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer with Skating Penguin loop
            SkatingPenguinFooter()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Event Registration Modal Dialog
    if (selectedEventForModal != null) {
        SketchbookRegistrationDialog(
            event = selectedEventForModal!!,
            formState = registrationState,
            onNameChange = viewModel::onStudentNameChanged,
            onRollChange = viewModel::onRollNumberChanged,
            onEmailChange = viewModel::onEmailChanged,
            onPhoneChange = viewModel::onPhoneChanged,
            onSubmit = viewModel::submitRegistration,
            onDismiss = viewModel::closeEventModal
        )
    }
}

/**
 * Top App Bar with Hand-Drawn Sketchbook styling.
 */
@Composable
fun ZenithTopAppBar(
    currentScreen: String,
    onEventsClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        color = PaperBackground,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Wordmark Logo: "⚡ Zenith '26"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onEventsClick() }
            ) {
                Text("⚡", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Zenith '26",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = InkPrimary,
                    letterSpacing = (-0.5).sp
                )
            }

            // Navigation Link: Admin Dashboard
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentScreen == "Admin") MustardYellow else PaperCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                modifier = Modifier
                    .sketchShadow(offsetX = 2.dp, offsetY = 2.dp, radius = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAdminClick() }
            ) {
                Text(
                    text = "Admin Dashboard →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = InkPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Hero Section featuring:
 * 1. Rotated ticket-stub badge: "🎟️ 14–15 NOV 2026"
 * 2. Main title with mustard marker highlight behind "Zenith '26"
 * 3. Cartoon characters flanking left and right
 * 4. Acrobatic Cartoon Mascot cartwheeling and somersaulting along the center track!
 */
@Composable
fun HeroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PaperBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rotated Hand-Drawn Ticket-Stub Badge
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = PaperCard,
            border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
            modifier = Modifier
                .rotate(-1.5f)
                .sketchShadow(offsetX = 2.5.dp, offsetY = 2.5.dp, radius = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("🎟️", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "14–15 NOV 2026",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkPrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Title Row flanked by Left and Right Cartoon Characters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Character: Scheming Cat chasing mouse
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset(x = (-4).dp)
            ) {
                SvgCharacter(
                    assetName = "character1.svg",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Center Column: Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ignite Your Spark",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = InkPrimary,
                    textAlign = TextAlign.Center
                )

                // Title with Marker Highlight behind "Zenith '26"
                Box(
                    modifier = Modifier.padding(top = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(20.dp)
                            .background(MustardYellow.copy(alpha = 0.55f), RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = "at Zenith '26",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = InkPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Right Character: Tiny Dragon blowing star
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 4.dp)
            ) {
                SvgCharacter(
                    assetName = "character5.svg",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tagline Subtitle
        Text(
            text = "48 Hours of Energy, Innovation & Rivalry across Campus Auditoriums.",
            fontSize = 11.5.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Acrobatic Mascot Routine on Center Chalk Track
        AcrobaticHeroTrack(modifier = Modifier.fillMaxWidth())
    }
}

/**
 * Day Banner: "Day 1 — Sat 14 Nov" with count pill and hand-drawn doodle underline.
 */
@Composable
fun DayBanner(
    title: String,
    count: Int,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📅", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = InkPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = accentColor,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                modifier = Modifier.sketchShadow(offsetX = 2.dp, offsetY = 2.dp, radius = 6.dp)
            ) {
                Text(
                    text = "$count EVENTS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Hand-drawn underline
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(3.dp)
                .background(accentColor, RoundedCornerShape(2.dp))
        )
    }
}

/**
 * Hand-Drawn Sketchbook Event Card:
 * - 2px ink outline
 * - Hard offset drop shadow (4px 4px 0 #2B2A28)
 * - Layered paper backing peeking out behind
 * - Tape strip doodle at top center
 * - Category badge in category color
 * - Free/Fee badge
 * - "FULL" rubber stamp when at capacity
 * - Formatted date "Sat, 14 Nov · 10:00 AM"
 * - Hand-drawn seat progress bar
 * - "✏️ Register" button
 */
@Composable
fun SketchEventCard(
    event: EventItem,
    onViewDetails: () -> Unit
) {
    val isFull = event.availableSeats <= 0
    val feeText = if (event.fee == 0) "Free" else "₹${event.fee}"
    val categoryColor = getCategoryColor(event.category)

    // Calculate seat utilization percentage
    val capacity = if (event.capacity > 0) event.capacity else 100
    val seatsLeft = event.availableSeats.coerceAtLeast(0)
    val usedSeats = (capacity - seatsLeft).coerceAtLeast(0)
    val utilization = (usedSeats.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)

    val progressColor = when {
        utilization >= 0.9f -> TomatoPrimary
        utilization >= 0.7f -> MustardYellow
        else -> SageGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Layered Paper Backing peeking out behind card
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 4.dp)
                .rotate(1.5f)
                .background(categoryColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .border(2.dp, InkBorder, RoundedCornerShape(14.dp))
        )

        // Main Card Surface
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = PaperCard,
            border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .sketchShadow(offsetX = 4.dp, offsetY = 4.dp, radius = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Category top tinted accent band
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(categoryColor)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Top Row: Icon, Category Badge, Title, Fee/FULL badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PaperBackground,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(event.icon?.ifBlank { "⚡" } ?: "⚡", fontSize = 20.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                // Category Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = categoryColor,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder)
                                ) {
                                    Text(
                                        text = event.category,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = InkPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = event.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Fee or FULL badge
                        if (isFull) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TomatoPrimary,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                                modifier = Modifier
                                    .rotate(-4f)
                                    .sketchShadow(offsetX = 2.dp, offsetY = 2.dp, radius = 6.dp)
                            ) {
                                Text(
                                    text = "FULL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PaperCard,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                                modifier = Modifier.sketchShadow(offsetX = 2.dp, offsetY = 2.dp, radius = 6.dp)
                            ) {
                                Text(
                                    text = feeText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description text
                    Text(
                        text = event.description?.ifBlank { "Join this exciting fest competition!" }
                            ?: "Join this exciting fest competition!",
                        fontSize = 12.sp,
                        color = InkMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips: Date & Venue in Sketchbook boxes
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PaperBackground,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, InkLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📅", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatEventDateTime(event.eventDate, event.eventTime),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = InkPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📍", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = event.venue,
                                    fontSize = 11.5.sp,
                                    color = InkMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Seat Utilization Hand-Drawn Progress Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SEATS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                            Text(
                                text = if (isFull) "0 left" else "$seatsLeft left",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFull) TomatoPrimary else InkPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Progress track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .background(PaperBackground, RoundedCornerShape(4.dp))
                                .border(1.2.dp, InkBorder, RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(utilization)
                                    .fillMaxHeight()
                                    .background(progressColor, RoundedCornerShape(4.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Button: "✏️ Register"
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isFull) PaperCardBacking else TomatoPrimary,
                        border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .sketchShadow(offsetX = 3.dp, offsetY = 3.dp, radius = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = !isFull) { onViewDetails() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isFull) "Event is Full" else "✏️ Register",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFull) InkMuted else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Tape Strip Doodle on Top Center Edge
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(10.dp)
                .offset(y = (-3).dp)
                .align(Alignment.TopCenter)
                .background(MustardYellow.copy(alpha = 0.85f), RoundedCornerShape(2.dp))
                .border(1.dp, InkBorder.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
        )
    }
}

/**
 * Sketchbook Paper Modal Dialog for Event Details & Registration.
 */
@Composable
fun SketchbookRegistrationDialog(
    event: EventItem,
    formState: RegistrationFormState,
    onNameChange: (String) -> Unit,
    onRollChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val isFull = event.availableSeats <= 0
    val categoryColor = getCategoryColor(event.category)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaperCard,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, InkBorder),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .sketchShadow(offsetX = 6.dp, offsetY = 6.dp, radius = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with category accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(categoryColor)
                        .border(
                            width = 1.dp,
                            color = InkBorder,
                            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(event.icon?.ifBlank { "⚡" } ?: "⚡", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    event.category.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = InkPrimary
                                )
                                Text(
                                    event.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = InkPrimary)
                        }
                    }
                }

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Meta Details Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PaperBackground,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, InkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                SketchMetaField("Date & Time", formatEventDateTime(event.eventDate, event.eventTime), Modifier.weight(1f))
                                SketchMetaField("Venue", event.venue, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                SketchMetaField(
                                    "Entry Fee",
                                    if (event.fee == 0) "Free" else "₹${event.fee}",
                                    Modifier.weight(1f)
                                )
                                SketchMetaField(
                                    "Seats Left",
                                    if (isFull) "FULL (0 / ${event.capacity})" else "${event.availableSeats} of ${event.capacity} left",
                                    Modifier.weight(1f),
                                    valueColor = if (isFull) TomatoPrimary else InkPrimary
                                )
                            }
                        }
                    }

                    // About section
                    Column {
                        Text(
                            "ABOUT THIS EVENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            event.description?.ifBlank { "No additional details provided." }
                                ?: "No additional details provided.",
                            fontSize = 12.5.sp,
                            color = InkPrimary,
                            lineHeight = 17.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .background(InkLight)
                    )

                    // Registration Form Section
                    Column {
                        Text(
                            "Register for Event",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = InkPrimary
                        )
                        Text(
                            "Ensure your roll number and college email are accurate.",
                            fontSize = 11.sp,
                            color = InkMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Success Message Box (Sage Green paper note)
                        if (formState.submitSuccessMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SageLight,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sketchShadow(offsetX = 2.dp, offsetY = 2.dp, color = SageGreen, radius = 10.dp)
                            ) {
                                Text(
                                    "🎉 " + formState.submitSuccessMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkPrimary,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Error Message Box (Tomato red torn note)
                        if (formState.submitErrorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TomatoLight,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, TomatoPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sketchShadow(offsetX = 2.dp, offsetY = 2.dp, color = TomatoDark, radius = 10.dp)
                            ) {
                                Text(
                                    "⚠️ " + formState.submitErrorMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TomatoDark,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Inputs
                        SketchFormInputField(
                            label = "Student Full Name *",
                            value = formState.studentName,
                            onValueChange = onNameChange,
                            placeholder = "e.g. Priya Sharma",
                            errorMessage = formState.studentNameError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SketchFormInputField(
                            label = "Roll Number *",
                            value = formState.rollNumber,
                            onValueChange = onRollChange,
                            placeholder = "e.g. 23CS1082",
                            errorMessage = formState.rollNumberError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SketchFormInputField(
                            label = "Phone Number (Optional)",
                            value = formState.phone,
                            onValueChange = onPhoneChange,
                            placeholder = "e.g. 9876543210",
                            keyboardType = KeyboardType.Phone,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SketchFormInputField(
                            label = "College Email Address *",
                            value = formState.email,
                            onValueChange = onEmailChange,
                            placeholder = "e.g. priya@college.edu",
                            keyboardType = KeyboardType.Email,
                            errorMessage = formState.emailError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isFull) PaperCardBacking else TomatoPrimary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, InkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .sketchShadow(offsetX = 3.dp, offsetY = 3.dp, radius = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isFull && !formState.isSubmitting) { onSubmit() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (formState.isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Registering...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                } else if (isFull) {
                                    Text("Event is Full", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkMuted)
                                } else {
                                    Text("Confirm Registration", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SketchMetaField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = InkPrimary
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.5.sp, fontWeight = FontWeight.Medium, color = InkMuted)
        Spacer(modifier = Modifier.height(1.dp))
        Text(value, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun SketchFormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        Spacer(modifier = Modifier.height(3.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.5.sp, color = InkMuted) },
            singleLine = true,
            enabled = enabled,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TomatoPrimary,
                unfocusedBorderColor = InkBorder,
                errorBorderColor = TomatoPrimary,
                focusedContainerColor = PaperCard,
                unfocusedContainerColor = PaperCard,
                focusedTextColor = InkPrimary,
                unfocusedTextColor = InkPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                errorMessage,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TomatoPrimary
            )
        }
    }
}
