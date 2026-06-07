package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.MenuAdapter
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var gridMenu: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var progressDialog: AlertDialog
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mNamaUser: TextView
    private lateinit var mIdentitasUser: TextView

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mview = inflater.inflate(R.layout.fragment_home, container, false)
        dataInit(mview)
        val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("id_user", "") ?: ""
        setupRecyclerView()
        viewModel.loadIdentitas(idUser)
        viewModel.loadMenu(idUser)

        viewModel.identitasState.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                mNamaUser.text = resource.data.nama
                mIdentitasUser.text = "Identitas : ${resource.data.identitas}"
            } else if (resource is Resource.Error) {
                showToast(resource.message)
            }
        }

        viewModel.menuState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    if (resource.data.status != "failed") {
                        menuAdapter.replaceData(resource.data.listMenu)
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                mSwipeRefreshLayout.isRefreshing = false
                val uid = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
                    .getString("id_user", "") ?: ""
                viewModel.loadMenu(uid)
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
        progressDialog = AlertDialog.Builder(requireContext())
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("id_user", "") ?: ""
        viewModel.loadMenu(idUser)
        menuAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        context?.let {
            menuAdapter = MenuAdapter(it, mutableListOf())
            gridMenu.hasFixedSize()
            gridMenu.layoutManager = GridLayoutManager(context, 2)
            gridMenu.adapter = menuAdapter
        }
    }

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
}
