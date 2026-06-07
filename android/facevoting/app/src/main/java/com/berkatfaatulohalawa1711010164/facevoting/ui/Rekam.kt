package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.core.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.FaceViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Rekam : AppCompatActivity() {
    private lateinit var mPhoto: ImageView
    private lateinit var progressDialog: AlertDialog
    private lateinit var mRekam: Button
    private lateinit var mCek: Button
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var currentPhotoPath: String = ""

    private val viewModel: FaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekam)
        init()
        mRekam.setOnClickListener { dispatchTakePictureIntent() }

        viewModel.rekamState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    SessionHelper.catatrekam(this)
                    startActivity(Intent(this, Checkpoint::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File = try { createImageFile() } catch (ex: IOException) { return }
        val photoUri = FileProvider.getUriForFile(
            applicationContext,
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    private fun init() {
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
        mPhoto = findViewById(R.id.photo)
        mRekam = findViewById(R.id.btnRekam)
        mCek = findViewById(R.id.btnCek)
        mRekam.visibility = View.VISIBLE
        mCek.visibility = View.GONE

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (!success) return@registerForActivityResult

            val bmOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            val targetW = mPhoto.width
            val targetH = mPhoto.height
            val scaleFactor = minOf(bmOptions.outWidth / targetW, bmOptions.outHeight / targetH)
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor

            val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            val convertedImage = getResizedBitmap(bitmap, 1024)
            val stream = ByteArrayOutputStream()
            convertedImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            mPhoto.setImageBitmap(convertedImage)
            mCek.visibility = View.VISIBLE

            mCek.setOnClickListener {
                val myId = applicationContext
                    .getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
                    .getString("id_user", null)
                val idUser = (myId ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val requestBody = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("upload", currentPhotoPath, requestBody)
                viewModel.rekamWajah(idUser, filePart)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".png", storageDir).also {
            currentPhotoPath = it.absolutePath
        }
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
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

    private fun showToast(msg: String) { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
}
