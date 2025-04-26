package com.example.booksapp.ui.search_books

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.booksapp.R
import com.example.booksapp.databinding.SearchBooksFragmentBinding
import com.example.booksapp.utils.AlertDialogUtils

class SearchBooksFragment : Fragment() {

    lateinit var binding: SearchBooksFragmentBinding
    private lateinit var loadingDialog: ProgressDialog

    private lateinit var viewModel: SearchBooksViewModel
    private val adapter : BookAdapter = BookAdapter(ArrayList())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchBooksFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchBooksViewModel::class.java)


        viewModel.onError.observe(viewLifecycleOwner) {
            AlertDialogUtils.showAlert(requireContext(), getString(R.string.error), it)
        }

        viewModel.books.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }

        binding.list.adapter = adapter

        binding.search.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.search.isIconified = false
    }
}