package com.rafael.camapp
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var videoView: VideoView
    private var isUsingFrontCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        videoView = findViewById(R.id.videoView)
        val switchCameraButton: Button = findViewById(R.id.switchCameraButton)

        cameraExecutor = Executors.newSingleThreadExecutor()

        switchCameraButton.setOnClickListener {
            isUsingFrontCamera = !isUsingFrontCamera
            if (isUsingFrontCamera) {
                startVideoPlayback()
            } else {
                startCameraPreview()
            }
        }

        startCameraPreview()
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                // Handle exceptions here
            }
        }, ContextCompat.getMainExecutor(this))

        videoView.visibility = VideoView.GONE
        previewView.visibility = PreviewView.VISIBLE
    }

    private fun startVideoPlayback() {
        previewView.visibility = PreviewView.GONE
        videoView.visibility = VideoView.VISIBLE

        // Set the video to play from raw resources
        val videoUri = "android.resource://" + packageName + "/" + R.raw.chicken
        videoView.setVideoPath(videoUri)
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
