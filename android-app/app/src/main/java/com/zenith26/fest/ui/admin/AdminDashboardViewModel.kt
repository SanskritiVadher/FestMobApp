package com.zenith26.fest.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.CreateEventRequest
import com.zenith26.fest.data.model.EventItem
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.StatsResponse
import com.zenith26.fest.data.repository.Result
import com.zenith26.fest.data.repository.ZenithRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EnrichedEventItem(
    val event: EventItem,
    val registeredCount: Int,
    val utilizationPct: Double
)

enum class AdminTab {
    EVENTS_OVERVIEW,
    REGISTRATION_ROSTER
}

data class CreateEventFormState(
    val title: String = "",
    val category: String = "Technical",
    val icon: String = "⚡",
    val eventDate: String = "2026-11-14",
    val eventTime: String = "10:00 AM",
    val venue: String = "",
    val capacity: String = "50",
    val fee: String = "0",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminDashboardViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(AdminTab.EVENTS_OVERVIEW)
    val selectedTab: StateFlow<AdminTab> = _selectedTab.asStateFlow()

    private val _stats = MutableStateFlow<StatsResponse?>(null)
    val stats: StateFlow<StatsResponse?> = _stats.asStateFlow()

    private val _events = MutableStateFlow<List<EnrichedEventItem>>(emptyList())
    val events: StateFlow<List<EnrichedEventItem>> = _events.asStateFlow()

    private val _rawEvents = MutableStateFlow<List<EventItem>>(emptyList())
    val rawEvents: StateFlow<List<EventItem>> = _rawEvents.asStateFlow()

    private val _registrations = MutableStateFlow<List<RegistrationItem>>(emptyList())
    val registrations: StateFlow<List<RegistrationItem>> = _registrations.asStateFlow()

    private val _selectedEventFilterId = MutableStateFlow<Int?>(null)
    val selectedEventFilterId: StateFlow<Int?> = _selectedEventFilterId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAddEventModalOpen = MutableStateFlow(false)
    val isAddEventModalOpen: StateFlow<Boolean> = _isAddEventModalOpen.asStateFlow()

    private val _createEventForm = MutableStateFlow(CreateEventFormState())
    val createEventForm: StateFlow<CreateEventFormState> = _createEventForm.asStateFlow()

    init {
        loadDashboardData()
    }

    fun selectTab(tab: AdminTab) {
        _selectedTab.value = tab
        if (tab == AdminTab.REGISTRATION_ROSTER) {
            loadRegistrations(_selectedEventFilterId.value)
        }
    }

    fun setEventFilter(eventId: Int?) {
        _selectedEventFilterId.value = eventId
        loadRegistrations(eventId)
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Parallel stats and events fetch
            val statsJob = launch {
                when (val res = repository.getStats()) {
                    is Result.Success -> _stats.value = res.data
                    is Result.Error -> _errorMessage.value = res.message
                }
            }

            val eventsJob = launch {
                when (val res = repository.getEvents(null, null)) {
                    is Result.Success -> {
                        _rawEvents.value = res.data
                        // Calculate registered count and utilization % and sort descending
                        val enriched = res.data.map { ev ->
                            val registered = ev.capacity - ev.availableSeats
                            val utilPct = if (ev.capacity > 0) (registered.toDouble() / ev.capacity) * 100.0 else 0.0
                            EnrichedEventItem(ev, registered, utilPct)
                        }.sortedByDescending { it.utilizationPct }

                        _events.value = enriched
                    }
                    is Result.Error -> _errorMessage.value = res.message
                }
            }

            statsJob.join()
            eventsJob.join()

            if (_selectedTab.value == AdminTab.REGISTRATION_ROSTER) {
                loadRegistrations(_selectedEventFilterId.value)
            }

            _isLoading.value = false
        }
    }

    fun loadRegistrations(eventId: Int? = null) {
        viewModelScope.launch {
            when (val res = repository.getRegistrations(eventId)) {
                is Result.Success -> _registrations.value = res.data
                is Result.Error -> _errorMessage.value = res.message
            }
        }
    }

    fun cancelRegistration(registrationId: Int) {
        viewModelScope.launch {
            when (val res = repository.cancelRegistration(registrationId)) {
                is Result.Success -> {
                    // Reload everything to update available seats and stats
                    loadDashboardData()
                }
                is Result.Error -> {
                    _errorMessage.value = res.message
                }
            }
        }
    }

    fun openAddEventModal() {
        _createEventForm.value = CreateEventFormState()
        _isAddEventModalOpen.value = true
    }

    fun closeAddEventModal() {
        _isAddEventModalOpen.value = false
        _createEventForm.value = CreateEventFormState()
    }

    fun updateCreateEventForm(
        title: String? = null,
        category: String? = null,
        icon: String? = null,
        eventDate: String? = null,
        eventTime: String? = null,
        venue: String? = null,
        capacity: String? = null,
        fee: String? = null,
        description: String? = null
    ) {
        val cur = _createEventForm.value
        _createEventForm.value = cur.copy(
            title = title ?: cur.title,
            category = category ?: cur.category,
            icon = icon ?: cur.icon,
            eventDate = eventDate ?: cur.eventDate,
            eventTime = eventTime ?: cur.eventTime,
            venue = venue ?: cur.venue,
            capacity = capacity ?: cur.capacity,
            fee = fee ?: cur.fee,
            description = description ?: cur.description,
            errorMessage = null
        )
    }

    fun submitCreateEvent() {
        val form = _createEventForm.value
        if (form.isSubmitting) return

        val title = form.title.trim()
        val category = form.category.trim()
        val venue = form.venue.trim()
        val date = form.eventDate.trim()
        val time = form.eventTime.trim()
        val cap = form.capacity.toIntOrNull() ?: 0
        val fee = form.fee.toIntOrNull() ?: 0

        if (title.isBlank() || category.isBlank() || venue.isBlank() || date.isBlank() || time.isBlank()) {
            _createEventForm.value = form.copy(errorMessage = "Title, category, venue, date, and time are required")
            return
        }

        if (cap <= 0) {
            _createEventForm.value = form.copy(errorMessage = "Capacity must be greater than 0")
            return
        }

        viewModelScope.launch {
            _createEventForm.value = form.copy(isSubmitting = true, errorMessage = null)
            val req = CreateEventRequest(
                title = title,
                category = category,
                description = form.description.trim().ifBlank { null },
                eventDate = date,
                eventTime = time,
                venue = venue,
                capacity = cap,
                fee = fee,
                icon = form.icon.trim().ifBlank { "⚡" }
            )

            when (val res = repository.createEvent(req)) {
                is Result.Success -> {
                    _createEventForm.value = form.copy(
                        isSubmitting = false,
                        successMessage = "Event \"${res.data.title}\" added successfully!"
                    )
                    loadDashboardData()
                }
                is Result.Error -> {
                    _createEventForm.value = form.copy(
                        isSubmitting = false,
                        errorMessage = res.message
                    )
                }
            }
        }
    }
}
