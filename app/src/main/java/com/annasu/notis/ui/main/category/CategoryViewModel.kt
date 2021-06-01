package com.annasu.notis.ui.main.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.annasu.notis.constant.Constants
import com.annasu.notis.repository.NotiRepository
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