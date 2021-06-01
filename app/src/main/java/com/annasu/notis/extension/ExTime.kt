package com.annasu.notis.extension

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by datasaver on 2021/02/19.
 */
fun LocalDateTime.ms(): Long {
    return atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.toTermTimeString(): String {
    var min = this / (60 * 1000)
    val hour = min / 60
    min %= 60
    val df = DecimalFormat("00")
    return "${df.format(hour)}시간 ${df.format(min)}분"
}

fun Long.toTermHour(): Int {
    val hour = this / (60 * 1000) / 60
    return hour.toInt()
}

fun Long.toTermMinute(): Int {
    var min = this / (60 * 1000)
    min %= 60
    return min.toInt()
}

fun Int.toTermHourString(): String {
    val df = DecimalFormat("00")
    return df.format(this)
}

fun Int.toTermMinuteString(): String {
    val df = DecimalFormat("00")
    return df.format(this)
}

fun Long.toLocalHourMinute(): Pair<Int, Int> {
    val zoneId = ZoneId.systemDefault()
    val time = Instant.ofEpochMilli(this).atZone(zoneId).toLocalTime()
    return Pair(time.hour, time.minute)
}

// 화면 잠금 날짜
fun Long.toDateString(): String {
    val zoneId = ZoneId.systemDefault()
    val time = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    return time.format(DateTimeFormatter.ofPattern("MM월 dd일"))
}

// 사용량 공유 시간
fun Long.toShareLocalTime(): String {
    val zoneId = ZoneId.systemDefault()
    val time = Instant.ofEpochMilli(this).atZone(zoneId).toLocalTime()
    return time.format(DateTimeFormatter.ofPattern("HH시 mm분 기준"))
}

fun Long.toTimeLog(): String {
    return SimpleDateFormat("yyyy-MM-dd (E) HH:mm:ss.SSS", Locale.KOREA).format(this)
}

fun Long.checkDiffDay(compareTime: Long?): Boolean {
    if (compareTime == null)
        return true
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()
    val compareDateTime = Instant.ofEpochMilli(compareTime).atZone(zoneId).toLocalDateTime()
    return zonedDateTime.dayOfYear != compareDateTime.dayOfYear
}

fun Long.checkSameMinute(compareTime: Long?): Boolean {
    if (compareTime == null)
        return false
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()
    val compareDateTime = Instant.ofEpochMilli(compareTime).atZone(zoneId).toLocalDateTime()
    return zonedDateTime.dayOfYear == compareDateTime.dayOfYear
        && zonedDateTime.hour == compareDateTime.hour
        && zonedDateTime.minute == compareDateTime.minute
}

// 시간 표시
fun Long.toDate(): String {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId)
    return zonedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 E요일"))
}

// 시간 표시
fun Long.toTime(): String {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId)
    return zonedDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("a h:mm"))
}

// 날짜 또는 시간 표시
fun Long.toDateOrTime(): String {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId)
    val localDate = zonedDateTime.toLocalDate()
    val today = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(zoneId).toLocalDate()
    return when {
        // 오늘일 경우 시간만 표시
        localDate.compareTo(today) == 0 ->
            zonedDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("a h:mm"))
        // 1일전은 어제로 표시
        localDate.compareTo(today) == -1 -> "어제"
        // 1일전 이상은 날짜로 표시
        localDate.year == today.year -> localDate.format(DateTimeFormatter.ofPattern("M월 d일"))
        // 연도가 다를 경우 연도 표시
        else -> localDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))
    }
}

// 몇시간전, 몇분전, 몇일전
fun Long.toBeforeTime(): String {
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = Instant.ofEpochMilli(this).atZone(zoneId)
    val localDate = zonedDateTime.toLocalDate()
    val today = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(zoneId)
    val dayCompare = today.toLocalDate().compareTo(localDate)
    return if (dayCompare == 0) {
        val todayTime = today.toLocalTime()
        val localTime = zonedDateTime.toLocalTime()
        var cmp = todayTime.hour.compareTo(localTime.hour)
        if (cmp == 0) {
            cmp = todayTime.minute.compareTo(localTime.minute)
            if (cmp == 0) "방금전" else "${cmp}분전"
        } else {
            "${cmp}시간전"
        }
    } else {
        "${dayCompare}일전"
    }
}

// 요일 변환, 일요일 7 -> 0, 나머지는 그대로
fun LocalDateTime.getWeekDay(): Int {
    return if (dayOfWeek.value == 7) 0 else dayOfWeek.value
}

// 사용량
fun Long.toUsageTermTime(): String {
    val sec = this / 1000 % 60
    var min = this / (60 * 1000)
    val hour = min / 60
    min %= 60
    val df = DecimalFormat("00")
    return "${df.format(hour)} : ${df.format(min)} : ${df.format(sec)}"
}

fun Long.isNextDay(nextTime: Long): Boolean {
    val zoneId = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val nextDay = Instant.ofEpochMilli(nextTime).atZone(zoneId).toLocalDate()
//    Timber.d("이전 체크 날짜:${date}, 지금 체크 날짜:${nextDay}")
    return date.isBefore(nextDay)
}