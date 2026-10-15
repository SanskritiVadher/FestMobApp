package com.zenith26.fest.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    /**
     * Default base URL configured for Android emulator connecting to host PC running FastAPI.
     * Use "http://10.0.2.2:8000/" for standard Android Emulator.
     * Can be dynamically modified at runtime via [setCustomBaseUrl].
     */
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:8009/"
    const val DEFAULT_LOCAL_BASE_URL = "http://127.0.0.1:8009/"

    private var currentBaseUrl: String = DEFAULT_EMULATOR_BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: ZenithApiService? = null

    fun getBaseUrl(): String = currentBaseUrl

    fun setCustomBaseUrl(newUrl: String) {
        val sanitized = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (sanitized != currentBaseUrl) {
            currentBaseUrl = sanitized
            retrofitInstance = null
            apiServiceInstance = null
        }
    }

    fun getApiService(): ZenithApiService {
        if (apiServiceInstance == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitInstance = retrofit
            apiServiceInstance = retrofit.create(ZenithApiService::class.java)
        }
        return apiServiceInstance!!
    }
}
