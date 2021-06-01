package com.annasu.notis.ui.main.pkg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.annasu.notis.data.room.entity.PkgInfoWithNotiInfo
import com.annasu.notis.databinding.LayoutPkgItemBinding
import com.annasu.notis.extension.getAppIcon
import com.annasu.notis.extension.getAppName
import com.annasu.notis.extension.toBeforeTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by datasaver on 2021/04/26.
 */
class PkgViewHolder(
    private val binding: LayoutPkgItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: PkgInfoWithNotiInfo, listener: (String) -> Unit) {
        binding.run {
            // 앱 아이콘
            CoroutineScope(Dispatchers.Main).launch {
                icon.setImageDrawable(root.context.getAppIcon(data.pkgInfo.pkgName))
            }
            // 앱 이름
            title.text = root.context.getAppName(data.pkgInfo.pkgName)
            // 마지막 알림 시간
            timestamp.text = data.pkgInfo.updateTime.toBeforeTime()
            // 노티 목록
            recycler.adapter = PkgNotiAdapter().apply {
                submitList(data.notiInfoList.sortedByDescending {
                    it.notiInfo.timestamp
                })
            }

            topView.setOnClickListener {
                listener(data.pkgInfo.pkgName)
            }
        }
    }

    companion object {
        fun getInstance(parent: ViewGroup): PkgViewHolder {
            val binding = LayoutPkgItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PkgViewHolder(binding)
        }
    }
}