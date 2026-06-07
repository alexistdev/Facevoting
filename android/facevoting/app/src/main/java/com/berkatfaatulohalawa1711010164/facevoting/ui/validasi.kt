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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.berkatfaatulohalawa1711010164.facevoting.BuildConfig
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper
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
import androidx.core.graphics.scale

class Validasi : AppCompatActivity() {
    private lateinit var mPhoto: ImageView
    private lateinit var progressDialog: AlertDialog
    private lateinit var mRekam: Button
    private lateinit var mCek: Button
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validasi)
        init()
        mRekam.setOnClickListener { dispatchTakePictureIntent() }
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
                tampilLoading()
                val myId = applicationContext
                    .getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                    .getString("id_user", null)
                val idUser = RequestBody.create(MediaType.parse("multipart/form-data"), myId ?: "")
                val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), byteArray)
                val filePart = MultipartBody.Part.createFormData("upload", currentPhotoPath, requestBody)

                APIService.create(applicationContext).cekWajah(idUser, filePart)
                    .enqueue(object : Callback<MessageModel> {
                        override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                            hideLoading()
                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    when (body.message) {
                                        "match" -> {
                                            simpanSuara(myId ?: "")
                                            startActivity(Intent(this@Validasi, Landing::class.java).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            })
                                            finish()
                                        }
                                        "no match" -> tampilPesan("Wajah tidak cocok, silahkan ulangi pengambilan gambar")
                                        else -> {
                                            startActivity(Intent(this@Validasi, MainActivity::class.java).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            })
                                            finish()
                                            tampilPesan("Ada kesalahan sistem, silahkan dicoba lagi nanti !")
                                        }
                                    }
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

    private fun tampilPesan(pesan: String) {
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_LONG).show()
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    private fun simpanSuara(idUser: String) {
        val extra = intent.extras ?: return
        val idPaslon = extra.getString("idPaslon", "0") ?: "0"
        val idKategori = extra.getString("idKategori", "0") ?: "0"
        try {
            APIService.create(this).simpanVote(idUser, idKategori, idPaslon)
                .enqueue(object : Callback<MessageModel> {
                    override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {}
                    override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                        tampilPesan(t.message ?: "")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }
}
