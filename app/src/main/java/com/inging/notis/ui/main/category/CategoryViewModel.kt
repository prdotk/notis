package com.inging.notis.ui.main.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.inging.notis.constant.Constants
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val notiRepository: NotiRepository
) : ViewModel() {

    val categoryKeys = Constants.categoryMap.keys.toList()

    val categoryMap = Constants.categoryMap

    fun getCategoryUnreadCount(category: String) =
        notiRepository.getCategoryUnreadCount(category).asLiveData()
}