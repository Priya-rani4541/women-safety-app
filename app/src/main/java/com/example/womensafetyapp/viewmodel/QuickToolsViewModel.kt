package com.example.womensafetyapp.viewmodel

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.data.model.ToolItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickToolsViewModel : ViewModel() {
    private val _tools = mutableStateOf(
        listOf(
            ToolItem("🎥", "Stealth Recorder", "Record audio & video silently while screen is off", "LIVE", Color(0xFFE8325A)),
            ToolItem("🔦", "Strobe SOS", "Flash light in SOS pattern to attract attention"),
            ToolItem("🎭", "Fake Call", "Simulate an incoming call to exit situations", "NEW", Color(0xFF9B32D6)),
            ToolItem("📊", "Area Heat Map", "View community-reported danger spots nearby"),
            ToolItem("🤝", "Safety Buddy", "Match with nearby verified SheShield users"),
            ToolItem("🎙️", "Voice Trigger", "Set a code word to instantly trigger SOS"),
        )
    )
    val tools: State<List<ToolItem>> = _tools

    private val _strobeActive = mutableStateOf(false)
    val strobeActive: State<Boolean> = _strobeActive

    fun toggleStrobe(context: Context) {
        _strobeActive.value = !_strobeActive.value
        if (_strobeActive.value) {
            triggerStrobeSOS(context)
        }
    }

    private fun triggerStrobeSOS(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        val cameraId = try { cameraManager.cameraIdList.firstOrNull() } catch (e: Exception) { null } ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            val sosTiming = listOf(200L, 200L, 200L, 600L, 600L, 600L, 200L, 200L, 200L)
            repeat(3) {
                if (!_strobeActive.value) return@repeat
                sosTiming.forEach { duration ->
                    if (!_strobeActive.value) return@forEach
                    try {
                        cameraManager.setTorchMode(cameraId, true)
                        delay(duration)
                        cameraManager.setTorchMode(cameraId, false)
                        delay(100)
                    } catch (_: Exception) {}
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                _strobeActive.value = false
            }
        }
    }
}
