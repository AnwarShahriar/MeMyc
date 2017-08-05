package me.anwarshahriar.memyc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Button

class MainActivity : AppCompatActivity() {

  private val REQUEST_MEDIA_PROJECTION = 1

  private val requestCaptureButton: Lazy<Button> = lazy { findViewById<Button>(R.id.capture_request_button) }

  private var mediaProjection: MediaProjection? = null
  private lateinit var handler: Handler
  private val callback = Callback()
  private lateinit var virtualDisplay: VirtualDisplay
  private lateinit var projectionThread: HandlerThread
  private lateinit var imageTransformer: ImageTransformer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    init()
  }

  private fun init() {
    requestCaptureButton.value.setOnClickListener {
      val captureIntent = prepareCaptureIntent()
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
      createProjectionHandler()
      mediaProjection = getProjectionManager().getMediaProjection(resultCode, data)
      mediaProjection?.registerCallback(Callback(), handler)
      virtualDisplay = createVirtualDisplay(mediaProjection!!)
      virtualDisplay.surface
    }
  }

  private class Callback: MediaProjection.Callback() {
    override fun onStop() {
      Log.d("MeMyc", "Projection stopped");
      // todo: clean up the mess here
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaProjection?.unregisterCallback(callback)
  }

  private fun createVirtualDisplay(mediaProjection: MediaProjection): VirtualDisplay {
    val displayMetric = resources.displayMetrics
    imageTransformer = ImageTransformer(handler, windowManager)
    return mediaProjection.createVirtualDisplay("MeMyc",
        imageTransformer.width,
        imageTransformer.height,
        displayMetric.densityDpi,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        imageTransformer.imageReader.surface,
        null,
        handler)
  }

  private fun createProjectionHandler() {
    projectionThread = HandlerThread("Projection Thread")
    projectionThread.start()
    handler = Handler(projectionThread.looper)
  }

  private class ImageTransformer(handler: Handler, windowManager: WindowManager): ImageReader.OnImageAvailableListener {
    var width: Int = 0
    var height: Int = 0
    var imageReader: ImageReader
    var count = 0

    init {
      val display = windowManager.defaultDisplay
      val size = Point()

      display.getSize(size)

      var width = size.x
      var height = size.y

      while ((width * height) > (2 shl 19)) {
        width = (width shr 1)
        height = (height shr 1)
      }

      this.width = width
      this.height = height

      imageReader = ImageReader.newInstance(width, height,
            PixelFormat.RGBA_8888, 30)
      imageReader.setOnImageAvailableListener(this, handler)
    }

    override fun onImageAvailable(p0: ImageReader?) {
      count++

      val image = imageReader.acquireLatestImage()
      Log.d("MeMyc", "Got image: ${count}")
      image?.close()
    }
  }
}
