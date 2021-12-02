package com.inging.notis.data.model

data class AppInfo(

    // 패키지 이름
    var pkgName: String,

    // 앱 표시 이름
    var label: String,

    // 시스템 앱인지 체크
    var isSystemApp: Boolean,

    // 블럭 여부, 기본 true
    var isBlock: Boolean = true,

    // 저장 여부, 기본 true
    var isSave: Boolean = true
)