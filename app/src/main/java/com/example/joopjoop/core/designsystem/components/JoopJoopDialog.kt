package com.example.joopjoop.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgElevated
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangeLight
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextSecondary

/**
 * [JoopJoopDialog] 적용 및 배치 가이드
 * * 1. 데이터 구조 (DialogState)
 * - 다이얼로그의 콘텐츠와 로직은 ViewModel의 UiState 내 [DialogState] 객체로 관리합니다.
 * - 필드: title, description, confirmText, dismissText(null이면 버튼 1개), onConfirm, onDismiss, icon
 *
 * 2. UI 배치 원칙 (Z-Index 관리)
 * - Compose의 선언 순서에 따른 레이어 중첩 원리를 이용합니다.
 * - ✅ 중요: 반드시 호출 화면(예: MapScreen)의 최상위 [Box] 내에서 '가장 마지막 요소'로 배치해야
 * 지도나 버튼 등 다른 UI 요소보다 항상 위에 나타납니다.
 *
 * 3. 작동 흐름 (State-Driven)
 * - Trigger: ViewModel에서 [_uiState.dialogState]에 데이터를 주입하여 노출.
 * - Dismiss: [onDismissRequest] 콜백 내에서 [viewModel.dismissDialog()]를 호출해
 * 상태를 null로 초기화하여 다이얼로그를 닫습니다.
 *
 * [배치 예시]
 * Box(modifier = Modifier.fillMaxSize()) {
 * GoogleMap(...)           // 레이어 1
 * TopButtons(...)          // 레이어 2
 * BottomCards(...)         // 레이어 3
 *
 * // 최하단 배치로 최상단 레이어 보장
 * uiState.dialogState?.let { state ->
 * JoopJoopDialog(
 * onDismissRequest = { viewModel.dismissDialog() },
 * title = state.title,
 * // ... 매개변수 매핑
 * )
 * }
 * }
 */

@Composable
fun JoopJoopDialog(
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    confirmText: String = "확인",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    icon: Painter? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // 우리 테마의 BgDark를 사용하여 카드 배경 설정
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = BgDark, // 우리 앱의 카드/바텀시트 배경색
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 아이콘 (OrangeLight로 강조)
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = OrangeLight
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. 제목 (TextPrimary: 흰색 계열)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. 설명문 (TextSecondary: 연한 갈색/베이지 계열)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4 // 행간 확보
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 취소 버튼 (있을 경우에만 노출, 보조적인 느낌의 BgElevated 사용)
                    if (dismissText != null) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDismiss?.invoke()
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BgElevated,
                                contentColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Text(text = dismissText, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // 확인 버튼 (OrangePrimary로 강력한 강조)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onConfirm()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text(text = confirmText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "1. 기본 에러 다이얼로그 (버튼 1개)")
@Composable
fun PreviewBasicDialog() {
    JoopJoopTheme { // 프로젝트의 테마 컴포저블로 감싸주세요.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            JoopJoopDialog(
                onDismissRequest = {},
                title = "인터넷 연결 확인",
                description = "네트워크 연결이 불안정합니다.\n잠시 후 다시 시도해주세요.",
                confirmText = "확인",
                onConfirm = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "2. 선택 다이얼로그 (버튼 2개)")
@Composable
fun PreviewChoiceDialog() {
    JoopJoopTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            JoopJoopDialog(
                onDismissRequest = {},
                title = "로그아웃 하시겠어요?",
                description = "로그아웃 시 쪽지 알림을\n받으실 수 없습니다.",
                confirmText = "로그아웃",
                dismissText = "취소",
                onConfirm = {},
                onDismiss = {} // 취소 버튼 클릭 시 동작
            )
        }
    }
}

@Preview(showBackground = true, name = "3. 아이콘 포함 다이얼로그")
@Composable
fun PreviewIconDialog() {
    JoopJoopTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            JoopJoopDialog(
                onDismissRequest = {},
                icon = painterResource(id = android.R.drawable.ic_menu_mylocation), // 예시 아이콘
                title = "위치 권한 허용",
                description = "주변 쪽지를 탐색하기 위해\n위치 권한이 필요합니다.",
                confirmText = "설정으로 이동",
                onConfirm = {}
            )
        }
    }
}