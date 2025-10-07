package com.darekbx.carscrap.repository.remote.scrap

import com.darekbx.carscrap.repository.remote.scrap.Common.addHeaders
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class FilterVerification(private val gson: Gson, private val client: OkHttpClient) {

    suspend fun verify(make: String, model: String) = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val requestData = VerificationRequest().apply { setFilterValues(make, model) }
            val jsonRequest = gson.toJson(requestData)
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonRequest.toRequestBody(mediaType)
            val request = Request.Builder().url(Common.url).apply { addHeaders() }.post(requestBody).build()
            val result = runCatching { client.newCall(request).execute() }

            when  {
                result.isSuccess -> {
                    val response = result.getOrNull()
                    if (response?.isSuccessful == true) {
                        val responseBody = response.body?.string()
                        val response = gson.fromJson(responseBody, CommonResponse::class.java)
                        val result = Result.success((response.data.advertSearch?.totalCount ?: 0) > 0)
                        continuation.resumeWith(result)
                    } else {
                        continuation.resumeWith(Result.failure(Exception("HTTP failed: ${response?.code}")))
                    }
                }
                result.isFailure -> {
                    continuation.resumeWith(Result.failure(result.exceptionOrNull()!!))
                }
            }
        }
    }
}
