package com.example.kinetic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kinetic.ui.screens.WorkoutsListScreen
import com.example.kinetic.ui.screens.defaultWorkoutListState
import com.example.kinetic.ui.theme.AestheticVariant
import com.example.kinetic.ui.theme.KineticAestheticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KineticAestheticTheme(variant = AestheticVariant.Light) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorkoutsListScreen(
                        state = defaultWorkoutListState,
                        onWorkoutClick = {},
                        onWatchClick = {},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    KineticAestheticTheme(variant = AestheticVariant.Light) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            WorkoutsListScreen(
                state = defaultWorkoutListState,
                onWorkoutClick = {},
                onWatchClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
