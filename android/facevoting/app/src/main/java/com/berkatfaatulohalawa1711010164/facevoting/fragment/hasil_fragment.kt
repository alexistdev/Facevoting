package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.HasilAdapter
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class hasil_fragment : Fragment() {
    private lateinit var hasilView: RecyclerView
    private lateinit var hasilAdapter: HasilAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_hasil_fragment, container, false)
        dataInit(view)
        activity?.let {
            val act = it as AppCompatActivity
            act.setSupportActionBar(toolbar)
            toolbar.title = "Hasil Pemilu"
        }
        setupRecyclerView()
        setData()
        return view
    }

    fun setData() {
        tampilLoading()
        try {
            APIService.create(context).tampilHasil()
                .enqueue(object : Callback<GetMenu> {
                    override fun onResponse(call: Call<GetMenu>, response: Response<GetMenu>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            response.body()?.listMenu?.let { hasilAdapter.replaceData(it) }
                        }
                    }
                    override fun onFailure(call: Call<GetMenu>, t: Throwable) {
                        hideLoading()
                        if (t is NoConnectivityException) tampilPesan("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            hideLoading()
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }

    private fun dataInit(mview: View) {
        toolbar = mview.findViewById(R.id.toolbarHasil)
        hasilView = mview.findViewById(R.id.rcHasil)
        progressDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
    }

    private fun setupRecyclerView() {
        val llm = object : LinearLayoutManager(context) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        hasilAdapter = HasilAdapter(requireContext(), mutableListOf())
        hasilView.layoutManager = llm
        hasilView.adapter = hasilAdapter
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    fun tampilPesan(pesan: String) {
        Toast.makeText(context, pesan, Toast.LENGTH_LONG).show()
    }
}
