package com.example.booksapp.ui.posts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.booksapp.utils.convertToBitmap
import com.example.booksapp.databinding.FragmentHomeBinding
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import com.example.booksapp.ui.comments.CommentActivity

class BookPostsFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = PostsAdapter(updatePostListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            openUpdatePostActivity(position)
        }
    }, deletePostListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            deletePost(position)
        }
    }, likePostListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            onLikeClicked(position)
        }
    }, commentsListener = object : OnItemClickListener {
        override fun onItemClicked(position: Int) {
            openUpdateCommentsPostActivity(position)
        }
    }, savePostListener = object : OnItemClickListener{
        override fun onItemClicked(position: Int) {
            onSavedClicked(position)
        }
    })

    private fun onSavedClicked(position: Int) {
        postsViewModel?.onSavedClicked(position)
    }

    var postsViewModel: PostsViewModel? = null

    private val createPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val post = result.data?.getSerializableExtra("post") as? Post
            val user = result.data?.getSerializableExtra("user") as? User
            post?.let {
                user?.let {
                    postsViewModel?.addPost(post, user)
                }
            }
        }
    }

    private val updatePostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val post = result.data?.getSerializableExtra("post") as? Post
            val position = result.data?.getIntExtra("position", -1) ?: -1
            val user = result.data?.getSerializableExtra("user") as? User
            post?.let {
                user?.let {
                    postsViewModel?.updatePost(position, post, user)
                }
            }
        }
    }

    private val updateCommentPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val post = result.data?.getSerializableExtra("post") as? Post
            val position = result.data?.getIntExtra("position", -1) ?: -1
            val user = result.data?.getSerializableExtra("user") as? User
            post?.let {
                user?.let {
                    postsViewModel?.updatePost(position, post, user)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postsViewModel = ViewModelProvider(this).get(PostsViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.swiperefresh.isRefreshing = true
        binding.swiperefresh.setOnRefreshListener {
            postsViewModel?.fetchPosts(false)
        }
        return binding.root
    }

    private fun updateTopBanner(user: User?) {
        user?.let {
            Glide.with(this)
                .asBitmap()
                .load(it.image.convertToBitmap())  // Replace with your actual bitmap
                .transform(CircleCrop()) // Makes the bitmap circular
                .into(binding.profileImage)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myType : PostsScreenType? = arguments?.getSerializable("type") as? PostsScreenType
        postsViewModel?.setPostsType(myType)
        postsViewModel?.fetchPosts()
        postsViewModel?.postsLD?.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = false
            binding.emptyState.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.list.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            adapter.setItems(it)
        }

        postsViewModel?.userLD?.observe(viewLifecycleOwner) {
            updateTopBanner(it)
        }

        binding.list.adapter = adapter

        binding.addPostContainer.setOnClickListener { openCreatePostActivity() }

        binding.addPostContainer.visibility = if (this.activity !is PostsActivity) View.VISIBLE else View.GONE

        binding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                postsViewModel?.onSearch(p0.toString())
            }
        })
    }

    private fun openCreatePostActivity() {
        val intent = Intent(requireContext(), CreateOrUpdateBookPostActivity::class.java)
        getCurrentUser()?.let {
            intent.putExtra("user", it)
        }
        createPostLauncher.launch(intent)
    }

    private fun openUpdatePostActivity(position: Int) {
        val intent = Intent(requireContext(), CreateOrUpdateBookPostActivity::class.java)
        getCurrentUser()?.let {
            intent.putExtra("user", it)
        }
        intent.putExtra("position", position)
        intent.putExtra("post", postsViewModel?.postsLD?.value?.get(position))
        updatePostLauncher.launch(intent)
    }

    private fun openUpdateCommentsPostActivity(position: Int) {
        val intent = Intent(requireContext(), CommentActivity::class.java)
        getCurrentUser()?.let {
            intent.putExtra("user", it)
        }
        intent.putExtra("position", position)
        intent.putExtra("post", postsViewModel?.postsLD?.value?.get(position))
        updateCommentPostLauncher.launch(intent)
    }

    private fun getCurrentUser(): User? = postsViewModel?.userLD?.value


    private fun deletePost(position: Int) {
        postsViewModel?.onDeleteClicked(position)
    }

    private fun onLikeClicked(position: Int) {
        postsViewModel?.onLikeClicked(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}