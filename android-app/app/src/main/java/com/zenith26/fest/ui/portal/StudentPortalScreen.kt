package com.zenith26.fest.ui.portal

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
        "Technical" to "💻 Technical",
        "Cultural" to "🎭 Cultural",
        "Sports" to "⚽ Sports",
        "Gaming" to "🎮 Gaming",
        "Workshop" to "📐 Workshop"
    )

    Scaffold(
        topBar = {
            ZenithTopAppBar(
                currentScreen = "Events",
                onEventsClick = { /* Already on events */ },
                onAdminClick = onNavigateToAdmin
            )
        },
        containerColor = Slate50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            HeroSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search & Filter Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Search TextField
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = {
                                Text(
                                    "Search events by title...",
                                    fontSize = 14.sp,
                                    color = Slate400
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand500,
                                unfocusedBorderColor = Slate300,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { (key, label) ->
                                val isSelected = selectedCategory == key
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Brand600 else Slate100,
                                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setCategory(key) }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else Slate600,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content State (Loading / Error / Empty / Grid)
                when (val state = uiState) {
                    is PortalUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Brand600)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Loading fest events...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    is PortalUiState.Empty -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text("🔍", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No events match your criteria",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Slate800
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Try adjusting your search query or selecting a different category filter.",
                                    fontSize = 12.sp,
                                    color = Slate500,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    is PortalUiState.Error -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Rose50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Rose600)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Failed to load events",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Rose700
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.message,
                                    fontSize = 12.sp,
                                    color = Rose600,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.loadEvents() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Try Again", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    is PortalUiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            state.events.forEach { event ->
                                EventCard(
                                    event = event,
                                    onViewDetails = { viewModel.openEventDetails(event) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer
                FooterSection()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Detail & Registration Modal Dialog
    if (selectedEventForModal != null) {
        EventDetailRegistrationDialog(
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

@Composable
fun ZenithTopAppBar(
    currentScreen: String,
    onEventsClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "ZENITH",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Brand900,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Brand50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand200)
                ) {
                    Text(
                        "'26",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Brand600,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Navigation Links
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Events Link
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (currentScreen == "Events") Brand50 else Color.Transparent,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onEventsClick() }
                ) {
                    Text(
                        "Events",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (currentScreen == "Events") Brand600 else Slate600,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Admin Dashboard Link
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (currentScreen == "Admin") Brand50 else Slate100,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onAdminClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Admin",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (currentScreen == "Admin") Brand600 else Slate700
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate200
                        ) {
                            Text(
                                "Coordinator",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate700,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(Brand900, Brand800, Color(0xFF581C87))
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pill
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Text(
                    "🎉 The Annual National College Tech & Cultural Festival",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Brand100,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = "Ignite Your Spark at Zenith '26",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "48 Hours of Pure Energy, Innovation, and Rivalry. Discover workshops, intense hack sprints, gaming battles, and high-voltage cultural shows.",
                fontSize = 12.sp,
                color = Brand100.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Badges row
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeroInfoBadge("📅 November 14 – 15, 2026")
                HeroInfoBadge("📍 Campus Grounds & Auditoriums")
                HeroInfoBadge("🎟️ 14 Seeded Competitions & Workshops")
            }
        }
    }
}

@Composable
private fun HeroInfoBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Brand100,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EventCard(
    event: EventItem,
    onViewDetails: () -> Unit
) {
    val isFull = event.availableSeats <= 0
    val feeText = if (event.fee == 0) "Free" else "₹${event.fee}"

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Icon, Category, Title, Fee/FULL badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(event.icon?.ifBlank { "⚡" } ?: "⚡", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Surface(
                            shape = CircleShape,
                            color = Brand50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Brand100)
                        ) {
                            Text(
                                event.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand600,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Full or Fee Badge
                if (isFull) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Rose600,
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            "FULL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (event.fee == 0) Emerald50 else Amber50,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (event.fee == 0) Emerald200 else Amber200
                        )
                    ) {
                        Text(
                            feeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (event.fee == 0) Emerald700 else Amber700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = event.description?.ifBlank { "Join this exciting fest event! Click details to read more." }
                    ?: "Join this exciting fest event! Click details to read more.",
                fontSize = 12.sp,
                color = Slate500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Meta Info Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate50,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📅 Date & Time:", fontSize = 11.sp, color = Slate400)
                        Text(
                            "${event.eventDate} · ${event.eventTime}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate700
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📍 Venue:", fontSize = 11.sp, color = Slate400)
                        Text(
                            event.venue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate700,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Divider(color = Slate200.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎟️ Available Seats:", fontSize = 11.sp, color = Slate400)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isFull) Rose100 else Emerald50,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isFull) Rose200 else Emerald200
                            )
                        ) {
                            Text(
                                if (isFull) "0 Seats Left" else "${event.availableSeats} / ${event.capacity}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFull) Rose700 else Emerald700,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = onViewDetails,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFull) Slate100 else Brand600,
                    contentColor = if (isFull) Slate700 else Color.White
                ),
                border = if (isFull) androidx.compose.foundation.BorderStroke(1.dp, Slate200) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "View Details & Register",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EventDetailRegistrationDialog(
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Brand900, Brand800)
                            )
                        )
                        .padding(18.dp)
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
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(event.icon?.ifBlank { "⚡" } ?: "⚡", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        event.category.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Brand100,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    event.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Brand100)
                        }
                    }
                }

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Meta Grid
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaField("Date & Time", "${event.eventDate}, ${event.eventTime}", Modifier.weight(1f))
                                MetaField("Venue", event.venue, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaField(
                                    "Entry Fee",
                                    if (event.fee == 0) "Free" else "₹${event.fee}",
                                    Modifier.weight(1f),
                                    valueColor = if (event.fee == 0) Emerald600 else Amber700
                                )
                                MetaField(
                                    "Seats Left",
                                    if (isFull) "FULL (0 / ${event.capacity})" else "${event.availableSeats} of ${event.capacity} left",
                                    Modifier.weight(1f),
                                    valueColor = if (isFull) Rose600 else Brand600
                                )
                            }
                        }
                    }

                    // About
                    Column {
                        Text(
                            "ABOUT THIS EVENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            event.description?.ifBlank { "No additional details provided." }
                                ?: "No additional details provided.",
                            fontSize = 13.sp,
                            color = Slate600,
                            lineHeight = 18.sp
                        )
                    }

                    Divider(color = Slate100, thickness = 1.dp)

                    // Registration Form Section
                    Column {
                        Text(
                            "Register for Event",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            "Ensure your roll number and college email are accurate.",
                            fontSize = 11.sp,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Success Message Box
                        if (formState.submitSuccessMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    formState.submitSuccessMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Emerald700,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Error Message Box
                        if (formState.submitErrorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Rose50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    formState.submitErrorMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Rose700,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Input: Student Name
                        FormInputField(
                            label = "Student Full Name *",
                            value = formState.studentName,
                            onValueChange = onNameChange,
                            placeholder = "e.g. Priya Sharma",
                            errorMessage = formState.studentNameError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input: Roll Number & Phone
                        FormInputField(
                            label = "Roll Number *",
                            value = formState.rollNumber,
                            onValueChange = onRollChange,
                            placeholder = "e.g. 23CS1082",
                            errorMessage = formState.rollNumberError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FormInputField(
                            label = "Phone Number (Optional)",
                            value = formState.phone,
                            onValueChange = onPhoneChange,
                            placeholder = "e.g. 9876543210",
                            keyboardType = KeyboardType.Phone,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input: Email Address
                        FormInputField(
                            label = "College Email Address *",
                            value = formState.email,
                            onValueChange = onEmailChange,
                            placeholder = "e.g. priya@college.edu",
                            keyboardType = KeyboardType.Email,
                            errorMessage = formState.emailError,
                            enabled = !isFull && !formState.isSubmitting
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = onSubmit,
                            enabled = !isFull && !formState.isSubmitting,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Brand600,
                                disabledContainerColor = if (isFull) Slate200 else Brand600.copy(alpha = 0.6f),
                                contentColor = Color.White,
                                disabledContentColor = if (isFull) Slate500 else Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (formState.isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Registering...", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            } else if (isFull) {
                                Text("Event is Full", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("Confirm Registration", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Slate800
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Slate400)
        Spacer(modifier = Modifier.height(1.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun FormInputField(
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
            fontWeight = FontWeight.SemiBold,
            color = Slate700
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Slate400) },
            singleLine = true,
            enabled = enabled,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand500,
                unfocusedBorderColor = Slate300,
                errorBorderColor = Rose500,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                errorMessage,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Rose500
            )
        }
    }
}

@Composable
fun FooterSection() {
    Surface(
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "© 2026 Zenith College Fest Management System · Native Android App with Jetpack Compose & FastAPI",
            fontSize = 10.sp,
            color = Slate400,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(14.dp)
        )
    }
}
