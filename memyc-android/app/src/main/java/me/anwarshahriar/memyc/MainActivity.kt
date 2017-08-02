package me.anwarshahriar.memyc

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

  var screenCaptureIntent: Intent? = null

  val requestCaptureButton: Lazy<Button> = lazy { findViewById<Button>(R.id.capture_request_button) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    init()
  }

  private fun init() {
    prepareCaptureIntent()
    requestCaptureButton.value.setOnClickListener {
      Toast.makeText(this, "Hello Memyc", Toast.LENGTH_LONG).show()
    }
  }

  private fun prepareCaptureIntent() {
    val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    screenCaptureIntent = projectionManager.createScreenCaptureIntent()
  }
}
