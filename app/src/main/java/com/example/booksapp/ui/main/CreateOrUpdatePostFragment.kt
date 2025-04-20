package com.example.booksapp.ui.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.booksapp.R
import com.example.booksapp.databinding.CreateOrUpdatePostLayoutBinding
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import com.example.booksapp.utils.AlertDialogUtils
import com.example.booksapp.utils.convertToBitmap
import com.google.firebase.auth.FirebaseAuth

class CreateOrUpdatePostFragment : Fragment() {

    private var loadingDialog: ProgressDialog? = null
    private var currentUser: User? = null
    private var imageBitmap: Bitmap? = null

    private val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = (result.data!!.extras!!["data"] as Bitmap?)!!
            imageBitmap = photo
            binding.postDataContainer.postImage.setImageBitmap(photo)
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val uri = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
                imageBitmap = bitmap
                binding.postDataContainer.postImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
            }
        }
    }

    companion object {
        fun newInstance(position: Int?, post: Post?, user: User?): CreateOrUpdatePostFragment {
            val bundle: Bundle = Bundle()
            position?.let {
                bundle.putInt("position", it)
            }
            post?.let {
                bundle.putSerializable("post", it)
            }
            bundle.putSerializable("user", user)
            return CreateOrUpdatePostFragment().apply {
                arguments = bundle
            }
        }
    }

    private var _binding: CreateOrUpdatePostLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateOrUpdateViewModel
    private var position: Int? = null
    private var post: Post? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateOrUpdateViewModel::class.java)
        position = arguments?.getInt("position", -1)
        position = if (position == -1) null else position
        post = arguments?.getSerializable("post") as? Post
        currentUser = arguments?.getSerializable("user") as? User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateOrUpdatePostLayoutBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog?.setMessage(getString(R.string.please_wait))
        viewModel.onSuccessLD.observe(viewLifecycleOwner) {
            onSuccess(it.first, it.second)
        }
        viewModel.onErrorLD.observe(viewLifecycleOwner) {
            loadingDialog?.dismiss()
            AlertDialogUtils.showAlert(requireContext(), getString(R.string.error), it)
        }

        Glide.with(this)
            .asBitmap()
            .load(currentUser?.image?.convertToBitmap())  // Replace with your actual bitmap
            .transform(CircleCrop()) // Makes the bitmap circular
            .into(binding.postDataContainer.profileImage)

        binding.header.setText(if (post == null) R.string.create_book_review else R.string.update_post)
        binding.postDataContainer.name.text = currentUser?.name
        binding.postDataContainer.camera.setOnClickListener { openCamera() }
        binding.postDataContainer.gallery.setOnClickListener { openGallery() }
        binding.postDataContainer.postImage.setImageBitmap(post?.image?.convertToBitmap())
        post?.let {
            binding.postDataContainer.description.setText(it.description)
        }

        binding.save.setText(if (post == null) R.string.create else R.string.update)
        binding.save.setOnClickListener { savePost() }
        binding.close.setOnClickListener { activity?.finish() }
        binding.postDataContainer.email.text = post?.createdUser?.email ?: FirebaseAuth.getInstance().currentUser?.email
        binding.postDataContainer.bookTypeSpinner.adapter = ArrayAdapter.createFromResource(
            this.requireContext(),  // or requireContext() if in Fragment
            R.array.book_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.postDataContainer.ratingBar.rating = post?.rating ?: 0f
        binding.postDataContainer.bookTypeSpinner.setSelection(resources.getStringArray(R.array.book_types).indexOf(post?.bookType))
        binding.postDataContainer.bookName.setText(post?.bookName)
    }

    private fun onSuccess(post: Post?, user: User?) {
        loadingDialog?.dismiss()
        val intent = Intent()
        intent.putExtra("post", post)
        intent.putExtra("position", position)
        user?.let {
            intent.putExtra("user", it)
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun savePost() {
        if (binding.postDataContainer.description.text.toString().isNullOrEmpty()) {
            binding.postDataContainer.description.error = getString(R.string.please_enter_description)
        } else {
            loadingDialog?.show()
            viewModel.savePost(post?.uid,
                currentUser, binding.postDataContainer.description.text.toString(),
                imageBitmap, post?.image, post?.likeUserIds, post?.savedByUserIds, post?.comments,
                binding.postDataContainer.ratingBar.rating,
                binding.postDataContainer.bookTypeSpinner.selectedItem as String, binding.postDataContainer
                    .bookName.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(takePictureIntent)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        galleryActivityResultLauncher.launch(intent)
    }
}