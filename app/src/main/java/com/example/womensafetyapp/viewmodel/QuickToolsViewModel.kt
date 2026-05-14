package com.example.womensafetyapp.viewmodel

import android.content.Context
import android.hardware.camera2.CameraManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.womensafetyapp.data.model.ToolItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickToolsViewModel : ViewModel() {

    // -----------------------------
    // QUICK TOOLS
    // -----------------------------

    var tools by mutableStateOf(

        listOf(

            ToolItem(
                "🎥",
                "Stealth Recorder",
                "Record audio & video silently while screen is off",
                "LIVE",
                "#E8325A"
            ),

            ToolItem(
                "🔦",
                "Strobe SOS",
                "Flash light in SOS pattern to attract attention"
            ),

            ToolItem(
                "🎭",
                "Fake Call",
                "Simulate an incoming call to exit unsafe situations",
                "NEW",
                "#9333EA"
            ),

            ToolItem(
                "📊",
                "Area Heat Map",
                "View community-reported danger spots nearby"
            ),

            ToolItem(
                "🤝",
                "Safety Buddy",
                "Match with nearby verified SheShield users"
            ),

            ToolItem(
                "🎙️",
                "Voice Trigger",
                "Set a code word to instantly trigger SOS"
            )
        )
    )
        private set

    // -----------------------------
    // STROBE STATE
    // -----------------------------

    var strobeActive by mutableStateOf(false)
        private set

    // -----------------------------
    // TOGGLE STROBE
    // -----------------------------

    fun toggleStrobe(
        context: Context
    ) {

        strobeActive = !strobeActive

        if (strobeActive) {

            triggerStrobeSOS(context)
        }
    }

    // -----------------------------
    // STROBE SOS
    // -----------------------------

    private fun triggerStrobeSOS(
        context: Context
    ) {

        val cameraManager =
            context.getSystemService(
                Context.CAMERA_SERVICE
            ) as? CameraManager ?: return

        val cameraId = try {

            cameraManager.cameraIdList.firstOrNull()

        } catch (e: Exception) {

            null
        } ?: return

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val sosPattern = listOf(

                    200L,
                    200L,
                    200L,

                    600L,
                    600L,
                    600L,

                    200L,
                    200L,
                    200L
                )

                repeat(3) {

                    if (!strobeActive)
                        return@repeat

                    sosPattern.forEach { duration ->

                        if (!strobeActive)
                            return@forEach

                        cameraManager.setTorchMode(
                            cameraId,
                            true
                        )

                        delay(duration)

                        cameraManager.setTorchMode(
                            cameraId,
                            false
                        )

                        delay(120)
                    }

                    delay(1000)
                }

            } catch (_: Exception) {

            }

            finally {

                withContext(Dispatchers.Main) {

                    strobeActive = false

                    try {

                        cameraManager.setTorchMode(
                            cameraId,
                            false
                        )

                    } catch (_: Exception) {

                    }
                }
            }
        }
    }
}