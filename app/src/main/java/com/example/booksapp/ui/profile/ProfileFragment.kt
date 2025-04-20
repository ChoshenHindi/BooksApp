package com.example.booksapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.booksapp.utils.AlertDialogUtils
import com.example.booksapp.R
import com.example.booksapp.utils.convertToBitmap
import com.example.booksapp.databinding.FragmentProfileBinding
import com.example.booksapp.ui.Login.LoginActivity
import com.example.booksapp.ui.posts.PostsScreenType

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater)
        binding.edit.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionOpenProfileActiviy(viewModel.userLD.value)
            navController.navigate(action)
        }
        binding.logOut.setOnClickListener {
            AlertDialogUtils.showAlertWithButtons(requireContext(), getString(R.string.alarm), getString(R.string.are_you_sure), { dialog, p1 ->
                signOut()
            }) { dialog, p1 ->
                dialog.dismiss()
            }
        }

        binding.myPosts.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionPostsActiviy(PostsScreenType.MY_POSTS)
            navController.navigate(action)
        }
        binding.likedPosts.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionPostsActiviy(PostsScreenType.LIKED)
            navController.navigate(action)
        }
        binding.saveBooks.setOnClickListener {
            val navController = findNavController()
            val action = ProfileFragmentDirections.actionPostsActiviy(PostsScreenType.SAVED)
            navController.navigate(action)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLD.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.name.text = user.name
                binding.email.text = user.email
                Glide.with(this)
                    .asBitmap()
                    .load(user.image.convertToBitmap())  // Replace with your actual bitmap
                    .transform(CircleCrop()) // Makes the bitmap circular
                    .into( binding.imageView)
            }
        }
    }

    private fun signOut() {
        viewModel.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }


}