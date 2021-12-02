package com.inging.notis.ui.main.more.save

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.inging.notis.constant.ClickMode
import com.inging.notis.data.model.AppInfo
import com.inging.notis.databinding.LayoutAppListItemBinding
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.getAppName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by annasu on 2021/04/26.
 */
class SaveAppsViewHolder(
    private val binding: LayoutAppListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val onSwitchAllChanged =
        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender is ObservableInt) {
                    when (sender.get()) {
                        0 -> binding.onOff.isChecked = false
                        1 -> binding.onOff.isChecked = true
                    }
                }
            }
        }

    fun bind(
        info: AppInfo,
        allOnOff: ObservableInt,
        listener: (Int, AppInfo) -> Unit
    ) {
        binding.run {
            // 앱 아이콘
            CoroutineScope(Dispatchers.Main).launch {
                title.text = root.context.getAppName(info.pkgName)
                icon.setImageDrawable(root.context.getAppIcon(info.pkgName))
            }

            onOff.setOnCheckedChangeListener(null)
            onOff.isChecked = info.isSave
            onOff.setOnCheckedChangeListener { _, isChecked ->
                info.isSave = isChecked
                listener(ClickMode.DEFAULT, info)
            }
            allOnOff.addOnPropertyChangedCallback(onSwitchAllChanged)
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): SaveAppsViewHolder {
            val binding = LayoutAppListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SaveAppsViewHolder(binding)
        }
    }
}