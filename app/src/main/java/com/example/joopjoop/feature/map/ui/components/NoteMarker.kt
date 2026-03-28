package com.example.joopjoop.feature.map.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.joopjoop.feature.map.ui.NoteDTO
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextTertiary
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState

/**
 * 지도 위에 표시될 개별 쪽지 마커 컴포넌트
 * @param isPickable 내 위치 기준 30m 이내 여부에 따라 색상 변경
 */
@Composable
fun NoteMarker(
    note: NoteDTO, isPickable: Boolean, onClick: () -> Unit = {}
) {
    // 마커의 고유 아이디를 키로 설정하여 성능 최적화
    key(note.id) {
        MarkerComposable(
            state = MarkerState(position = LatLng(note.latitude, note.longitude)),
            title = note.id,
            onClick = {
                onClick()
                true // true를 반환해야 지도의 기본 동작(카메라 중앙 이동 등)을 제어할 수 있다.
            }) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isPickable) OrangePrimary else TextTertiary, // 주황(줍기 가능) vs 회색(멂)
                        shape = CircleShape
                    )
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Email, null, tint = Color.White)
            }
        }
    }
}