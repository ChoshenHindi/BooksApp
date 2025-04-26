package com.example.booksapp.ui.search_books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booksapp.R
import com.example.booksapp.entities.Book

class BookAdapter(private val books: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = view.findViewById(R.id.coverImage)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val yearText: TextView = view.findViewById(R.id.yearText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    fun setItems (books: List<Book>) {
        this.books.clear()
        this.books.addAll(books)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.titleText.text = book.title ?: "Unknown Title"
        holder.authorText.text = book.author_name?.joinToString() ?: "Unknown Author"
        holder.yearText.text = book.first_publish_year?.toString() ?: "N/A"

        val coverUrl = book.cover_i?.let {
            "https://covers.openlibrary.org/b/id/${it}-L.jpg"
        }

        Glide.with(holder.itemView.context)
            .load(coverUrl)
            .placeholder(R.drawable.book_placeholder) // your placeholder drawable
            .into(holder.coverImage)
    }
}
