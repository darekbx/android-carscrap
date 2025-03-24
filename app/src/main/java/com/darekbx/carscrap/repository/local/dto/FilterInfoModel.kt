package com.darekbx.carscrap.repository.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "filter_info")
class FilterInfoModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val filterId: String,
    val fuelType: String,
    val enginePower: String,
    val gearbox: String
)
