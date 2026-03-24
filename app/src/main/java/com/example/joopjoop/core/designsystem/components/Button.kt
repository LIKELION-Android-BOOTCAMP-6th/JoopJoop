package com.example.joopjoop.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.joopjoop.core.designsystem.OrangePrimary


@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp),
        contentAlignment = Alignment.Center
    ) {
        // 버튼 아래 빛 번짐 효과
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        val glowColor = OrangePrimary.copy(alpha = 0.5f)

                        frameworkPaint.color = glowColor.toArgb()
                        frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
                            30f,
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )

                        canvas.drawRoundRect(
                            0f, 0f, size.width, size.height,
                            20.dp.toPx(), 20.dp.toPx(),
                            paint
                        )
                    }
                }
        )
        // 버튼 영역
        Surface(
            onClick = onClick,
            enabled = true,
            shape = RoundedCornerShape(20.dp),
            color = OrangePrimary,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.TopCenter)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
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

// 프리뷰로 미리보기
@Preview(showBackground = true)
@Composable
fun PreviewJoopJoopButton() {
    CommonButton(text = "줍줍하기", onClick = {}, Modifier)
}