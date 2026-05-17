package com.example.womensafetyapp.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import android.os.Handler
import android.os.Looper

class AudioRecorder(
    private val context: Context
) {

    private var recorder: MediaRecorder? = null

    private var outputFile: String = ""

    fun startRecording() {

        try {

            outputFile =
                "/storage/emulated/0/Download/sos_recording.mp3"

            recorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    MediaRecorder()
                }

            recorder?.apply {

                setAudioSource(MediaRecorder.AudioSource.MIC)

                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                setOutputFile(outputFile)

                // ✅ DEBUG LOG
                Log.d("SOS_AUDIO", "startRecording called")

                prepare()

                start()

                // ✅ DEBUG LOG
                Log.d(
                    "SOS_AUDIO",
                    "recording started successfully"
                )

                Handler(Looper.getMainLooper()).postDelayed({

                    stopRecording()

                }, 10000)
            }

        } catch (e: Exception) {

            // ✅ DEBUG ERROR LOG
            Log.e(
                "SOS_AUDIO",
                "recording failed",
                e
            )

            e.printStackTrace()
        }
    }

    fun stopRecording() {

        try {

            recorder?.apply {

                stop()

                release()
                Log.d(
                    "SOS_AUDIO",
                    "recording stopped successfully"
                )
            }

            recorder = null

            Log.d(
                "SOS_AUDIO",
                "recording stopped successfully"
            )

        } catch (e: Exception) {

            Log.e(
                "SOS_AUDIO",
                "stop recording failed",
                e
            )

            e.printStackTrace()
        }
    }

    fun getRecordingFile(): File {

        return File(outputFile)
    }
}