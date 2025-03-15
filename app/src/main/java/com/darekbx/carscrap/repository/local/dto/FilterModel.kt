package com.darekbx.carscrap.repository.local.dto

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "filter")
class Filter(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val make: String,
    val model: String,
    val generation: String,
    val salvage: Boolean
) {
    @Ignore
    var itemsCount: Int = 0
}
