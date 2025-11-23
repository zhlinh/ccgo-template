package com.mojet.app.sample.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data class representing a test button in the grid
 */
data class TestButton(
    val id: String,
    val label: String,
    val onClick: () -> String
)

@Composable
internal fun App() {
    // Log output state
    var logText by remember { mutableStateOf("") }

    // Scroll state for log output
    val scrollState = rememberScrollState()

    // Function to append log messages
    fun appendLog(message: String) {
        val timestamp = getCurrentTimestamp()
        logText += "[$timestamp] $message\n"
    }

    // Define test buttons - users can easily add more buttons by adding to this list
    val testButtons = remember {
        listOf(
            TestButton(
                id = "test1",
                label = "Test 1",
                onClick = {
                    "Test 1 clicked - Platform: ${getPlatformName()}"
                }
            ),
            TestButton(
                id = "test2",
                label = "Test 2",
                onClick = {
                    "Test 2 clicked - Testing native bridge"
                }
            ),
            TestButton(
                id = "test3",
                label = "Test 3",
                onClick = {
                    "Test 3 clicked - Checking integration"
                }
            ),
            TestButton(
                id = "debug_log",
                label = "Enable Debug Log",
                onClick = {
                    // Call native SetDebugLog method
                    try {
                        val result = callNativeSetDebugLog(true)
                        "Debug logging enabled: $result"
                    } catch (e: Exception) {
                        "Error enabling debug log: ${e.message}"
                    }
                }
            ),
            TestButton(
                id = "disable_debug",
                label = "Disable Debug Log",
                onClick = {
                    // Call native SetDebugLog method
                    try {
                        val result = callNativeSetDebugLog(false)
                        "Debug logging disabled: $result"
                    } catch (e: Exception) {
                        "Error disabling debug log: ${e.message}"
                    }
                }
            ),
            TestButton(
                id = "clear_log",
                label = "Clear Log",
                onClick = {
                    logText = ""
                    "Log cleared"
                }
            )
            // Add more buttons here as needed
            // TestButton(
            //     id = "custom_test",
            //     label = "Custom Test",
            //     onClick = { "Custom test result" }
            // )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top section: GridView with buttons (3 columns)
        // Takes up 40% of screen height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(testButtons) { button ->
                    Button(
                        onClick = {
                            val result = button.onClick()
                            appendLog(result)
                        },
                        modifier = Modifier
                            .aspectRatio(1f) // Make buttons square
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text(
                            text = button.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Divider between sections
        Divider(
            color = Color.Gray,
            thickness = 1.dp
        )

        // Bottom section: ScrollView for log output
        // Takes up 60% of screen height
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        ) {
            // Log header
            Surface(
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Log Output",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Scrollable log text area
            Surface(
                color = Color(0xFFF5F5F5),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = if (logText.isEmpty()) "Tap buttons above to see output..." else logText,
                    fontSize = 14.sp,
                    color = if (logText.isEmpty()) Color.Gray else Color.Black,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(12.dp)
                )
            }
        }
    }

    // Auto-scroll to bottom when log text changes
    LaunchedEffect(logText) {
        if (logText.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
}

/**
 * Get current timestamp for logging
 * Platform-specific implementation via expect/actual pattern
 */
expect fun getCurrentTimestamp(): String

/**
 * Call native SetDebugLog method
 * Platform-specific implementation via expect/actual pattern
 */
expect fun callNativeSetDebugLog(enable: Boolean): String
