package com.berkatfaatulohalawa1711010164.facevoting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.VoteAdapter
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.VoteViewModel

class VoteFragment : Fragment() {
    private lateinit var voteView: RecyclerView
    private lateinit var voteAdapter: VoteAdapter
    private lateinit var progressDialog: AlertDialog
    private lateinit var toolbar: Toolbar

    private lateinit var viewModel: VoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[VoteViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_votefragment, container, false)
        dataInit(view)
        activity?.let {
            val act = it as AppCompatActivity
            act.setSupportActionBar(toolbar)
            toolbar.title = "Bukti Pemilihan"
        }
        setupRecyclerView()
        val idUser = requireActivity().getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("id_user", "") ?: ""
        viewModel.loadVotes(idUser)

        viewModel.voteState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    resource.data.listVote?.let { voteAdapter.replaceData(it) }
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
        return view
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
        progressDialog = AlertDialog.Builder(requireContext())
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
}
