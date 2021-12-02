package com.inging.notis.service

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.*
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat.*
import androidx.lifecycle.*
import com.inging.notis.constant.NotiViewType
import com.inging.notis.constant.ServiceCommandType
import com.inging.notis.data.room.entity.NotiInfo
import com.inging.notis.data.room.entity.PkgInfo
import com.inging.notis.extension.*
import com.inging.notis.repository.NotiRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors
import javax.inject.Inject


/**
 * Created by annasu on 2021/04/23.
 * 노티 리스너 서비스
 */
@AndroidEntryPoint
class NotisNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var notiRepository: NotiRepository

    // single thread context
    private val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    // reply data map
    // key : pkgName_summaryText
    private val actionIntentMap = mutableMapOf<String, PendingIntent?>()
    private val resultKeyMap = mutableMapOf<String, String?>()

    // content intent map
    // key : notiId
    private val contentIntentMap = mutableMapOf<Long, PendingIntent?>()

    // block app list
    private var pkgInfoList = listOf<PkgInfo>()

    // notification
    private val notificationBar: NotisNotificationBar by lazy {
        NotisNotificationBar(this)
    }

    // firebase analytics
//    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

//        firebaseAnalytics = Firebase.analytics

//        firebaseAnalytics.logEvent(NOTI_SERVICE) {
//            param("flow", "onCreate()")
//        }

        notificationBar.createNotification()

        notiRepository.getPkgInfoListFlow().map {
            pkgInfoList = it
        }//.launchIn(CoroutineScope(Dispatchers.IO))
    }

    // 서비스 커맨드 실행
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val type = it.getIntExtra("COMMAND_TYPE", 0)

//            firebaseAnalytics.logEvent(NOTI_SERVICE) {
//                param("flow", "onStartCommand:type:$type")
//            }

            when (type) {
                ServiceCommandType.SEND_MESSAGE_TYPE -> sendMessage(it)
                ServiceCommandType.CHECK_REPLY_POSSIBLE -> checkReply(it)
                ServiceCommandType.RUN_CONTENT_INTENT -> runContentIntent(it)
            }
        }

        return START_NOT_STICKY
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

//        firebaseAnalytics.logEvent(NOTI_SERVICE) {
//            param("flow", "onListenerConnected")
//        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

//        firebaseAnalytics.logEvent(NOTI_SERVICE) {
//            param("flow", "onListenerDisconnected")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()

