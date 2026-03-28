package com.example.joopjoop.core.common.util

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 기타 계산 로직, 변환 작업 등을 위한 Util 객체
object Util {
    // 시간 Date -> String 형변환
    fun formatDate(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("M월 dd일", Locale.getDefault())
        return formatter.format(date)
    }
}

/**
 * [공통 UI 유틸리티] Context 확장 함수
 * * 토스트 메시지를 짧고 간결하게 호출하기 위해 사용합니다.
 * 매번 Toast.makeText(...).show()를 작성하는 번거로움을 줄여줍니다.
 *
 * @param message 사용자에게 보여줄 메시지 문자열
 * * [사용 예시]
 * Composable 내부:
 * val context = LocalContext.current
 * context.showToast("메시지 내용")
 * * Activity 내부:
 * showToast("메시지 내용")
 *
 * [핵심 아키텍처 규칙]
 * - ViewModel에서는 직접 호출하지 마세요.
 * - 대신 ViewModel에서 UiState의 'errorMessage' 등을 업데이트하고,
 * Screen(Composable)의 LaunchedEffect에서 이 함수를 호출하는 패턴을 권장합니다.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}