package com.example.joopjoop.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.joopjoop.ui.theme.OrangePrimary


@Composable
fun JoopJoopButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false, // 로딩 상태 추가
    enabled: Boolean = true      // 활성화 상태 추가
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. 버튼 아래 빛 번짐 효과 (로딩 중이거나 비활성일 때는 투명도 조절 가능)
        if (enabled && !isLoading) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.BottomCenter)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint()
                            val frameworkPaint = paint.asFrameworkPaint()
                            val glowColor = OrangePrimary.copy(alpha = 0.5f) // 투명도 조절

                            frameworkPaint.color = glowColor.toArgb()
                            frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
                                50f, // 번짐 반경
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )

                            canvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                20.dp.toPx(), 20.dp.toPx(), // 모서리 반지름 조절
                                paint
                            )
                        }
                    }
            )
        }
        // 버튼 영역
        Surface(
            onClick = onClick,
            enabled = enabled && !isLoading, // 로딩 중에는 클릭 방지
            shape = RoundedCornerShape(20.dp),
            color = if (enabled) OrangePrimary else Color.Gray, // 비활성 시 색상 변경(선택)
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.TopCenter)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    // 버튼 내부 로딩 인디케이터
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = text,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 프리뷰로 미리보기
@Preview(showBackground = true)
@Composable
fun PreviewJoopJoopButton() {
    JoopJoopButton(text = "줍줍하기", onClick = {}, Modifier)
}