package com.example.booksapp.ui.search_books

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.api.OpenLibraryApi
import com.example.booksapp.database.DataBaseManager
import com.example.booksapp.entities.Book
import com.example.booksapp.entities.Comment
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchBooksViewModel : ViewModel() {

    val createSuccess : MutableLiveData<Unit> = MutableLiveData()
    val onError : MutableLiveData<String> = MutableLiveData()
    val books: MutableLiveData<ArrayList<Book>> = MutableLiveData()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(OpenLibraryApi::class.java)


    fun search(query:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.searchBooks(query)
                val books = response.docs
                withContext(Dispatchers.Main){
                    this@SearchBooksViewModel.books.value = books
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}