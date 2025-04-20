package com.example.booksapp.ui.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.booksapp.R
import com.example.booksapp.utils.convertToBitmap
import com.example.booksapp.databinding.PostItemLayoutBinding
import com.example.booksapp.entities.Post
import com.example.booksapp.ui.posts.PostsAdapter.PostViewHolder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class PostsAdapter(
    private val posts: ArrayList<Post> = arrayListOf(),
    private val updatePostListener: OnItemClickListener,
    private val deletePostListener: OnItemClickListener,
    private val likePostListener: OnItemClickListener,
    private val savePostListener: OnItemClickListener,
    private val commentsListener: OnItemClickListener

) : RecyclerView.Adapter<PostViewHolder>() {

    private val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[holder.adapterPosition]
        holder.binding.description.text = post.description
        holder.binding.userName.text = post.createdUser?.name
        holder.binding.likesCount.text = "${post.likeUserIds?.size ?: 0}"
        holder.binding.time.text = formatter.format(post.created)
        Glide.with(holder.binding.userImage)
            .asBitmap()
            .load(post.createdUser?.image?.convertToBitmap())  // Replace with your actual bitmap
            .transform(CircleCrop()) // Makes the bitmap circular
            .into(holder.binding.userImage)
        holder.binding.delete.setOnClickListener { deletePostListener.onItemClicked(holder.adapterPosition) }
        holder.binding.edit.setOnClickListener { updatePostListener.onItemClicked(holder.adapterPosition) }
        holder.binding.actionContainer.visibility = if (post.createdUser?.uid == FirebaseAuth.getInstance().uid) View.VISIBLE else View.GONE
        holder.binding.rating.text = post.rating.toString()
        holder.binding.postImage.setImageBitmap(post.image.convertToBitmap())
        holder.binding.postImage.visibility = if (post.image != null) View.VISIBLE else View.GONE
        post.likeUserIds?.firstOrNull { it == FirebaseAuth.getInstance().uid }?.let {
           holder.binding.likeImage.rotation = 180f
        } ?: run {
            holder.binding.likeImage.rotation = 0f
        }
        holder.binding.likeImage.setOnClickListener { likePostListener.onItemClicked(holder.adapterPosition) }
        holder.binding.bookName.text = "Book Name: ${post.bookName}"
        holder.binding.bookType.text = "Book Type: ${post.bookType}"
        holder.binding.commentCount.text = (post.comments?.size ?: 0).toString()
        holder.binding.commentsContainer.setOnClickListener { commentsListener.onItemClicked(position) }
        holder.binding.email.text = post.createdUser?.email
        holder.binding.save.apply {
            setBackgroundResource(
                if (post.savedByUserIds?.contains(FirebaseAuth.getInstance().uid ?: "") == true)
                    R.drawable.baseline_bookmark_fill else R.drawable.baseline_bookmark_border_24
            )
            setOnClickListener { savePostListener.onItemClicked(position) }
        }
    }

    fun setItems(posts: ArrayList<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(val binding: PostItemLayoutBinding) : ViewHolder(binding.root) {

    }
}


