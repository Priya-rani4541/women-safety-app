package com.example.womensafetyapp.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private var acceleration = 10f
    private var currentAcceleration =
        SensorManager.GRAVITY_EARTH

    private var lastAcceleration =
        SensorManager.GRAVITY_EARTH

    private var shakeCount = 0

    private var lastShakeTime = 0L

    private var lastTriggerTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {

        event ?: return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        lastAcceleration = currentAcceleration

        currentAcceleration =
            sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta = currentAcceleration - lastAcceleration

        acceleration = acceleration * 0.9f + delta

        // MUCH STRONGER SHAKE REQUIRED
        if (acceleration > 18) {

            val currentTime =
                System.currentTimeMillis()

            // Reset if delay too long
            if (currentTime - lastShakeTime > 1500) {

                shakeCount = 0
            }

            shakeCount++

            lastShakeTime = currentTime

            // REQUIRE 3 SHAKES
            if (shakeCount >= 3) {

                // 5 sec cooldown
                if (
                    currentTime - lastTriggerTime > 5000
                ) {

                    lastTriggerTime = currentTime

                    shakeCount = 0

                    onShakeDetected()
                }
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {}
}