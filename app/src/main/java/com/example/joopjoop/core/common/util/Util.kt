package com.example.joopjoop.core.common.util

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 기타 계산 로직, 변환 작업 등을 위한 Util 객체

// 시간 Date -> String 형변환
fun formatDate(date: Date?): String {
    if (date == null) return ""
    val formatter = SimpleDateFormat("M월 d일", Locale.getDefault())
    return formatter.format(date)
}

// 토스트 알림
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

object OnSingleClickListener {
    private var lastClickTime = 0L
    private const val DEFAULT_DELAY = 1000L

    fun onclick(delay: Long = DEFAULT_DELAY, action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delay) {
            lastClickTime = currentTime
            action()
        }
    }
}
// 개발자모드 - 목업데이터 시딩 On / Off
object DevFlags {
    const val ENABLE_SEEDING = true
}