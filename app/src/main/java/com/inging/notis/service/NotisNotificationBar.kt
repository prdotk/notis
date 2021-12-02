package com.inging.notis.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.inging.notis.R
import com.inging.notis.constant.Constants
import com.inging.notis.data.model.IconInfo
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.extension.getAppIcon
import com.inging.notis.extension.loadRecentIconList
import com.inging.notis.extension.saveRecentIconList
import com.inging.notis.extension.toDateOrTime
import com.inging.notis.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 스테이터스 바에 보이는 노티피케이션
 */
class NotisNotificationBar(private val context: Context) {

//    private lateinit var notificationBuilder: NotificationCompat.Builder

//    private val notificationLayout: RemoteViews by lazy {
//        RemoteViews(context.packageName, R.layout.layout_notification_bar)
//    }

    fun createNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
//                setSound(null, null)
//                enableLights(false)
//                enableVibration(false)
//                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notifyIntent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationLayout = RemoteViews(context.packageName, R.layout.layout_notification_bar)

        val notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_ID)
            .setChannelId(Constants.CHANNEL_ID)
            .setContentIntent(notifyPendingIntent)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setStyle(NotificationCompat.BigTextStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.

//        notificationManager.notify(0, notificationBuilder.build())

        CoroutineScope(Dispatchers.Default).launch {
            // load list
            val recentIconList = context.loadRecentIconList()
            // 최신순 정렬
            recentIconList.sortByDescending {
                it.timestamp
            }
            // 아이콘 이미지
            recentIconList.forEachIndexed { index, iconInfo ->
                if (index < iconResIds.size) {
                    try {
                        // 아이콘 15개로 설정 시 커스텀 노티가 안보이는 현상 발생, 현재 12개로 함
                        val icon = context.getAppIcon(iconInfo.pkgName)?.toBitmap()
                        notificationLayout.setImageViewBitmap(iconResIds[index], icon)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (index == 0) {
                        notificationLayout.setTextViewText(
                            R.id.timestamp,
                            iconInfo.timestamp.toDateOrTime()
                        )
                    }
                }
            }

            with(NotificationManagerCompat.from(context)) {
                notify(0, notificationBuilder.build())
            }
        }
    }

    fun updateNotification(notiInfo: NotiInfo) {
        CoroutineScope(Dispatchers.Default).launch {
            // intent
            val notifyIntent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            // layout
            val notificationLayout = RemoteViews(context.packageName, R.layout.layout_notification_bar)
            // notification builder
            val notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_ID)
                .setChannelId(Constants.CHANNEL_ID)
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(NotificationCompat.BigTextStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setOngoing(true) // 사용자가 직접 못지우게 계속 실행하기.
            // load list
            val recentIconList = context.loadRecentIconList()
            // 기존 목록에서 찾기
            val findIconInfo = recentIconList.find {
                it.pkgName == notiInfo.pkgName
            }
            // 없으면 추가, 있으면 업데이트
            if (findIconInfo == null) {
                recentIconList.add(IconInfo(notiInfo.pkgName, notiInfo.timestamp))
            } else {
                findIconInfo.timestamp = notiInfo.timestamp
            }
            // 최신순 정렬
            recentIconList.sortByDescending {
                it.timestamp
            }
            // 삭제 목록
            val deleteList = mutableListOf<IconInfo>()
            // 아이콘 이미지
            recentIconList.forEachIndexed { index, iconInfo ->
                if (index < iconResIds.size) {
                    try {
                        val icon = context.getAppIcon(iconInfo.pkgName)?.toBitmap()
                        notificationLayout.setImageViewBitmap(iconResIds[index], icon)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    deleteList.add(iconInfo)
                }
            }
            // 초과 목록 삭제
            recentIconList.removeAll(deleteList)
            // save list
            context.saveRecentIconList(recentIconList)

            notificationLayout.setTextViewText(
                R.id.timestamp,
//                recentIconList.size.toString()
                notiInfo.timestamp.toDateOrTime()
            )

            with(NotificationManagerCompat.from(context)) {
                notify(0, notificationBuilder.build())
            }
        }
    }

//    private fun refreshNotification() {
//        CoroutineScope(Dispatchers.Default).launch {
//            // load list
//            val recentIconList = context.loadRecentIconList()
//            // 최신순 정렬
//            recentIconList.sortByDescending {
//                it.timestamp
//            }
//            // 아이콘 이미지
//            recentIconList.forEachIndexed { index, iconInfo ->
//                if (index < iconResIds.size) {
//                    val icon = context.getAppIcon(iconInfo.pkgName)?.toBitmap()
//                    notificationLayout.setImageViewBitmap(iconResIds[index], icon)
//                    if (index == 0) {
//                        notificationLayout.setTextViewText(
//                            R.id.timestamp,
//                            iconInfo.timestamp.toDateOrTime()
//                        )
//                    }
//                }
//            }
//
//            with(NotificationManagerCompat.from(context)) {
//                notify(0, notificationBuilder.build())
//            }
//        }
//    }

    companion object {
        val iconResIds = listOf(
            R.id.icon00, R.id.icon01, R.id.icon02, R.id.icon03, R.id.icon04,
            R.id.icon05, R.id.icon06, R.id.icon07, R.id.icon08, R.id.icon09,
            R.id.icon10, R.id.icon11, //R.id.icon12, R.id.icon13//, R.id.icon14
        )
    }
}