package com.example.womensafetyapp.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File

class AudioRecorder(
    private val context: Context
) {

    private var recorder: MediaRecorder? = null

    private var audioFile: File? = null

    fun startRecording() {

        try {

            audioFile = File(
                context.getExternalFilesDir(null),
                "sos_recording.mp3"
            )

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

                setOutputFile(audioFile!!.absolutePath)

                prepare()

                start()

                Log.d(
                    "SOS_AUDIO",
                    "Recording Started"
                )

                // AUTO STOP AFTER 10 SEC
                Handler(Looper.getMainLooper()).postDelayed({

                    stopRecording()

                }, 10000)
            }

        } catch (e: Exception) {

            Log.e(
                "SOS_AUDIO",
                "Recording Failed",
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
            }

            recorder = null

            Log.d(
                "SOS_AUDIO",
                "Recording Stopped"
            )

        } catch (e: Exception) {

            Log.e(
                "SOS_AUDIO",
                "Stop Failed",
                e
            )

            e.printStackTrace()
        }
    }

    fun getRecordingFile(): File? {

        return audioFile
    }
}