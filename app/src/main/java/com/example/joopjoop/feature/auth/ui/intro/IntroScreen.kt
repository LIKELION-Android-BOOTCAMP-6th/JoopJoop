package com.example.joopjoop.feature.auth.ui.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.R
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextSecondary

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단 - 텍스트
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 80.dp)
        ) {
            Text(
                text = stringResource(R.string.app_description),
                color = TextSecondary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = TextPrimary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.sub_name),
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black
            )
        }

        // 중간 - 로고
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // 배경 큰 원
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color(0xFF2D1E14).copy(alpha = 0.5f), CircleShape)
            )
            // 중앙 아이콘 배경
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(OrangePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_compass),
                    contentDescription = null,
                    tint = BgDarkest,
                    modifier = Modifier.size(60.dp)
                )
            }
            // 작은 쪽지/메일 아이콘
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(top = 20.dp, end = 20.dp)
                    .align(Alignment.CenterEnd)
                    .background(OrangePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = null,
                    tint = BgDarkest,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 하단 - 로그인으로 가는  버튼
        Column(
            modifier = Modifier.padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.go_to_login),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = TextSecondary)) {
                        append(stringResource(R.string.dont_have_account))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(R.string.create_account))
                    }
                },
                modifier = Modifier.clickable { onSignupClick() },
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    JoopJoopTheme {
        IntroScreen()
    }
}
