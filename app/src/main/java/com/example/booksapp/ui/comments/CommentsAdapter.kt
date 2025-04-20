package com.example.booksapp.ui.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.booksapp.utils.convertToBitmap
import com.example.booksapp.databinding.CommentItemBinding
import com.example.booksapp.entities.Comment
import com.example.booksapp.ui.comments.CommentsAdapter.CommentViewHolder
import java.text.SimpleDateFormat
import java.util.Locale

class CommentsAdapter(
    private val comments: ArrayList<Comment> = arrayListOf(),

) : RecyclerView.Adapter<CommentViewHolder>() {

    private val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(CommentItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
      holder.binding.commentText.text = comment.comment
        holder.binding.commentTime.text = formatter.format(comment.created)
        Glide.with(holder.binding.commentText)
            .asBitmap()
            .load(comment.createdUser?.image?.convertToBitmap())  // Replace with your actual bitmap
            .transform(CircleCrop()) // Makes the bitmap circular
            .into(holder.binding.commentUserImage)
        holder.binding.commentUserName.text = comment.createdUser?.name
    }

    fun setItems(comments: ArrayList<Comment>) {
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(val binding: CommentItemBinding) : ViewHolder(binding.root) {

    }
}


