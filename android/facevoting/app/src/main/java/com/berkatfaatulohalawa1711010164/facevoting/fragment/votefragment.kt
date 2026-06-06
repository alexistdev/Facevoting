package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.app.ProgressDialog
import android.content.Context
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
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.VoteAdapter
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.response.GetVote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class votefragment : Fragment() {
    private lateinit var voteView: RecyclerView
    private lateinit var voteAdapter: VoteAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_votefragment, container, false)
        dataInit(view)
        activity?.let {
            val act = it as AppCompatActivity
            act.setSupportActionBar(toolbar)
            toolbar.title = "Bukti Pemilihan"
        }
        setupRecyclerView()
        setData(context)
        return view
    }

    fun setData(mContext: Context?) {
        tampilLoading()
        try {
            val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
                .getString("id_user", "") ?: ""
            APIService.Factory.create(mContext).tampilVote(idUser)
                .enqueue(object : Callback<GetVote> {
                    override fun onResponse(call: Call<GetVote>, response: Response<GetVote>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            response.body()?.listVote?.let { voteAdapter.replaceData(it) }
                        }
                    }
                    override fun onFailure(call: Call<GetVote>, t: Throwable) {
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

    private fun setupRecyclerView() {
        val llm = object : LinearLayoutManager(context) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        voteAdapter = VoteAdapter(requireContext(), mutableListOf())
        voteView.layoutManager = llm
        voteView.adapter = voteAdapter
    }

    private fun dataInit(mview: View) {
        toolbar = mview.findViewById(R.id.toolbarVote)
        voteView = mview.findViewById(R.id.rcVote)
        progressDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    fun tampilPesan(pesan: String) {
        Toast.makeText(context, pesan, Toast.LENGTH_LONG).show()
    }
}
