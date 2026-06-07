package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.core.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.ui.Login
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.AkunViewModel
import android.content.Context

class akun_fragment : Fragment() {
    private lateinit var pDialog: AlertDialog
    private lateinit var mEmail: EditText
    private lateinit var mNama: EditText
    private lateinit var mNik: EditText
    private lateinit var mPassword: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var mEdit: Button
    private lateinit var mLogout: Button

    private val viewModel: AkunViewModel by viewModels()

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
        viewModel.loadAkun(idUser)

        viewModel.akunState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showDialog()
                is Resource.Success -> {
                    hideDialog()
                    mEmail.setText(resource.data.email)
                    mNama.setText(resource.data.nama)
                    mNik.setText(resource.data.identitas)
                }
                is Resource.Error -> {
                    hideDialog()
                    showToast(resource.message)
                }
            }
        }

        viewModel.updateState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showDialog()
                is Resource.Success -> {
                    hideDialog()
                    viewModel.loadAkun(idUser)
                    showToast("Data Akun berhasil diperbaharui!")
                }
                is Resource.Error -> {
                    hideDialog()
                    showToast(resource.message)
                }
            }
        }

        mEdit.setOnClickListener {
            val nama = mNama.text.toString()
            val identitas = mNik.text.toString()
            val password = mPassword.text.toString()
            if (nama.isEmpty() || identitas.isEmpty()) {
                showToast("Silahkan lengkapi form!")
            } else {
                viewModel.updateAkun(idUser, nama, identitas, password)
            }
        }
        mLogout.setOnClickListener {
            SessionHelper.logout(context)
            startActivity(Intent(activity, Login::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            activity?.finish()
        }
        return myview
    }

    fun setData(view: View) {
        toolbar = view.findViewById(R.id.toolbarAkun)
        mEmail = view.findViewById<EditText>(R.id.txtEmail).also { it.isEnabled = false }
        mNama = view.findViewById(R.id.txtNama)
        mNik = view.findViewById(R.id.txtIdentitas)
        mPassword = view.findViewById(R.id.txtPassword)
        mEdit = view.findViewById(R.id.btnEdit)
        mLogout = view.findViewById(R.id.btnLogout)
        pDialog = AlertDialog.Builder(requireContext())
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    private fun showDialog() { if (!pDialog.isShowing) pDialog.show() }
    private fun hideDialog() { if (pDialog.isShowing) pDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
}
