package com.zenith26.fest.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.ui.portal.ZenithTopAppBar
import com.zenith26.fest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel,
    onNavigateToPortal: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val events by viewModel.events.collectAsState()
    val rawEvents by viewModel.rawEvents.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val selectedEventFilterId by viewModel.selectedEventFilterId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAddEventModalOpen by viewModel.isAddEventModalOpen.collectAsState()
    val createEventForm by viewModel.createEventForm.collectAsState()

    var regFilterExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ZenithTopAppBar(
                currentScreen = "Admin",
                onEventsClick = onNavigateToPortal,
                onAdminClick = { /* Already on admin */ }
            )
        },
        containerColor = Slate50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Header Title & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Fest Operations Dashboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Live metrics, capacity alerts, and student registration logs.",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.loadDashboardData() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Brand600)
                    }

                    Button(
                        onClick = { viewModel.openAddEventModal() },
                        colors = ButtonDefaults.buttonColors(containerColor = Brand600),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Event", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Error Alert Banner
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Rose50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMessage!!,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Rose700,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Loading state
            if (isLoading && stats == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Brand600)
                }
            } else {
                // 4 Metric Stats Cards (Grid 2x2 on mobile)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            emoji = "🎪",
                            label = "TOTAL EVENTS",
                            value = "${stats?.totalEvents ?: "--"}",
                            valueColor = Slate900,
                            bgColor = Brand50,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            emoji = "👥",
                            label = "TOTAL REGISTRATIONS",
                            value = "${stats?.totalRegistrations ?: "--"}",
                            valueColor = Emerald600,
                            bgColor = Emerald50,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            emoji = "📈",
                            label = "CAPACITY UTILIZATION",
                            value = "${stats?.overallUtilizationPct ?: "--"}%",
                            valueColor = Purple600,
                            bgColor = Purple50,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            emoji = "⏳",
                            label = "UPCOMING EVENTS",
                            value = "${stats?.upcomingEventsCount ?: "--"}",
                            valueColor = Amber600,
                            bgColor = Amber50,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Category Breakdown Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Events Breakdown by Category",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text("Distribution", fontSize = 10.sp, color = Slate400)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val catMap = stats?.eventsByCategory ?: emptyMap()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            catMap.forEach { (cat, count) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Slate50,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
                                    modifier = Modifier.width(100.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("$count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                            Text("EVENTS", fontSize = 8.sp, color = Slate400)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Tabs
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == AdminTab.EVENTS_OVERVIEW) Color.White else Color.Transparent,
                            shadowElevation = if (selectedTab == AdminTab.EVENTS_OVERVIEW) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.selectTab(AdminTab.EVENTS_OVERVIEW) }
                        ) {
                            Text(
                                "Events Overview & Capacity",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == AdminTab.EVENTS_OVERVIEW) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (selectedTab == AdminTab.EVENTS_OVERVIEW) Brand600 else Slate600,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == AdminTab.REGISTRATION_ROSTER) Color.White else Color.Transparent,
                            shadowElevation = if (selectedTab == AdminTab.REGISTRATION_ROSTER) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.selectTab(AdminTab.REGISTRATION_ROSTER) }
                        ) {
                            Text(
                                "Registration Roster",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == AdminTab.REGISTRATION_ROSTER) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (selectedTab == AdminTab.REGISTRATION_ROSTER) Brand600 else Slate600,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // TAB 1: Events Overview Table
                if (selectedTab == AdminTab.EVENTS_OVERVIEW) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "All Fest Events",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    Text(
                                        "Sorted by utilization % (≥90% highlighted warning)",
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Slate100
                                ) {
                                    Text(
                                        "${events.size} Events",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Slate600,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (events.isEmpty()) {
                                Text(
                                    "No events found in database.",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    events.forEach { item ->
                                        AdminEventRowCard(item)
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: Registration Roster Table
                if (selectedTab == AdminTab.REGISTRATION_ROSTER) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Student Registrations Roster",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    Text(
                                        "Live feed of all submitted registrations",
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Filter Dropdown
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Filter:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate600)

                                val currentSelectedTitle = rawEvents.find { it.id == selectedEventFilterId }?.title
                                    ?: "All Events (${rawEvents.size})"

                                ExposedDropdownMenuBox(
                                    expanded = regFilterExpanded,
                                    onExpandedChange = { regFilterExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = currentSelectedTitle,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regFilterExpanded) },
                                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Brand500,
                                            unfocusedBorderColor = Slate300
                                        ),
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = regFilterExpanded,
                                        onDismissRequest = { regFilterExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("All Events (${rawEvents.size})", fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.setEventFilter(null)
                                                regFilterExpanded = false
                                            }
                                        )
                                        rawEvents.forEach { ev ->
                                            DropdownMenuItem(
                                                text = { Text("${ev.title} (#${ev.id})", fontSize = 12.sp) },
                                                onClick = {
                                                    viewModel.setEventFilter(ev.id)
                                                    regFilterExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (registrations.isEmpty()) {
                                Text(
                                    "No student registrations found for this selection.",
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    registrations.forEach { r ->
                                        RegistrationRosterCard(
                                            registration = r,
                                            onCancel = { viewModel.cancelRegistration(r.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Stretch Goal: Add New Event Modal
    if (isAddEventModalOpen) {
        AddEventModalDialog(
            formState = createEventForm,
            onTitleChange = { viewModel.updateCreateEventForm(title = it) },
            onCategoryChange = { viewModel.updateCreateEventForm(category = it) },
            onIconChange = { viewModel.updateCreateEventForm(icon = it) },
            onDateChange = { viewModel.updateCreateEventForm(eventDate = it) },
            onTimeChange = { viewModel.updateCreateEventForm(eventTime = it) },
            onVenueChange = { viewModel.updateCreateEventForm(venue = it) },
            onCapacityChange = { viewModel.updateCreateEventForm(capacity = it) },
            onFeeChange = { viewModel.updateCreateEventForm(fee = it) },
            onDescChange = { viewModel.updateCreateEventForm(description = it) },
            onSubmit = viewModel::submitCreateEvent,
            onDismiss = viewModel::closeAddEventModal
        )
    }
}

@Composable
private fun AdminStatCard(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )
                Text(
                    value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = valueColor
                )
            }
        }
    }
}

@Composable
private fun AdminEventRowCard(item: EnrichedEventItem) {
    val isCritical = item.utilizationPct >= 90.0
    val isHigh = item.utilizationPct >= 75.0 && item.utilizationPct < 90.0

    val badgeBg = when {
        isCritical -> Rose100
        isHigh -> Amber100
        else -> Slate100
    }
    val badgeTextColor = when {
        isCritical -> Rose800
        isHigh -> Amber800
        else -> Slate600
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCritical) Rose200 else Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(item.event.icon?.ifBlank { "⚡" } ?: "⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg,
                    border = if (isCritical || isHigh) androidx.compose.foundation.BorderStroke(1.dp, if (isCritical) Rose200 else Amber200) else null
                ) {
                    Text(
                        "${String.format("%.1f", item.utilizationPct)}% ${if (isCritical) "⚠️ Warning" else ""}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${item.event.category} · ${item.event.venue}",
                    fontSize = 11.sp,
                    color = Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Reg: ${item.registeredCount} / ${item.event.capacity}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Slate700
                )
            }
        }
    }
}

@Composable
private fun RegistrationRosterCard(
    registration: RegistrationItem,
    onCancel: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "#${registration.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        registration.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Brand50
                    ) {
                        Text(
                            registration.rollNumber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Brand700,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Event: ${registration.eventTitle}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate700
                )
                Text(
                    "${registration.email} · ${registration.phone ?: "No phone"}",
                    fontSize = 10.sp,
                    color = Slate500
                )
            }

            // Cancel Button
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rose50,
                    contentColor = Rose600
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AddEventModalDialog(
    formState: CreateEventFormState,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onVenueChange: (String) -> Unit,
    onCapacityChange: (String) -> Unit,
    onFeeChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brand900)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "➕ Add New Fest Event",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (formState.successMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Emerald50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                formState.successMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald700,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (formState.errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Rose50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Rose200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                formState.errorMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Rose700,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = onTitleChange,
                        label = { Text("Event Title *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.category,
                            onValueChange = onCategoryChange,
                            label = { Text("Category *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.icon,
                            onValueChange = onIconChange,
                            label = { Text("Icon") },
                            singleLine = true,
                            modifier = Modifier.weight(0.5f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.eventDate,
                            onValueChange = onDateChange,
                            label = { Text("Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.eventTime,
                            onValueChange = onTimeChange,
                            label = { Text("Time") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.venue,
                            onValueChange = onVenueChange,
                            label = { Text("Venue *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.capacity,
                            onValueChange = onCapacityChange,
                            label = { Text("Capacity *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    OutlinedTextField(
                        value = formState.fee,
                        onValueChange = onFeeChange,
                        label = { Text("Registration Fee (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = onDescChange,
                        label = { Text("Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onSubmit,
                        enabled = !formState.isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = Brand600),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Create Event", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
