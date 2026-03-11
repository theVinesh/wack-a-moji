package com.thevinesh.wackamoji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val backgroundMusicController = remember(applicationContext) {
                AndroidBackgroundMusicController(applicationContext)
            }

            App(backgroundMusicController = backgroundMusicController)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}