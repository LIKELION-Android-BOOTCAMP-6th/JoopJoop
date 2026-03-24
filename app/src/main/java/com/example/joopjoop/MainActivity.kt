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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.note.NoteDetailScreen
import com.example.joopjoop.note.NoteListScreen
import com.example.joopjoop.note.WriteNoteScreen
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
                composable("writeNote") { WriteNoteScreen(navController = navController) }
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