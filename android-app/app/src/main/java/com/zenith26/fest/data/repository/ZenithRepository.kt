package com.zenith26.fest.data.repository

import com.google.gson.Gson
import com.zenith26.fest.data.api.NetworkClient
import com.zenith26.fest.data.model.*
import retrofit2.Response

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val statusCode: Int? = null) : Result<Nothing>()
}

class ZenithRepository {

    private val apiService get() = NetworkClient.getApiService()
    private val gson = Gson()

    // Offline bundled seed events for cloud emulators (e.g., Appetize) where localhost/10.0.2.2 cannot reach host PC
    private val defaultEvents = mutableListOf(
        EventItem(1, "Code Sprint 2.0", "Technical", "Fast-paced competitive coding contest testing algorithmic problem solving and speed.", "2026-11-14", "10:00 AM", "Main Auditorium", 100, 0, "💻", 99),
        EventItem(2, "Robo Race", "Technical", "Autonomous and manual robot racing challenge across custom obstacle tracks.", "2026-11-14", "02:00 PM", "Engineering Lab 2", 40, 50, "🤖", 40),
        EventItem(3, "AI Pitch Fest", "Technical", "Pitch groundbreaking AI/ML startup concepts and prototypes to venture judges.", "2026-11-15", "11:00 AM", "Seminar Hall A", 60, 0, "🚀", 60),
        EventItem(4, "Web Design Rumble", "Technical", "Head-to-head front-end web design showdown with live creative themes.", "2026-11-14", "04:00 PM", "Computer Lab 1", 50, 0, "🎨", 50),
        EventItem(5, "Battle of Bands", "Cultural", "Inter-college musical face-off featuring electric live band performances.", "2026-11-15", "06:00 PM", "Open Air Theatre", 300, 0, "🎸", 300),
        EventItem(6, "Dance Fiesta", "Cultural", "Solo and group choreographic competition celebrating western and classical styles.", "2026-11-14", "07:00 PM", "Open Air Theatre", 200, 0, "💃", 200),
        EventItem(7, "Nukkad Natak", "Cultural", "Powerful street play competition addressing contemporary social challenges.", "2026-11-15", "03:00 PM", "Amphitheatre", 150, 0, "🎭", 150),
        EventItem(8, "Fashion Fusion", "Cultural", "Runway fashion show spotlighting innovative, sustainable student designs.", "2026-11-15", "08:00 PM", "Main Stage", 250, 100, "✨", 250),
        EventItem(9, "Open Mic Night", "Cultural", "Spoken word poetry, stand-up comedy, and acoustic jam session.", "2026-11-14", "09:00 PM", "Cafeteria Lawn", 100, 0, "🎤", 100),
        EventItem(10, "Five-a-Side Football", "Sports", "High-intensity knockout football tournament on the turf ground.", "2026-11-14", "08:00 AM", "Sports Ground", 80, 0, "⚽", 80),
        EventItem(11, "Chess Championship", "Sports", "Rapid and blitz chess tournament adhering to official FIDE rules.", "2026-11-15", "09:00 AM", "Indoor Games Hall", 32, 0, "♟️", 32),
        EventItem(12, "Valorant Showdown", "Gaming", "5v5 tactical shooter showdown on dedicated high-performance gaming rigs.", "2026-11-14", "01:00 PM", "Gaming Arena", 60, 0, "🎮", 60),
        EventItem(13, "UI/UX Design Workshop", "Workshop", "Hands-on masterclass on modern design systems and Figma prototyping.", "2026-11-14", "11:00 AM", "Design Lab", 40, 0, "📐", 40),
        EventItem(14, "Intro to Vibe Coding", "Workshop", "Learn agentic development and rapid full-stack vibe coding with Antigravity.", "2026-11-15", "10:00 AM", "Computer Lab 1", 45, 0, "⚡", 45),
        EventItem(15, "Kotlin Compose Bootcamp", "Workshop", "Hands-on Kotlin and Jetpack Compose mobile app development workshop.", "2026-11-15", "03:00 PM", "Mobile Computing Lab", 30, 0, "📱", 29)
    )

    private val localRegistrations = mutableListOf(
        RegistrationItem(1, 1, "Code Sprint 2.0", "Priya Sharma", "23CS1082", "priya.sharma@college.edu", "9876543210", "2026-10-12T12:26:43")
    )

    private fun <T> parseError(response: Response<T>): Result.Error {
        val errorBody = response.errorBody()?.string()
        val errorDetail = if (!errorBody.isNullOrBlank()) {
            try {
                val parsed = gson.fromJson(errorBody, ApiErrorResponse::class.java)
                parsed.detail ?: errorBody
            } catch (e: Exception) {
                errorBody
            }
        } else {
            response.message().ifBlank { "HTTP Error ${response.code()}" }
        }
        return Result.Error(errorDetail, response.code())
    }

