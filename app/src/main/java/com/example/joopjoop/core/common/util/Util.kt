package com.example.joopjoop.core.common.util

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 기타 계산 로직, 변환 작업 등을 위한 Util 객체

// 시간 Date -> String 형변환
fun formatDate(date: Date?): String {
    if (date == null) return ""
    val formatter = SimpleDateFormat("M월 dd일", Locale.getDefault())
    return formatter.format(date)
}

// 토스트 알림
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}