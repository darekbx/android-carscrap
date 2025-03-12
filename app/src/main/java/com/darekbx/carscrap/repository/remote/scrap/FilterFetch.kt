package com.darekbx.carscrap.repository.remote.scrap

import android.util.Log
import com.darekbx.carscrap.repository.local.dao.CarModelDao
import com.darekbx.carscrap.repository.local.dao.FilterDao
import com.darekbx.carscrap.repository.local.dto.CarModel
import com.darekbx.carscrap.repository.remote.scrap.Common.addHeaders
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class FilterFetch(
    private val gson: Gson,
    private val client: OkHttpClient,
    private val carModelDao: CarModelDao,
    private val filterDao: FilterDao
) {
    suspend fun fetch(filterId: String) {
        val filter = filterDao.getFilter(filterId)
        val requestData = FilterRequest().apply {
            setFilterValues(filter.make, filter.model, filter.generation, filter.salvage)
        }
        val externalIds = carModelDao.fetchIds(filterId)
        var page = 1

        Log.d(TAG, "Fetching data for filterId: $filterId, existing ids count: ${externalIds.size}")

        suspendCancellableCoroutine<Unit> { continuation ->
            while (true) {
                Log.d(TAG, "Fetch page: $page")
                requestData.variables.page = page

                val request = createRequest(requestData)
                val result = runCatching { client.newCall(request).execute() }
                val response = result.getOrNull()

                if (!result.isSuccess || response == null) {
                    continuation.resumeWith(Result.failure(result.exceptionOrNull()!!))
                    return@suspendCancellableCoroutine
                }

                val responseBody = response.body?.string()
                val fetchResponse = gson.fromJson(responseBody, CommonResponse::class.java)
                val jsonData = fetchResponse.data.advertSearch

                val totalCount = jsonData.totalCount
                val pageSize = jsonData.pageInfo.pageSize
                val currentOffset = jsonData.pageInfo.currentOffset

                var insertCount = 0

                carModelDao.insertAllSync(jsonData.edges.mapNotNull {
                    if (externalIds.contains(it.node.id)) {
                        null
                    } else {
                        insertCount++
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

                page += 1

                Log.d(TAG, "Page fetched, added $insertCount rows")

                if (currentOffset + pageSize > totalCount) {
                    break
                }
            }

            Log.d(TAG, "Fetch done!")
        }
    }

    private fun createRequest(requestData: FilterRequest): Request {
        val jsonRequest = gson.toJson(requestData)
        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonRequest.toRequestBody(mediaType)
        val request = Request.Builder().url(Common.url).apply { addHeaders() }.post(requestBody).build()
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(createdAt, formatter)
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    companion object {
        private val TAG = "FilterFetch"
    }
}
