package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Rekam : AppCompatActivity() {
    private lateinit var mPhoto: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mRekam: Button
    private lateinit var mCek: Button
    private var currentPhotoPath: String = ""

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekam)
        init()
        mRekam.setOnClickListener { dispatchTakePictureIntent() }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try { createImageFile() } catch (ex: IOException) { null }
            photoFile?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    fun init() {
        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
        mPhoto = findViewById(R.id.photo)
        mRekam = findViewById(R.id.btnRekam)
        mCek = findViewById(R.id.btnCek)
        mRekam.visibility = View.VISIBLE
        mCek.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val targetW = mPhoto.width
            val targetH = mPhoto.height
            val bmOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight
            val scaleFactor = minOf(photoW / targetW, photoH / targetH)
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            val converetdImage = getResizedBitmap(bitmap, 1024)

            val stream = ByteArrayOutputStream()
            converetdImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            mPhoto.setImageBitmap(converetdImage)
            mCek.visibility = View.VISIBLE

            mCek.setOnClickListener {
                tampilLoading()
                val myId = applicationContext.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
                    .getString("id_user", null)
                val idUser = RequestBody.create(MediaType.parse("multipart/form-data"), myId ?: "")
                val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), byteArray)
                val filePart = MultipartBody.Part.createFormData("upload", currentPhotoPath, requestBody)
                APIService.Factory.create(applicationContext).rekamWajah(idUser, filePart)
                    .enqueue(object : Callback<MessageModel> {
                        override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                            hideLoading()
                            if (response.isSuccessful) {
                                if (SessionHelper.catatrekam(this@Rekam)) {
                                    startActivity(Intent(this@Rekam, Checkpoint::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                    finish()
                                }
                            } else {
                                tampilPesan(ErrorHelper.parseError(response).message)
                            }
                        }
                        override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                            hideLoading()
                            tampilPesan(t.message ?: "")
                        }
                    })
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".png", storageDir).also {
            currentPhotoPath = it.absolutePath
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun tampilPesan(pesan: String) {
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_LONG).show()
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
}
