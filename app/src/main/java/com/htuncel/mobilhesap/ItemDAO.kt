package com.htuncel.mobilhesap

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ItemDAO {
    @Insert
    fun saveItem(item: ItemEntity)

    @Query("SELECT * FROM ITEMS ORDER BY DATE DESC")
    fun readAllItems(): Array<ItemEntity>

    @RawQuery
    fun runtimeQuery(sortQuery: SupportSQLiteQuery): List<ItemEntity>
}