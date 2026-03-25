package com.example.joopjoop.feature.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.joopjoop.feature.map.ui.MapScreen
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MapTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoopJoopTheme {
                MapScreen()
            }
        }
    }
}
