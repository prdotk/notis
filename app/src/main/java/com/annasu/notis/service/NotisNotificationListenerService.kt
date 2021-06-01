package com.annasu.notis.service

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.Parcelable
import android.os.ResultReceiver
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.annasu.notis.constant.NotiViewType
import com.annasu.notis.constant.ServiceCommandType
import com.annasu.notis.data.room.entity.NotiInfo
import com.annasu.notis.extension.saveIcon
import com.annasu.notis.repository.NotiRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Created by datasaver on 2021/04/23.
 * 노티 리스너 서비스
 */
@AndroidEntryPoint
class NotisNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var notiRepository: NotiRepository

    // single thread context
    private val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    // reply data map
    private val actionIntentMap = mutableMapOf<String, PendingIntent?>()
    private val resultKeyMap = mutableMapOf<String, String?>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d("onNotificationPosted", sbn.toString())

        CoroutineScope(singleDispatcher).launch {
            val notiInfo = NotiInfo(pkgName = sbn?.packageName ?: "", senderType = NotiViewType.LEFT)

            sbn?.notification?.let { notification ->
                // 노티 카테고리 저장
                notiInfo.category = notification.category ?: ""

                // 노티 데이터 저장
                notification.extras?.let { bundle ->
                    Log.d("onNotificationPosted", bundle.toString())

                    bundle.get(Notification.EXTRA_TITLE)?.let {
                        notiInfo.title = it.toString()
                        notiInfo.subText = it.toString()
                        notiInfo.summaryText = it.toString()
                    }
                    bundle.get(Notification.EXTRA_TEXT)?.let {
                        notiInfo.text = it.toString()
                    }
                    bundle.get(Notification.EXTRA_SUB_TEXT)?.let {
                        notiInfo.subText = it.toString()
                        notiInfo.summaryText = it.toString()
                    }
                    bundle.get(Notification.EXTRA_SUMMARY_TEXT)?.let {
                        notiInfo.summaryText = it.toString()
                    }
//                    notiInfo.icon = bundle.getInt("android.icon")

                    val largeIcon = bundle.getParcelable<Parcelable>("android.largeIcon")
                    if (largeIcon is Bitmap) {
                        notiInfo.largeIcon = largeIcon
                            .saveIcon(this@NotisNotificationListenerService, "${notiInfo.pkgName}_${notiInfo.title}")
                    } else if (largeIcon is Icon) {
                        notiInfo.largeIcon = (largeIcon.loadDrawable(this@NotisNotificationListenerService) as? BitmapDrawable)
                            ?.saveIcon(this@NotisNotificationListenerService, "${notiInfo.pkgName}_${notiInfo.title}")
                            ?: ""
                    }
                }

                // 노티 답장 액션 저장
                notification.actions?.let { actions ->
                    actions.forEach { action ->
                        // 답변 보내기 액션 있을 경우 인텐트 저장
                        action?.remoteInputs?.let { remoteInputs ->
                            remoteInputs.forEach { remoteInput ->
                                if (notiInfo.summaryText.isNotEmpty()) {
                                    actionIntentMap[notiInfo.summaryText] = action.actionIntent
                                    resultKeyMap[notiInfo.summaryText] = remoteInput?.resultKey
                                }
                            }
                        }
                    }
                }

                // db 저장
                if (notiInfo.title.isNotEmpty() || notiInfo.text.isNotEmpty()) {
                    // 노티 시간 저장 TODO: Notification 자체에 시간있는지 확인 필요
                    notiInfo.timestamp = System.currentTimeMillis()
                    // 노티 데이터 db 저장
                    notiRepository.insertNoti(notiInfo)
                }
            }
        }
    }

    // 메시지 보내기
    private fun sendMessage(intent: Intent) {
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""
        val messageText = intent.getStringExtra("MESSAGE_TEXT") ?: ""
        val resultKey = resultKeyMap[summaryText]
        val actionIntent = actionIntentMap[summaryText]

        if (resultKey != null && actionIntent != null) {
            val i = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            RemoteInput.addResultsToIntent(arrayOf(RemoteInput.Builder(resultKey).build()), i,
                Bundle().apply { putCharSequence(resultKey, messageText) })
            try {
                actionIntent.send(this@NotisNotificationListenerService, 0, i)
                sendResult(intent, true)
            } catch (e: Exception) {
                sendResult(intent, false)
            }
        } else {
            sendResult(intent, success = false, check = false)
        }
    }

    // 결과값 보내기
    private fun sendResult(intent: Intent, success: Boolean, check: Boolean = true) {
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""
        val messageText = intent.getStringExtra("MESSAGE_TEXT") ?: ""

        intent.getParcelableExtra<ResultReceiver>("RECEIVER")?.let { receiver ->
            receiver.send(0, Bundle().apply {
                putBoolean("RESULT", success)
                putString("SUMMARY_TEXT", summaryText)
                putString("MESSAGE_TEXT", messageText)
                putBoolean("CHECK", check)
            })
        }
    }

    // 답장 보낼 수 있는지 확인
    private fun checkReply(intent: Intent) {
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""
        val resultKey = resultKeyMap[summaryText]
        val actionIntent = actionIntentMap[summaryText]

        val check = resultKey != null && actionIntent != null

        intent.getParcelableExtra<ResultReceiver>("RECEIVER")?.let { receiver ->
            receiver.send(1, Bundle().apply {
                putBoolean("CHECK", check)
            })
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.getIntExtra("COMMAND_TYPE", 0)) {
                ServiceCommandType.SEND_MESSAGE_TYPE -> sendMessage(it)
                ServiceCommandType.CHECK_REPLY_POSSIBLE -> checkReply(it)
            }
        }

        return START_NOT_STICKY
    }
}