    suspend fun getEvents(category: String?, search: String?): Result<List<EventItem>> {
        try {
            val response = apiService.getEvents(
                category = if (category.isNullOrBlank() || category == "All") null else category,
                search = if (search.isNullOrBlank()) null else search
            )
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            // Fallback for cloud/Appetize sandbox without active host bridge
        }

        // Apply local filtering over events
        var filtered = defaultEvents.toList()
        if (!category.isNullOrBlank() && category != "All") {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (!search.isNullOrBlank()) {
            filtered = filtered.filter { it.title.contains(search, ignoreCase = true) }
        }
        return Result.Success(filtered)
    }

    suspend fun getEvent(id: Int): Result<EventItem> {
        try {
            val response = apiService.getEvent(id)
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            // Fallback
        }
        val found = defaultEvents.find { it.id == id }
        return if (found != null) Result.Success(found) else Result.Error("Event with id $id not found", 404)
    }

    suspend fun registerForEvent(eventId: Int, request: RegistrationRequest): Result<RegistrationResponse> {
        try {
            val response = apiService.registerForEvent(eventId, request)
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            } else if (response.code() in listOf(400, 404, 409)) {
                return parseError(response)
            }
        } catch (e: Exception) {
            // Fallback local registration handling
        }

        val eventIdx = defaultEvents.indexOfFirst { it.id == eventId }
        if (eventIdx == -1) return Result.Error("Event with id $eventId not found", 404)
        val event = defaultEvents[eventIdx]

        if (event.availableSeats <= 0) {
            return Result.Error("Event is full", 409)
        }

        val existing = localRegistrations.find { it.eventId == eventId && it.email.equals(request.email, ignoreCase = true) }
        if (existing != null) {
            return Result.Error("Already registered", 409)
        }

        val newId = (localRegistrations.maxOfOrNull { it.id } ?: 0) + 1
        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        val newReg = RegistrationItem(
            id = newId,
            eventId = eventId,
            eventTitle = event.title,
            studentName = request.studentName,
            rollNumber = request.rollNumber,
            email = request.email,
            phone = request.phone,
            registeredAt = now
        )
        localRegistrations.add(0, newReg)
        defaultEvents[eventIdx] = event.copy(availableSeats = (event.availableSeats - 1).coerceAtLeast(0))

        return Result.Success(
            RegistrationResponse(
                id = newId,
                eventId = eventId,
                studentName = request.studentName,
                registeredAt = now
            )
        )
    }

    suspend fun getRegistrations(eventId: Int? = null): Result<List<RegistrationItem>> {
        try {
            val response = apiService.getRegistrations(eventId)
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            // Fallback
        }
        val list = if (eventId != null) localRegistrations.filter { it.eventId == eventId } else localRegistrations
        return Result.Success(list)
    }

    suspend fun getStats(): Result<StatsResponse> {
        try {
            val response = apiService.getStats()
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            // Fallback
        }

        val totalEvents = defaultEvents.size
        val totalRegistrations = localRegistrations.size
        val totalCapacity = defaultEvents.sumOf { it.capacity }
        val utilPct = if (totalCapacity > 0) Math.round((totalRegistrations.toDouble() / totalCapacity * 100.0) * 10.0) / 10.0 else 0.0
        val catMap = defaultEvents.groupBy { it.category }.mapValues { it.value.size }

        return Result.Success(
            StatsResponse(
                totalEvents = totalEvents,
                totalRegistrations = totalRegistrations,
                totalCapacity = totalCapacity,
                overallUtilizationPct = utilPct,
                upcomingEventsCount = totalEvents,
                eventsByCategory = catMap
            )
        )
    }

    suspend fun createEvent(request: CreateEventRequest): Result<EventItem> {
        try {
            val response = apiService.createEvent(request)
            if (response.isSuccessful && response.body() != null) {
                return Result.Success(response.body()!!)
            }
        } catch (e: Exception) {
            // Fallback
        }

        val newId = (defaultEvents.maxOfOrNull { it.id } ?: 0) + 1
        val newEvent = EventItem(
            id = newId,
            title = request.title,
            category = request.category,
            description = request.description,
            eventDate = request.eventDate,
            eventTime = request.eventTime,
            venue = request.venue,
            capacity = request.capacity,
            fee = request.fee,
            icon = request.icon ?: "⚡",
            availableSeats = request.capacity
        )
        defaultEvents.add(newEvent)
        return Result.Success(newEvent)
    }

    suspend fun cancelRegistration(registrationId: Int): Result<Unit> {
        try {
            val response = apiService.cancelRegistration(registrationId)
            if (response.isSuccessful) {
                return Result.Success(Unit)
            }
        } catch (e: Exception) {
            // Fallback
        }

        val reg = localRegistrations.find { it.id == registrationId }
        if (reg != null) {
            localRegistrations.remove(reg)
            val evIdx = defaultEvents.indexOfFirst { it.id == reg.eventId }
            if (evIdx != -1) {
                val ev = defaultEvents[evIdx]
                defaultEvents[evIdx] = ev.copy(availableSeats = (ev.availableSeats + 1).coerceAtMost(ev.capacity))
            }
        }
        return Result.Success(Unit)
    }
}
