package greymatter.com.plantsaver

import android.annotation.SuppressLint
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*;

class MainActivity : AppCompatActivity() {
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    private val mCameraRequestCode = 0
    private val mCameraRequestCode2 = 10
    private val mInputSize = 200 //224
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels.txt"
    private var name= ""

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        mClassifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        mCameraButton.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent, mCameraRequestCode)
        }
        mCameraButton2.setOnClickListener {
            val callCameraIntent2 = Intent(Intent.ACTION_PICK).setType("image/*")
            startActivityForResult(callCameraIntent2, mCameraRequestCode2)
        }
        mResultTextView.setOnClickListener{
            if(mResultTextView.text.toString().equals("Diagnosis of Plant Diseases")){

            }else{
                val callDetailsIntent = Intent(this, WebActivity::class.java).putExtra("name",name)
                startActivity(callDetailsIntent)
            }

        }
       /* val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(callCameraIntent, mCameraRequestCode)

        */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == mCameraRequestCode){
            if(resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                mPhotoImageView.setImageBitmap(mBitmap)
                val model_output = mClassifier.recognizeImage(scaleImage(mBitmap)).firstOrNull()
                name = model_output?.title.toString()
                mResultTextView.text = "Disease: " + model_output?.title + "\n" + "Accuracy: " + model_output?.confidence
            }
        }

        if(requestCode == mCameraRequestCode2){
            if(resultCode == Activity.RESULT_OK && data != null) {
                try {
                    val mBitmap = MediaStore.Images.Media.getBitmap(contentResolver,data?.data) as Bitmap
                    this.mBitmap = scaleImage(mBitmap)
                    mPhotoImageView.setImageBitmap(mBitmap)
                    val model_output = mClassifier.recognizeImage(scaleImage(mBitmap)).firstOrNull()
                    name = model_output?.title.toString()
                    mResultTextView.text = "Disease: " + model_output?.title + "\n"+ "Accuracy: " + model_output?.confidence
                }catch (e:Exception){

                }

            }
        }
    }

    fun scaleImage(bitmap: Bitmap?): Bitmap {
        val width = bitmap!!.width
        val height = bitmap.height
        val scaledWidth = mInputSize.toFloat() / width
        val scaledHeight = mInputSize.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaledWidth, scaledHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }
}

