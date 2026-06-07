package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.HasilAdapter
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.HasilViewModel

class hasil_fragment : Fragment() {
    private lateinit var hasilView: RecyclerView
    private lateinit var hasilAdapter: HasilAdapter
    private lateinit var progressDialog: AlertDialog
    private lateinit var toolbar: Toolbar

    private val viewModel: HasilViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_hasil_fragment, container, false)
        dataInit(view)
        activity?.let {
            val act = it as AppCompatActivity
            act.setSupportActionBar(toolbar)
            toolbar.title = "Hasil Pemilu"
        }
        setupRecyclerView()
        viewModel.loadHasil()

        viewModel.hasilState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    resource.data.listMenu?.let { hasilAdapter.replaceData(it) }
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
        return view
    }

    private fun dataInit(mview: View) {
        toolbar = mview.findViewById(R.id.toolbarHasil)
        hasilView = mview.findViewById(R.id.rcHasil)
        progressDialog = AlertDialog.Builder(requireContext())
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
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

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
}
