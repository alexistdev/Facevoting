package com.berkatfaatulohalawa1711010164.facevoting.ui

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
import androidx.core.graphics.scale
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.FaceViewModel
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.VoteViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Validasi : AppCompatActivity() {
    private lateinit var mPhoto: ImageView
    private lateinit var progressDialog: AlertDialog
    private lateinit var mRekam: Button
    private lateinit var mCek: Button
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var currentPhotoPath: String = ""

    private val faceViewModel: FaceViewModel by viewModels()
    private val voteViewModel: VoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validasi)
        init()
        mRekam.setOnClickListener { dispatchTakePictureIntent() }

        faceViewModel.cekState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    when (resource.data.message) {
                        "match" -> {
                            val myId = applicationContext
                                .getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                                .getString("id_user", "") ?: ""
                            val idPaslon = intent.extras?.getString("idPaslon", "0") ?: "0"
                            val idKategori = intent.extras?.getString("idKategori", "0") ?: "0"
                            voteViewModel.simpanVote(myId, idKategori, idPaslon)
                            startActivity(Intent(this, Landing::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            finish()
                        }
                        "no match" -> showToast("Wajah tidak cocok, silahkan ulangi pengambilan gambar")
                        else -> {
                            startActivity(Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            finish()
                            showToast("Ada kesalahan sistem, silahkan dicoba lagi nanti !")
                        }
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File = try { createImageFile() } catch (_: IOException) { return }
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
            val convertedImage = getResizedBitmap(bitmap)
            val stream = ByteArrayOutputStream()
            convertedImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            mPhoto.setImageBitmap(convertedImage)
            mCek.visibility = View.VISIBLE

            mCek.setOnClickListener {
                val myId = applicationContext
                    .getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                    .getString("id_user", null)
                val idUser = (myId ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val requestBody = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("upload", currentPhotoPath, requestBody)
                faceViewModel.cekWajah(idUser, filePart)
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

    private fun getResizedBitmap(image: Bitmap): Bitmap {
        val maxSize = 1024
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
        return image.scale(width, height)
    }

    private fun showToast(msg: String) { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
}
