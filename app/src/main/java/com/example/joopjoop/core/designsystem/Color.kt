package com.example.joopjoop.core.designsystem

import androidx.compose.ui.graphics.Color

// ── Primary ──────────────────────────────
val OrangePrimary    = Color(0xFFE07B2A)  // 버튼, 마커, 탭 활성, 강조
val OrangeLight      = Color(0xFFF09340)  // 아이콘 강조, 마커 외곽선
val OrangeDark       = Color(0xFFB85E18)  // 버튼 pressed

// ── Background ───────────────────────────
val BgDarkest        = Color(0xFF1A1208)  // 앱 전체 최하단 배경
val BgDark           = Color(0xFF251A0E)  // 카드, 바텀시트 배경
val BgSurface        = Color(0xFF332211)  // 입력창, 리스트 아이템
val BgElevated       = Color(0xFF4A3520)  // 선택된 상태, 구분선

// ── Text ─────────────────────────────────
val TextPrimary      = Color(0xFFFFFFFF)  // 제목, 주요 텍스트
val TextSecondary    = Color(0xFFC8B49A)  // 부제목, 설명 텍스트
val TextTertiary     = Color(0xFF7A6552)  // 힌트, 비활성, 날짜

// ── Divider ──────────────────────────────
val DividerColor     = Color(0xFF2A2A2A)  // 구분선 (Divider는 기본 컴포넌트 이름과 겹칠 수 있어 Color 접미사 추가)

// ── Button (Aliasing for clarity if needed) ──
val ButtonEnabled    = OrangePrimary      // 활성 버튼 배경
val ButtonDisabledBg = BgElevated         // 비활성 버튼 배경
val ButtonDisabledTx = TextTertiary       // 비활성 버튼 텍스트
val ButtonPressed    = OrangeDark         // 버튼 누르는 순간

// ── Like / Scrap ─────────────────────────
val LikeOn           = OrangePrimary      // 좋아요·스크랩 활성
val LikeOff          = TextTertiary       // 좋아요·스크랩 비활성
