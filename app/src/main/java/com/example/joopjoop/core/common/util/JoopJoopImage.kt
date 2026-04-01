package com.example.joopjoop.core.common.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.joopjoop.R

/**
 * [출력 전용 유틸] JoopJoopImage
 * 화면에 이미지를 표시할 때 사용하며, 전역 설정된 캐시(Memory/Disk)를 자동으로 활용합니다.
 *
 * @param model 이미지 소스 (URL, Uri, Resource ID 등)
 * @param contentDescription 시각 장애인을 위한 설명 (필요 없으면 null)
 * @param modifier 크기, 모양, 테두리 등 스타일 지정
 * @param contentScale 이미지 맞춤 설정 (기본값: Crop - 영역 채우기)
 */
@Composable
fun JoopJoopImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isBlurred: Boolean = false
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        // 로딩 중
        loading = {
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        // 에러 시
        error = {
            Image(
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                contentScale = contentScale
            )
        },
        // 2. 성공 시점에만 블러를 적용하도록 변경
        success = { state ->
            Image(
                painter = state.painter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .matchParentSize()
                    .then(if (isBlurred) Modifier.blur(10.dp) else Modifier),
                contentScale = contentScale
            )
        }
    )
}