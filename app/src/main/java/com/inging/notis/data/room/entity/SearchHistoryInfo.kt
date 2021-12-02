package com.inging.notis.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by annasu on 2021/04/26.
 */
@Entity
data class SearchHistoryInfo(

    @PrimaryKey(autoGenerate = false)
    var word: String = "",

    // 저장 시간
    var timestamp: Long = -1
)