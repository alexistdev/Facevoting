package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel
import com.berkatfaatulohalawa1711010164.facevoting.ui.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class akun_fragment : Fragment() {
    private lateinit var pDialog: ProgressDialog
    private lateinit var mEmail: EditText
    private lateinit var mNama: EditText
    private lateinit var mNik: EditText
    private lateinit var mPassword: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var mEdit: Button
    private lateinit var mLogout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val myview = inflater.inflate(R.layout.fragment_akun, container, false)
        setData(myview)
        activity?.let {
            val act = it as AppCompatActivity
            act.setSupportActionBar(toolbar)
            toolbar.title = "Setting Akun"
        }
        val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("id_user", "") ?: ""
        loadData(idUser)
        mEdit.setOnClickListener { updateUser(idUser) }
        mLogout.setOnClickListener {
            SessionHelper.logout(context)
            val intent = Intent(activity, Login::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            activity?.finish()
        }
        return myview
    }

    private fun updateUser(idUser: String) {
        val nama = mNama.text.toString()
        val identitas = mNik.text.toString()
        val password = mPassword.text.toString()
        if (nama.isEmpty() || identitas.isEmpty()) {
            tampilPesan("Silahkan lengkapi form!")
        } else {
            showDialog()
            try {
                APIService.Factory.create(context).updateAkun(idUser, nama, identitas, password)
                    .enqueue(object : Callback<AkunModel> {
                        override fun onResponse(call: Call<AkunModel>, response: Response<AkunModel>) {
                            if (response.isSuccessful) {
                                hideDialog()
                                if (response.body() != null) {
                                    loadData(idUser)
                                    tampilPesan("Data Akun berhasil diperbaharui!")
                                }
                            } else {
                                hideDialog()
                                tampilPesan(ErrorHelper.parseError(response).message)
                            }
                        }
                        override fun onFailure(call: Call<AkunModel>, t: Throwable) {
                            hideDialog()
                            tampilPesan(t.message ?: "")
                        }
                    })
            } catch (e: Exception) {
                hideDialog()
                e.printStackTrace()
                tampilPesan(e.message ?: "")
            }
        }
    }

    fun loadData(idUser: String) {
        showDialog()
        try {
            APIService.Factory.create(context).tampilAKun(idUser)
                .enqueue(object : Callback<AkunModel> {
                    override fun onResponse(call: Call<AkunModel>, response: Response<AkunModel>) {
                        if (response.isSuccessful) {
                            hideDialog()
                            response.body()?.let {
                                mEmail.setText(it.email)
                                mNama.setText(it.nama)
                                mNik.setText(it.identitas)
                            }
                        } else {
                            hideDialog()
                            tampilPesan(ErrorHelper.parseError(response).message)
                        }
                    }
                    override fun onFailure(call: Call<AkunModel>, t: Throwable) {
                        hideDialog()
                        tampilPesan(t.message ?: "")
                    }
                })
        } catch (e: Exception) {
            hideDialog()
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }

    fun setData(view: View) {
        toolbar = view.findViewById(R.id.toolbarAkun)
        mEmail = view.findViewById<EditText>(R.id.txtEmail).also { it.isEnabled = false }
        mNama = view.findViewById(R.id.txtNama)
        mNik = view.findViewById(R.id.txtIdentitas)
        mPassword = view.findViewById(R.id.txtPassword)
        mEdit = view.findViewById(R.id.btnEdit)
        mLogout = view.findViewById(R.id.btnLogout)
        pDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
    }

    private fun showDialog() { if (!pDialog.isShowing) pDialog.show() }
    private fun hideDialog() { if (pDialog.isShowing) pDialog.dismiss() }

    fun tampilPesan(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
