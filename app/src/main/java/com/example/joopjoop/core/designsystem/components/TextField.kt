package com.example.joopjoop.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.joopjoop.R

@Composable
fun CommonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    leadingIcon: Int? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = hint,
                color = Color(0xFF606E83) // 힌트 색상
            )
        },
        // 왼쪽 아이콘
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    modifier = Modifier.padding(start = 10.dp),
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        // 오른쪽 아이콘
        trailingIcon = trailingIcon,
        // 입력창 테두리
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = keyboardOptions,              // 비밀번호 입력시 사용자 편리성을 위한 키보드 (특수문자, 숫자 바로 입력가능)
        visualTransformation = visualTransformation,    // 비밀번호일때 input 값 표시 변경
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTextField() {
    CommonTextField(
        value = "",
        onValueChange = {},
        hint = "힌트 텍스트",
        leadingIcon = R.drawable.ic_lock
    )
}
