package com.graphiteplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.graphiteplate.ui.nav.GraphitePlateNavGraph
import com.graphiteplate.ui.theme.PaperBackground
import com.graphiteplate.ui.theme.SketchTheme

/**
 * Single-activity host. Compose handles every screen; Material 3's
 * [Scaffold] is used only to surface safe-area insets so that the
 * textured paper background can extend edge-to-edge while real content
 * still avoids the system bars.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GraphitePlateApp()
        }
    }
}

@Composable
private fun GraphitePlateApp() {
    SketchTheme {
        Scaffold(containerColor = Color.Transparent) { insetPadding ->
            // Paper texture spans the full window so the grain reaches
            // the screen edges; nav content is inset by Scaffold's
            // padding so it doesn't slide under status/nav bars.
            PaperBackground(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.padding(insetPadding)) {
                    GraphitePlateNavGraph()
                }
            }
        }
    }
}
