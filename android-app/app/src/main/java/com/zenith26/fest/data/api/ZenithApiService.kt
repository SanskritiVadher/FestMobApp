package com.zenith26.fest.data.api

import com.zenith26.fest.data.model.CreateEventRequest
import com.zenith26.fest.data.model.EventItem
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.model.RegistrationResponse
import com.zenith26.fest.data.model.StatsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZenithApiService {

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<EventItem>>

    @GET("api/events/{id}")
    suspend fun getEvent(
        @Path("id") id: Int
    ): Response<EventItem>

    @POST("api/events/{id}/register")
    suspend fun registerForEvent(
        @Path("id") id: Int,
        @Body request: RegistrationRequest
    ): Response<RegistrationResponse>

    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): Response<List<RegistrationItem>>

    @GET("api/stats")
    suspend fun getStats(): Response<StatsResponse>

    @POST("api/events")
    suspend fun createEvent(
        @Body request: CreateEventRequest
    ): Response<EventItem>

    @DELETE("api/registrations/{id}")
    suspend fun cancelRegistration(
        @Path("id") id: Int
    ): Response<Map<String, Any>>
}
