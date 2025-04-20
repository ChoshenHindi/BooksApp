package com.example.booksapp.ui.comments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.booksapp.utils.AlertDialogUtils
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.databinding.CommentScreenBinding
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import java.util.UUID

class CommentsFragment : Fragment() {

    lateinit var binding: CommentScreenBinding
    private lateinit var loadingDialog: ProgressDialog

    private lateinit var viewModel: CommentsViewModel
    private val adapter : CommentsAdapter = CommentsAdapter()


    companion object {
        fun newInstance(position: Int?, post: Post?, user: User?): CommentsFragment {
            val bundle: Bundle = Bundle()
            position?.let {
                bundle.putInt("position", it)
            }
            post?.let {
                bundle.putSerializable("post", it)
            }
            bundle.putSerializable("user", user)
            return CommentsFragment().apply {
                arguments = bundle
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommentScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommentsViewModel::class.java)
        (arguments?.get("post") as? Post)?.let { post ->
            (arguments?.get("user") as? User)?.let { user ->
                (arguments?.get("position") as? Int)?.let { position ->
                    viewModel.setPost(post, user, position)
                }
            }
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            AlertDialogUtils.showAlert(requireContext(), getString(R.string.error), it)
        }

        viewModel.createSuccess.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            val intent = Intent()
            intent.putExtra("post", viewModel.currentPost)
            intent.putExtra("position", viewModel.currentPosition)
            intent.putExtra("user", viewModel.currentUser)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        viewModel.comments.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }

        binding.list.adapter = adapter
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog.setMessage(getString(R.string.please_wait))
        binding.save.setOnClickListener {
            loadingDialog.show()
            viewModel.saveComment(UUID.randomUUID().toString(), binding.comment.text.toString(), System
                .currentTimeMillis())
        }
    }
}