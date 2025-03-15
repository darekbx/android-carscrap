package com.darekbx.carscrap.repository.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class Car(
    val externalId: String,
    val createdAt: Timestamp,
    val url: String,
    val price: Int,
    val currency: String,
    val fuelType: String,
    val gearbox: String,
    val enginePower: Int,
    val year: Int,
    val countryOrigin: String,
    val mileage: Int
) {
    
    companion object {
        fun DocumentSnapshot.toCar(): Car {
            return Car(
                externalId = getStringOrEmpty("externalId"),
                createdAt = getTimestamp("createdAt")!!,
                url = getStringOrEmpty("url"),
                price = getIntOrZero("price"),
                currency = getStringOrEmpty("currency"),
                fuelType = getStringOrEmpty("fuelType"),
                gearbox = getStringOrEmpty("gearbox"),
                enginePower = getIntOrZero("enginePower"),
                year = getIntOrZero("year"),
                countryOrigin = getStringOrEmpty("countryOrigin"),
                mileage = getIntOrZero("mileage")
            )
        }
    }
}

fun DocumentSnapshot.getStringOrEmpty(field: String): String {
    if (get(field) is String) {
        return getString(field) ?: ""
    } else {
        return get(field).toString()
    }
}

fun DocumentSnapshot.getIntOrZero(field: String): Int {
    val rawValue = get(field)
    if (rawValue is String) {
        return rawValue.toIntOrNull() ?: 0
    } else {
        return getLong(field)?.toInt() ?: 0
    }
}
