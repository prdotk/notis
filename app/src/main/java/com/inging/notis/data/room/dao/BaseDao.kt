package com.inging.notis.data.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

/**
 * Created by annasu on 2021/02/05.
 */
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(org: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(org: List<T>): LongArray

    @Delete
    suspend fun delete(vararg org: T)

    @Delete
    suspend fun delete(org: List<T>)
}