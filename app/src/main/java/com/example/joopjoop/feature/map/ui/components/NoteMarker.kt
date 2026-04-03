package com.example.joopjoop.feature.map.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextTertiary
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


// 지도 위에 표시될 개별 쪽지 마커 컴포넌트

@Composable
fun NoteMarkers(
    pickableNotes: List<Note>,
    distantNotes: List<Note>,
    onNoteClick: (String) -> Unit
) {
    val context = LocalContext.current

    // [성능의 핵심] 주황색/회색 아이콘을 딱 한 번만 그려서 메모리에 저장
    val pickableIcon = remember { createCustomMarkerDescription(context, OrangePrimary) }
    val distantIcon = remember { createCustomMarkerDescription(context, TextTertiary) }

    // 줍기 가능한 쪽지들
    pickableNotes.forEach { note ->
        key(note.id) {
            Marker(
                state = MarkerState(
                    position = LatLng(
                        note.location.latitude,
                        note.location.longitude
                    )
                ),
                icon = pickableIcon, // 미리 저장해둔 비트맵 사용
                title = note.id,
                onClick = {
                    onNoteClick(note.id)
                    true
                }
            )
        }
    }

    // 거리가 먼 쪽지들
    distantNotes.forEach { note ->
        key(note.id) {
            Marker(
                state = MarkerState(
                    position = LatLng(
                        note.location.latitude,
                        note.location.longitude
                    )
                ),
                icon = distantIcon, // 미리 저장해둔 비트맵 사용
                title = note.id,
                onClick = {
                    /*TODO 거리가 멀면 클릭 안되게 하거나 안내 메시지 */
                    true
                } // 클릭 차단 혹은 안내
            )
        }
    }
}

// Compose 디자인을 Google Maps용 Bitmap으로 변환
fun createCustomMarkerDescription(
    context: Context,
    backgroundColor: Color
): BitmapDescriptor {
    val size = 110 // 40.dp 정도의 크기를 픽셀로 환산 (해상도에 따라 조절 가능)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 1. 원형 배경 그리기
    val paint = android.graphics.Paint().apply {
        color = backgroundColor.toArgb()
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // 2. 메일 아이콘 그리기 (Vector Drawable을 비트맵 위에 얹음)
    val drawable = ContextCompat.getDrawable(
        context,
        android.R.drawable.ic_dialog_email
    )
    drawable?.let {
        it.setTint(android.graphics.Color.WHITE)
        val margin = size / 4
        it.setBounds(margin, margin, size - margin, size - margin)
        it.draw(canvas)
    }

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}