package com.htuncel.mobilhesap

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey var id: String,

    var name: String,

    var cost: String,

    @TypeConverters(DateConverter::class)
    var date: Long,

    var type: String
)