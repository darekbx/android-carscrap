package com.darekbx.carscrap.repository.remote.scrap

import android.util.Log
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.repository.remote.scrap.Common.addHeaders
import com.darekbx.carscrap.repository.remote.scrap.update.ApiRequest
import com.darekbx.carscrap.repository.remote.scrap.update.Filter
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class FilterFetch(
    private val gson: Gson,
    private val client: OkHttpClient,
    private val carModelDao: CarModelDao,
    private val filterDao: FilterDao
) {
    suspend fun fetch(
        filterId: String,
        onProgress: (Int, Int) -> Unit = { _, _ -> },
        onCompleted: (Int) -> Unit = { }
    ) {
        val filter = filterDao.getFilter(filterId)
        val apiRequest = ApiRequest().apply {
            filters = listOf(
                Filter(name = "filter_enum_make", value = filter.make),
                Filter(name = "filter_enum_model", value = filter.model),
                Filter(name = "filter_enum_generation", value = filter.generation),
                Filter(name = "filter_enum_damaged", value = if (filter.salvage) "1" else "0"),
                Filter(name = "category_id", value = "29")
            )
        }

        val externalIds = carModelDao.fetchIds(filterId)
        var page = 1

        Log.d(TAG, "Fetching data for filterId: $filterId, existing ids count: ${externalIds.size}")

        suspendCancellableCoroutine { continuation ->
            var addedCount = 0
            while (true) {
                Log.d(TAG, "Fetch page: $page")
                apiRequest.page = page

                val request = createRequest(apiRequest)
                val result = runCatching { client.newCall(request).execute() }
                val response = result.getOrNull()

                if (!result.isSuccess || response == null) {
                    continuation.resumeWith(Result.failure(result.exceptionOrNull()!!))
                    return@suspendCancellableCoroutine
                }

                val responseBody = response.body?.string()
                val fetchResponse = gson.fromJson(responseBody, CommonResponse::class.java)
                val jsonData = fetchResponse.data.advertSearch ?: continue

                val totalCount = jsonData.totalCount
                val pageSize = jsonData.pageInfo.pageSize
                val currentOffset = jsonData.pageInfo.currentOffset

                var insertCount = 0

                carModelDao.insertAllSync(jsonData.edges.mapNotNull {
                    if (externalIds.contains(it.node.id)) {
                        null
                    } else {
                        insertCount++
                        addedCount++
                        mapToCarModel(
                            filterId = filterId,
                            nodeId = it.node.id,
                            createdAt = it.node.createdAt,
                            url = it.node.url,
                            price = it.node.price,
                            parameters = it.node.parameters
                        )
                    }
                })

                onProgress(currentOffset, totalCount)
                page += 1

                Log.d(TAG, "Page fetched, added $insertCount rows")

                if (currentOffset + pageSize > totalCount) {
                    break
                }
            }

            Log.d(TAG, "Fetch done!")

            continuation.resumeWith(Result.success(Unit))
            onCompleted(addedCount)
        }
    }

    private fun createRequest(apiRequest: ApiRequest): Request {
        val jsonRequest = gson.toJson(apiRequest)
        val request = Request.Builder()
            .url("${Common.url}?operationName=listingScreen&variables=$jsonRequest&extensions={\"persistedQuery\":{\"sha256Hash\":\"1a840f0ab7fbe2543d0d6921f6c963de8341e04a4548fd1733b4a771392f900a\",\"version\":1}}")
            .apply { addHeaders() }
            .get()
            .build()
        return request
    }

    private fun mapToCarModel(
        filterId: String,
        nodeId: String,
        createdAt: String,
        url: String,
        price: Price,
        parameters: List<Parameter>
    ): CarModel {
        val paramMap = parameters.associateBy { it.key }
        return CarModel(
            externalId = nodeId,
            createdAt = parseCreatedAt(createdAt),
            url = url,
            price = price.amount.units,
            currency = price.amount.currencyCode,
            fuelType = paramMap.getOrDefault("fuel_type", ""),
            gearbox = paramMap.getOrDefault("gearbox", ""),
            enginePower = paramMap.getOrDefaultInt("engine_power", 0),
            year = paramMap.getOrDefaultInt("year", 0),
            countryOrigin = paramMap.getOrDefault("country_origin", ""),
            mileage = paramMap.getOrDefaultInt("mileage", 0),
            filterId = filterId
        )
    }

    private fun Map<String, Parameter>.getOrDefault(key: String, defaultValue: String): String {
        return this[key]?.value ?: defaultValue
    }

    private fun Map<String, Parameter>.getOrDefaultInt(key: String, defaultValue: Int): Int {
        return this[key]?.value?.toInt() ?: defaultValue
    }

    private fun parseCreatedAt(createdAt: String): Long {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val localDateTime = LocalDateTime.parse(createdAt, formatter)
            return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        } catch (e: Exception) {
            return try {
                val instant = Instant.parse(createdAt)
                instant.toEpochMilli()
            } catch (e: DateTimeParseException) {
                0L
            }
        }
    }

    companion object {
        private const val TAG = "FilterFetch"
    }
}
