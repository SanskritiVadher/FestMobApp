package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

data class EventItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String?,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("fee") val fee: Int = 0,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("available_seats") val availableSeats: Int
)

data class RegistrationRequest(
    @SerializedName("student_name") val studentName: String,
    @SerializedName("roll_number") val rollNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null
)

data class RegistrationResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("student_name") val studentName: String,
    @SerializedName("registered_at") val registeredAt: String
)

data class RegistrationItem(
    @SerializedName("id") val id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("event_title") val eventTitle: String,
    @SerializedName("student_name") val studentName: String,
    @SerializedName("roll_number") val rollNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("registered_at") val registeredAt: String
)

data class StatsResponse(
    @SerializedName("total_events") val totalEvents: Int,
    @SerializedName("total_registrations") val totalRegistrations: Int,
    @SerializedName("total_capacity") val totalCapacity: Int,
    @SerializedName("overall_utilization_pct") val overallUtilizationPct: Double,
    @SerializedName("upcoming_events_count") val upcomingEventsCount: Int,
    @SerializedName("events_by_category") val eventsByCategory: Map<String, Int>
)

data class CreateEventRequest(
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String?,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("fee") val fee: Int = 0,
    @SerializedName("icon") val icon: String? = "📅"
)

data class ApiErrorResponse(
    @SerializedName("detail") val detail: String?
)
