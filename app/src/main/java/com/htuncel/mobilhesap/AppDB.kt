package com.htuncel.mobilhesap

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [(ItemEntity::class)], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun itemDAO(): ItemDAO
}
