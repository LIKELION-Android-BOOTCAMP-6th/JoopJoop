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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.feature.note.ui.detail.NoteDetailScreen
import com.example.joopjoop.feature.note.ui.list.NoteListScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "noteList") {
                composable("noteList") { NoteListScreen(navController = navController) }
                composable("noteDetail") { NoteDetailScreen(navController = navController) }
                composable("writeNote") {
                    var uiState by remember {
                        mutableStateOf(
                            WriteNoteUiState(
                                selectedCategory = "일상",
                                noteContent = "",
                                storageHours = 12
                            )
                        )
                    }
                    WriteNoteScreen(
                        navController = navController,
                        uiState = uiState,
                        onCategorySelected = { uiState = uiState.copy(selectedCategory = it) },
                        onContentChange = { uiState = uiState.copy(noteContent = it) },
                        onHoursChange = { uiState = uiState.copy(storageHours = it) },
                        onBackClick = { navController.popBackStack() }
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