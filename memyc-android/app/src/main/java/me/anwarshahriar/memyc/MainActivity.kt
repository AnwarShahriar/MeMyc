package me.anwarshahriar.memyc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Display
import android.view.Surface
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

  private val REQUEST_MEDIA_PROJECTION = 1

  private val requestCaptureButton: Lazy<Button> = lazy { findViewById<Button>(R.id.capture_request_button) }

  private lateinit var mediaProjection: MediaProjection
  private lateinit var surface: Surface
  private lateinit var handler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    init()
  }

  private fun init() {
    requestCaptureButton.value.setOnClickListener {
      val captureIntent = prepareCaptureIntent();
      startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
    }
  }

  private fun prepareCaptureIntent(): Intent {
    return getProjectionManager().createScreenCaptureIntent()
  }

  private fun getProjectionManager(): MediaProjectionManager {
    return getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_MEDIA_PROJECTION) {
      mediaProjection = getProjectionManager().getMediaProjection(resultCode, data)
      createProjectionHandler()
      createVirtualDisplay(mediaProjection)
    }
  }

  private fun createVirtualDisplay(mediaProjection: MediaProjection): VirtualDisplay {
    val displayMetric = resources.displayMetrics
    return mediaProjection.createVirtualDisplay("MeMyc",
        320,
        480,
        displayMetric.densityDpi,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        null,
        null,
        handler)
  }

  private fun createProjectionHandler() {
    val projectionThread = HandlerThread("Projection Thread");
    projectionThread.start()
    handler = Handler(projectionThread.looper)
  }
}
