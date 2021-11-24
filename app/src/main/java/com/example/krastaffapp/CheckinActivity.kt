package com.example.krastaffapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
//import kotlinx.android.synthetic.main.activity_checkin_indv.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CheckinActivity : AppCompatActivity(){


    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var progressBar: ProgressBar? = null




    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin_indv)

        checkCameraPermission()
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar


        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))


    }

    inner class MyImageAnalyzer: ImageAnalysis.Analyzer {



        override fun analyze(imageProxy: ImageProxy) {
            scanBarcode(imageProxy)
        }

        @SuppressLint("UnsafeOptInUsageError")
        private fun scanBarcode(imageProxy: ImageProxy) {
            imageProxy.image?.let { image ->
                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()
                scanner.process(inputImage)
                        .addOnCompleteListener {
                            imageProxy.close()
                            if (it.isSuccessful) {
                                readBarcodeData(it.result as List<Barcode>)
                            } else {
                                it.exception?.printStackTrace()
                            }
                        }
            }
            imageProxy.close()
        }

         private fun readBarcodeData(barcodes: List<Barcode>) {
            for (barcode in barcodes) {
                when (barcode.valueType) {
                    //you can check if the barcode has other values
                    //For now I am using it just for URL
                    Barcode.TYPE_TEXT -> {
                        val staffno = barcode.rawValue?.toString()
//                        Toast.makeText(this@CaptureActivity, "StaffID: $staffno", Toast.LENGTH_SHORT).show()
                        val sharedPref: SharedPreferences = this@CheckinActivity.getPreferences(Context.MODE_PRIVATE)

                        sharedPref.edit()?.putString("staffno", staffno)?.apply()
                        checkin()

                    }


                }

            }
        }


    }

        var volleyRequestQueue: RequestQueue? = null
        val serverAPIURL: String = "http://192.168.137.1/qr/checkin.php"
        val TAG = "KRA-Check-In"


        fun checkin() {
            val sharedPref: SharedPreferences = this@CheckinActivity.getPreferences(Context.MODE_PRIVATE)

            val staffNoHolder = sharedPref.getString("staffno","").toString()

            progressBar?.visibility = View.VISIBLE

            volleyRequestQueue = Volley.newRequestQueue(this)
            val parameters: MutableMap<String, String> = HashMap()
            // Add your parameters in HashMap
            parameters.put("staffno", staffNoHolder)


            val strReq: StringRequest = object : StringRequest(
                Method.POST,serverAPIURL,
                Response.Listener { response ->
                    Log.e(TAG, "response: $response")

                    // Handle Server response here
                    try {
//                        val responseObj = JSONObject(response)
//                        val resp = responseObj.getString("")
//                        if (responseObj.equals("Success")) {
//                            progressBar?.visibility = View.INVISIBLE
//                            // Handle your server response data here
//                        }
                        Toast.makeText(this,"Checked in successfully.",Toast.LENGTH_LONG).show()
                        progressBar?.visibility = View.INVISIBLE
                        finish()

                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred", e)
                        e.printStackTrace()
                        progressBar?.visibility = View.INVISIBLE

                    }
                },
                Response.ErrorListener { volleyError -> // error occurred
                    progressBar?.visibility = View.INVISIBLE

                    Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
                }) {

                override fun getParams(): MutableMap<String, String> {
                    return parameters;
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {

                    val headers: MutableMap<String, String> = HashMap()
                    // Add your Header paramters here
                    return headers
                }
            }
            // Adding request to request queue
            volleyRequestQueue?.add(strReq)
    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            Intent().also {
                it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", packageName, null)
                startActivity(it)
                finish()
            }
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
                .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
//        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview
        )

        val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        imageAnalysis.setAnalyzer(cameraExecutor, MyImageAnalyzer())

        cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                imageAnalysis,
                preview
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}