//        firebaseAnalytics.logEvent(NOTI_SERVICE) {
//            param("flow", "onDestroy")
//        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("onNotificationRemoved", sbn?.packageName ?: "?????")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
//        super.onNotificationPosted(sbn, rankingMap)
        sbn?.let { notificationPosted(sbn) }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d("onNotificationPosted", sbn?.notification.toString())
        sbn?.let { notificationPosted(sbn) }
    }

    private fun notificationPosted(sbn: StatusBarNotification) {
        // Ongoing 아니면 저장
        if (!sbn.isOngoing) {
            val pkgInfo = pkgInfoList.find { info ->
                info.pkgName == sbn.packageName
            }
            cancelNoti(sbn, pkgInfo)
            saveNoti(sbn, pkgInfo)
        }
    }

    // 상단바에서 노티 제거
    private fun cancelNoti(sbn: StatusBarNotification, pkgInfo: PkgInfo?) {
        sbn.notification?.also { notification ->
            if (pkgInfo == null) {
                if (notification.category != CATEGORY_MESSAGE) {
                    cancelNotification(sbn.key)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        notiRepository.savePkgInfo(
                            PkgInfo(
                                sbn.packageName,
                                isBlock = false,
                                isSave = true
                            )
                        )
                    }
                }
            } else {
                if (pkgInfo.isBlock) {
                    cancelNotification(sbn.key)
                }
            }
        }
    }

    // 세이브 할지 판단
    private fun saveNoti(sbn: StatusBarNotification, pkgInfo: PkgInfo?) {
        sbn.notification?.let { notification ->
            if (pkgInfo != null) {
                if (pkgInfo.isSave) {
                    saveNoti(sbn.packageName, notification, sbn.key)
                }
            } else {
                saveNoti(sbn.packageName, notification, sbn.key)
            }
        }
    }

    // 노티 저장
    private fun saveNoti(pkgName: String, notification: Notification, key: String) {
        Log.d("onNotificationPosted", notification.flags.toString(2))
        CoroutineScope(singleDispatcher).launch {
            val notiInfo = NotiInfo(pkgName = pkgName, senderType = NotiViewType.LEFT)

            // 고유 키 저장
            notiInfo.key = key

            // 노티 시간
//            notiInfo.timestamp = if (postTime > 0) postTime else System.currentTimeMillis()
            notiInfo.timestamp =
                if (notification.`when` > 0) notification.`when` else System.currentTimeMillis()

            // 노티 카테고리 저장
            notiInfo.category = notification.category ?: ""

            // 플래그 예외처리
//            when {
//                // 포그라운드 서비스 예외처리
//                (notification.flags and FLAG_FOREGROUND_SERVICE) == FLAG_FOREGROUND_SERVICE -> return@launch
//                (notification.flags and FLAG_ONGOING_EVENT) == FLAG_ONGOING_EVENT -> return@launch
//            }

            // 카테고리 예외처리
//            when (notiInfo.category) {
//                // 예외처리: 시스템, 서비스
//                CATEGORY_SYSTEM,
//                CATEGORY_SERVICE -> return@launch
//            }
            // 패키지 예외처리
//            when (notiInfo.pkgName) {
//                "android",
//                "com.android.systemui",
//                "com.android.provide.downloads" -> return@launch

            // 플레이스토어 알림, 카테고리 없으면 저장안함
//                "com.android.vending" -> {
//                    if (notiInfo.category.isEmpty())
//                        return@launch
//                }
//            }

//            notification.contentIntent.
//            with(NotificationManagerCompat.from(this)) {
//                notify(0, builder.b)
//            }

            // 노티 데이터 저장
            notification.extras?.let { bundle ->
                Log.d("onNotificationPosted", bundle.toString())

                bundle.get(EXTRA_TITLE)?.let {
                    notiInfo.title = it.toString()
                    notiInfo.subText = it.toString()
                    notiInfo.summaryText = it.toString()
                }
                bundle.get(EXTRA_TEXT)?.let {
                    notiInfo.text = it.toString()
                }
                bundle.get(EXTRA_SUB_TEXT)?.let {
                    notiInfo.subText = it.toString()
                    notiInfo.summaryText = it.toString()
                }
                bundle.get(EXTRA_SUMMARY_TEXT)?.let {
                    notiInfo.summaryText = it.toString()
                }
//                    notiInfo.icon = bundle.getInt("android.icon")

                // EXTRA_LARGE_ICON
                val largeIcon = bundle.getParcelable<Parcelable>(EXTRA_LARGE_ICON)
                if (largeIcon is Bitmap) {
                    notiInfo.largeIcon = largeIcon
                        .saveFile(
                            this@NotisNotificationListenerService,
                            "${EXTRA_LARGE_ICON}/${notiInfo.pkgName}/${notiInfo.title}"
                        )
                } else if (largeIcon is Icon) {
                    notiInfo.largeIcon =
                        (largeIcon.loadDrawable(this@NotisNotificationListenerService) as? BitmapDrawable)
                            ?.saveFile(
                                this@NotisNotificationListenerService,
                                "${EXTRA_LARGE_ICON}/${notiInfo.pkgName}/${notiInfo.title}"
                            )
                            ?: ""
                }

                // EXTRA_PICTURE
                val picture = bundle.getParcelable<Parcelable>(EXTRA_PICTURE)
                if (picture is Bitmap) {
                    notiInfo.picture = picture
                        .saveFile(
                            this@NotisNotificationListenerService,
                            "${EXTRA_PICTURE}/${notiInfo.pkgName}/${notiInfo.title}"
                        )
                } else if (picture is Icon) {
                    notiInfo.picture =
                        (picture.loadDrawable(this@NotisNotificationListenerService) as? BitmapDrawable)
                            ?.saveFile(
                                this@NotisNotificationListenerService,
                                "${EXTRA_PICTURE}/${notiInfo.pkgName}/${notiInfo.title}"
                            )
                            ?: ""
                }

                // EXTRA_BACKGROUND_IMAGE_URI
//                val bgImage = bundle.getParcelable<Parcelable>(EXTRA_BACKGROUND_IMAGE_URI)
//                if (bgImage is Bitmap) {
//                    notiInfo.bgImage = bgImage
//                        .saveFile(
//                            this@NotisNotificationListenerService,
//                            "${EXTRA_BACKGROUND_IMAGE_URI}/${notiInfo.pkgName}/${notiInfo.title}"
//                        )
//                } else if (bgImage is Icon) {
//                    notiInfo.bgImage =
//                        (bgImage.loadDrawable(this@NotisNotificationListenerService) as? BitmapDrawable)
//                            ?.saveFile(
//                                this@NotisNotificationListenerService,
//                                "${EXTRA_BACKGROUND_IMAGE_URI}/${notiInfo.pkgName}/${notiInfo.title}"
//                            )
//                            ?: ""
//                }
            }

            // db 저장
            if (notiInfo.title.isNotEmpty() || notiInfo.text.isNotEmpty()) {
                // 노티 시간 저장
//                notiInfo.timestamp = System.currentTimeMillis() // notification.`when`
                // 노티 데이터 db 저장
                val notiId = notiRepository.insertNoti(notiInfo)

                // 클릭 액션 저장
                notification.contentIntent?.let { pendingIntent ->
                    // 맵정리, 인텐트가 사라지면 삭제
                    val tempList = contentIntentMap.toList()
                    tempList.forEach {
                        if (it.second == null) {
                            contentIntentMap.remove(it.first)
                        }
                    }
                    // 펜딩 인텐트 맵 저장
                    contentIntentMap[notiId] = pendingIntent
                }
                // 노티 답장 액션 저장
                notification.actions?.let { actions ->
                    actions.forEach { action ->
                        // 답변 보내기 액션 있을 경우 인텐트 저장
                        action?.remoteInputs?.let { remoteInputs ->
                            remoteInputs.forEach { remoteInput ->
                                if (notiInfo.summaryText.isNotEmpty()) {
                                    // 맵정리, 인텐트가 사라지면 삭제
                                    val tempList = actionIntentMap.toList()
                                    tempList.forEach {
                                        if (it.second == null) {
                                            actionIntentMap.remove(it.first)
                                            resultKeyMap.remove(it.first)
                                        }
                                    }
                                    // 맵 저장
                                    actionIntentMap["${notiInfo.pkgName}_${notiInfo.summaryText}"] =
                                        action.actionIntent
                                    resultKeyMap["${notiInfo.pkgName}_${notiInfo.summaryText}"] =
                                        remoteInput?.resultKey
                                }
                            }
                        }
                    }
                }
                // 노티 바 업데이트
                notificationBar.updateNotification(notiInfo)
            }
        }
    }

    // 액션 실행
    private fun runContentIntent(intent: Intent) {
        val pkgName = intent.getStringExtra("PKG_NAME") ?: ""
        val notiId = intent.getLongExtra("NOTI_ID", -1)

        contentIntentMap[notiId]?.let {
            try {
                it.send()
                return
            } catch (e: Exception) {
                contentIntentMap.remove(notiId)
            }
        }

        // IntentPending 실패 시 앱 실행
        executeLocalAppPackage(pkgName)
    }

    // 메시지 보내기
    private fun sendMessage(intent: Intent) {
        val pkgName = intent.getStringExtra("PKG_NAME") ?: ""
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""
        val messageText = intent.getStringExtra("MESSAGE_TEXT") ?: ""

        val mapKey = "${pkgName}_$summaryText"
        val resultKey = resultKeyMap[mapKey]
        val actionIntent = actionIntentMap[mapKey]

        if (resultKey != null && actionIntent != null) {
            val i = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            RemoteInput.addResultsToIntent(arrayOf(RemoteInput.Builder(resultKey).build()), i,
                Bundle().apply { putCharSequence(resultKey, messageText) })
            try {
                actionIntent.send(this@NotisNotificationListenerService, 0, i)
                sendResult(intent, true)
            } catch (e: Exception) {
                // PendingIntent 만료되거나 오류 되면 맵에서 삭제
                resultKeyMap.remove(mapKey)
                actionIntentMap.remove(mapKey)

                sendResult(intent, false)
            }
        } else {
            sendResult(intent, success = false, check = false)
        }
    }

    // 결과값 보내기
    private fun sendResult(intent: Intent, success: Boolean, check: Boolean = true) {
        val messageText = intent.getStringExtra("MESSAGE_TEXT") ?: ""

        intent.getParcelableExtra<ResultReceiver>("RECEIVER")?.let { receiver ->
            receiver.send(0, Bundle().apply {
                putBoolean("RESULT", success)
                putString("MESSAGE_TEXT", messageText)
                putBoolean("CHECK", check)
            })
        }
    }

    // 답장 보낼 수 있는지 확인
    private fun checkReply(intent: Intent) {
        val pkgName = intent.getStringExtra("PKG_NAME") ?: ""
        val summaryText = intent.getStringExtra("SUMMARY_TEXT") ?: ""

        val mapKey = "${pkgName}_$summaryText"
        val resultKey = resultKeyMap[mapKey]
        val actionIntent = actionIntentMap[mapKey]

        val check = resultKey != null && actionIntent != null

        intent.getParcelableExtra<ResultReceiver>("RECEIVER")?.let { receiver ->
            receiver.send(1, Bundle().apply {
                putBoolean("CHECK", check)
            })
        }
    }

    companion object {
        const val NOTI_SERVICE = "noti_service"
    }
}