package com.example.joopjoop.core.model

// DialogState.kt (공통 파일)
data class DialogState(
    val title: String,
    val description: String,
    val confirmText: String = "확인",
    val dismissText: String? = null,
    val onConfirm: () -> Unit,
    val onDismiss: (() -> Unit)? = null
)