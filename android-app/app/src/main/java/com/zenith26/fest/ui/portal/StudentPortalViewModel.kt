package com.zenith26.fest.ui.portal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.EventItem
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.repository.Result
import com.zenith26.fest.data.repository.ZenithRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PortalUiState {
    object Loading : PortalUiState
    data class Success(val events: List<EventItem>) : PortalUiState
    object Empty : PortalUiState
    data class Error(val message: String) : PortalUiState
}

data class RegistrationFormState(
    val studentName: String = "",
    val rollNumber: String = "",
    val email: String = "",
    val phone: String = "",
    val studentNameError: String? = null,
    val rollNumberError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val submitSuccessMessage: String? = null,
    val submitErrorMessage: String? = null
)

class StudentPortalViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<PortalUiState>(PortalUiState.Loading)
    val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEventForModal = MutableStateFlow<EventItem?>(null)
    val selectedEventForModal: StateFlow<EventItem?> = _selectedEventForModal.asStateFlow()

    private val _registrationState = MutableStateFlow(RegistrationFormState())
    val registrationState: StateFlow<RegistrationFormState> = _registrationState.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        loadEvents()
    }

    fun setCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadEvents()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(280) // 280ms debounce matching web frontend
            loadEvents()
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = PortalUiState.Loading
            when (val result = repository.getEvents(_selectedCategory.value, _searchQuery.value)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = PortalUiState.Empty
                    } else {
                        _uiState.value = PortalUiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.value = PortalUiState.Error(result.message)
                }
            }
        }
    }

    fun openEventDetails(event: EventItem) {
        _selectedEventForModal.value = event
        _registrationState.value = RegistrationFormState()
    }

    fun closeEventModal() {
        _selectedEventForModal.value = null
        _registrationState.value = RegistrationFormState()
    }

    fun onStudentNameChanged(value: String) {
        _registrationState.value = _registrationState.value.copy(
            studentName = value,
            studentNameError = null,
            submitErrorMessage = null
        )
    }

    fun onRollNumberChanged(value: String) {
        _registrationState.value = _registrationState.value.copy(
            rollNumber = value,
            rollNumberError = null,
            submitErrorMessage = null
        )
    }

    fun onEmailChanged(value: String) {
        _registrationState.value = _registrationState.value.copy(
            email = value,
            emailError = null,
            submitErrorMessage = null
        )
    }

    fun onPhoneChanged(value: String) {
        _registrationState.value = _registrationState.value.copy(
            phone = value,
            submitErrorMessage = null
        )
    }

    fun submitRegistration() {
        val currentEvent = _selectedEventForModal.value ?: return
        val currentForm = _registrationState.value

        // Prevent repeated tap while submitting
        if (currentForm.isSubmitting) return

        // Client-side validations matching web frontend
        val nameTrimmed = currentForm.studentName.trim()
        val rollTrimmed = currentForm.rollNumber.trim()
        val emailTrimmed = currentForm.email.trim()

        var hasError = false
        var nameErr: String? = null
        var rollErr: String? = null
        var emailErr: String? = null

        if (nameTrimmed.isEmpty()) {
            nameErr = "Student name is required"
            hasError = true
        }
        if (rollTrimmed.isEmpty()) {
            rollErr = "Roll number is required"
            hasError = true
        }
        if (emailTrimmed.isEmpty() || !emailTrimmed.contains("@") || emailTrimmed.startsWith("@") || emailTrimmed.endsWith("@")) {
            emailErr = "Please enter a valid email containing '@'"
            hasError = true
        }

        if (hasError) {
            _registrationState.value = currentForm.copy(
                studentNameError = nameErr,
                rollNumberError = rollErr,
                emailError = emailErr
            )
            return
        }

        viewModelScope.launch {
            _registrationState.value = currentForm.copy(isSubmitting = true, submitErrorMessage = null)

            val req = RegistrationRequest(
                studentName = nameTrimmed,
                rollNumber = rollTrimmed,
                email = emailTrimmed,
                phone = currentForm.phone.trim().ifBlank { null }
            )

            when (val res = repository.registerForEvent(currentEvent.id, req)) {
                is Result.Success -> {
                    _registrationState.value = RegistrationFormState(
                        isSubmitting = false,
                        submitSuccessMessage = "🎉 Registered successfully for ${currentEvent.title}! Registration ID: #${res.data.id}"
                    )
                    // Refresh events live to update seat counts
                    loadEvents()
                }
                is Result.Error -> {
                    val msg = res.message.ifBlank { "Registration failed. Please check your details." }
                    _registrationState.value = currentForm.copy(
                        isSubmitting = false,
                        submitErrorMessage = "⚠️ $msg"
                    )
                }
            }
        }
    }
}
