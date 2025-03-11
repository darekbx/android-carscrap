package com.darekbx.carscrap.repository.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "car_model")
data class CarModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val externalId: String,
    val createdAt: Long,
    val url: String,
    val price: Int,
    val currency: String,
    val fuelType: String,
    val gearbox: String,
    val enginePower: Int,
    val year: Int,
    val countryOrigin: String,
    val mileage: Int,
    val filterId: String
)
