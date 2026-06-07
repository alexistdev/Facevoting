package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.MenuAdapter
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class home_fragment : Fragment() {
    private lateinit var gridMenu: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mNamaUser: TextView
    private lateinit var mIdentitasUser: TextView
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mview = inflater.inflate(R.layout.fragment_home, container, false)
        mContext = context
        dataInit(mview)
        val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("id_user", "") ?: ""
        getIdentitas(idUser)
        setupRecyclerView()
        refresh(mContext)
        mSwipeRefreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                mSwipeRefreshLayout.isRefreshing = false
                refresh(context)
                menuAdapter.notifyDataSetChanged()
            }, 500)
        }
        return mview
    }

    private fun dataInit(mview: View) {
        mNamaUser = mview.findViewById(R.id.txtNama)
        mIdentitasUser = mview.findViewById(R.id.txtIdentitas)
        gridMenu = mview.findViewById(R.id.rcMenu)
        mSwipeRefreshLayout = mview.findViewById(R.id.refresh)
        progressDialog = ProgressDialog(context).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        refresh(mContext)
        menuAdapter.notifyDataSetChanged()
    }

    fun getIdentitas(idUser: String) {
        try {
            APIService.create(mContext).cekStatus(idUser)
                .enqueue(object : Callback<LoginModel> {
                    override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            response.body()?.let {
                                mNamaUser.text = it.nama
                                mIdentitasUser.text = "Identitas : ${it.identitas}"
                            }
                        }
                    }
                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        hideLoading()
                        if (t is NoConnectivityException) displayExceptionMessage("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            displayExceptionMessage(e.message ?: "")
        }
    }

    fun refresh(mContext: Context?) {
        try {
            tampilLoading()
            val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
                .getString("id_user", "") ?: ""
            APIService.create(mContext).listMenu(idUser)
                .enqueue(object : Callback<GetMenu> {
                    override fun onResponse(call: Call<GetMenu>, response: Response<GetMenu>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            try {
                                response.body()?.let { body ->
                                    if (body.status != "failed") {
                                        progressDialog.dismiss()
                                        menuAdapter.replaceData(body.listMenu)
                                    } else {
                                        progressDialog.dismiss()
                                    }
                                }
                            } catch (e: Exception) {
                                progressDialog.dismiss()
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onFailure(call: Call<GetMenu>, t: Throwable) {
                        hideLoading()
                        if (t is NoConnectivityException) displayExceptionMessage("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            hideLoading()
            e.printStackTrace()
            displayExceptionMessage(e.message ?: "")
        }
    }

    private fun setupRecyclerView() {
        context?.let {
            menuAdapter = MenuAdapter(mContext!!, mutableListOf())
            gridMenu.hasFixedSize()
            gridMenu.layoutManager = GridLayoutManager(context, 2)
            gridMenu.adapter = menuAdapter
        }
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    private fun displayExceptionMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
