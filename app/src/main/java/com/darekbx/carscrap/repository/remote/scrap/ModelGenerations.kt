package com.darekbx.carscrap.repository.remote.scrap

import com.darekbx.carscrap.repository.remote.scrap.Common.addHeaders
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class ModelGenerations(private val gson: Gson, private val client: OkHttpClient) {

    suspend fun fetchGenerations(make: String, model: String) = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val operationName = "listingScreen"
            val variables = mapOf(
                "filters" to emptyList<Any>(),
                "includeCepik" to false,
                "includeFiltersCounters" to false,
                "includeNewPromotedAds" to false,
                "includePriceEvaluation" to false,
                "includePromotedAds" to false,
                "includeRatings" to false,
                "includeSortOptions" to false,
                "includeSuggestedFilters" to false,
                "maxAge" to 60,
                "page" to 1,
                "parameters" to listOf("make"),
                "promotedInput" to emptyMap<String, Any>(),
                "searchTerms" to listOf("osobowe", make, model)
            )
            val encodedVariables = URLEncoder.encode(gson.toJson(variables), "UTF-8")
            val url = "${Common.url}?operationName=$operationName&variables=$encodedVariables&extensions=%7B%22persistedQuery%22%3A%7B%22sha256Hash%22%3A%221a840f0ab7fbe2543d0d6921f6c963de8341e04a4548fd1733b4a771392f900a%22%2C%22version%22%3A1%7D%7D"
            val request = Request.Builder().url(url).apply { addHeaders() }.get().build()
            val result = runCatching { client.newCall(request).execute() }

            when  {
                result.isSuccess -> {
                    val response = result.getOrNull()
                    if (response?.isSuccessful == true) {
                        val responseBody = response.body?.string()
                        val verificationResponse = gson.fromJson(responseBody, CommonResponse::class.java)
                        val modelGenerations = filterGenerations(verificationResponse)
                        continuation.resumeWith(Result.success(modelGenerations ?: emptyList()))
                    } else {
                        continuation.resumeWith(Result.failure(Exception("HTTP request failed: ${response?.code}")))
                    }
                }
                result.isFailure -> {
                    continuation.resumeWith(Result.failure(result.exceptionOrNull()!!))
                }
            }
        }
    }

    private fun filterGenerations(verificationResponse: CommonResponse) =
        verificationResponse.data.advertSearch?.alternativeLinks?.find { it.name == "generations" }?.links
}
