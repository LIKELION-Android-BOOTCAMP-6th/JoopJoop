package com.example.joopjoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.feature.note.ui.detail.NoteDetailScreen
import com.example.joopjoop.feature.note.ui.list.NoteListScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "noteList") {
                composable("noteList") { NoteListScreen(navController = navController) }
                composable("noteDetail/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                    NoteDetailScreen(navController = navController, noteId = noteId) }
                composable("writeNote") {
                    val viewModel: WriteNoteViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    LaunchedEffect(uiState.isSubmitSuccess) {
                        if (uiState.isSubmitSuccess && uiState.createdNoteId != null) {
                            val newId = uiState.createdNoteId
                            viewModel.resetNote() // 상태 초기화

                            // 작성 화면은 스택에서 제거하고 상세 화면으로 이동
                            navController.navigate("noteDetail/$newId") {
                                popUpTo("writeNote") { inclusive = true }
                            }
                        }
                    }
                    WriteNoteScreen(
                        navController = navController,
                        uiState = uiState,
                        onCategorySelected = viewModel::onCategorySelected,
                        onContentChange = viewModel::onContentChange,
                        onIncreaseHours = viewModel::increaseHours,
                        onDecreaseHours = viewModel::decreaseHours,
                        onBackClick = { navController.popBackStack() },
                        onLeaveNoteClick = viewModel::submitNote
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JoopJoopTheme {
    }
}