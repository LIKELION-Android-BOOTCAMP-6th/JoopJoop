package com.example.joopjoop.feature.auth.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.joopjoop.R
import com.example.joopjoop.core.designsystem.components.CommonTextField

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit, // ViewModel 등에서 정의한 토글 함수
    modifier: Modifier = Modifier,
    hint: String = ""
) {
    CommonTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        hint = hint,
        leadingIcon = R.drawable.ic_lock,
        // 우측 아이콘 슬롯에 사용자님의 기존 로직 배치
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    painter = painterResource(
                        id = if (isPasswordVisible) R.drawable.ic_visibility_off
                        else R.drawable.ic_visibility
                    ),
                    contentDescription = "Toggle Password Visibility",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        },
        // 비밀번호 표시 상태에 따라 마스킹 여부 결정
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordField() {
    PasswordField(
        value = "password123",
        onValueChange = {},
        isPasswordVisible = false,
        onTogglePasswordVisibility = {},
        hint = "비밀번호를 입력해주세요"
    )
}