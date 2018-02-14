package com.codenderitu.textrecognition

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var cameraView: SurfaceView
    lateinit var txtValue : TextView
    lateinit var cameraSource : CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.surfaceView)
        txtValue = findViewById(R.id.txtValue)

        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Text Recognizer is not functional", Toast.LENGTH_LONG).show()
        }

        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0.toFloat())
                .setAutoFocusEnabled(true)
                .build()
        cameraView.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    if (checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), 1)
                    }
                    cameraSource.start(cameraView.holder)
                }catch (ex : IOException){
                    ex.printStackTrace()
                }
            }
        })

        textRecognizer.setProcessor(object: Detector.Processor<TextBlock>{
            override fun release() {

            }

            override fun receiveDetections(p0: Detector.Detections<TextBlock>?) {
//                Toast.makeText(applicationContext, "Receive Detections", Toast.LENGTH_LONG).show()
                val items : SparseArray<TextBlock> = p0?.detectedItems!!
                if(items.size() != 0){
                    txtValue.post {
                        var value = StringBuilder()
                        for (i in 0 until items.size()) {
                            val item = items.valueAt(i)
                            value.append(item.value)
                            value.append("\n")
                        }
                        txtValue.text = value.toString()

                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.release()
    }
}
