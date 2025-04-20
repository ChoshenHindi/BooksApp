package com.example.booksapp.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.database.DataBaseManager
import com.example.booksapp.utils.convertToString
import com.example.booksapp.database.RoomManager
import com.example.booksapp.entities.Comment
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreateOrUpdateViewModel : ViewModel() {

    val onSuccessLD: MutableLiveData<Pair<Post, User?>> = MutableLiveData()
    val onErrorLD: MutableLiveData<String> = MutableLiveData()

    fun savePost(
        uid: String?,
        currentUser: User?,
        description: String,
        imageBitmap: Bitmap?,
        postImageUrl: String?,
        likeUserIds: ArrayList<String>?,
        savedByUserIds: ArrayList<String>?,
        comments: ArrayList<Comment>?,
        rating: Float,
        bookType: String,
        bookName: String
    ) {
        imageBitmap?.let {
            savePostToDataBase(uid, imageBitmap.convertToString(), currentUser, description, likeUserIds,
                savedByUserIds, comments,
                rating,
                bookType, bookName)
        } ?: run {
            savePostToDataBase(
                uid,
                postImageUrl,
                currentUser,
                description,
                likeUserIds,
                savedByUserIds,
                comments,
                rating,
                bookType,
                bookName
            )
        }

    }

    private fun savePostToDataBase(
        uid: String?,
        postImageUrl: String?,
        user: User?,
        description: String,
        likeUserIds: ArrayList<String>?,
        savedByUserId: ArrayList<String>?,
        comments: ArrayList<Comment>?,
        rating: Float,
        bookType: String,
        bookName: String
    ) {
        val _uid = uid ?: UUID.randomUUID().toString()
        val postData =
            Post(
                _uid, description, postImageUrl, user, likeUserIds,savedByUserId ?: arrayListOf(),comments ?:
                arrayListOf(), System.currentTimeMillis(), rating, bookType, bookName
            )

        DataBaseManager.savePost(postData, user) {
            if (it.isSuccessful) {
                onSuccessLD.postValue(Pair(postData, user))
            } else {
                onErrorLD.postValue(it.exception?.message.toString())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            RoomManager.database.postDao().insertPost(postData)
            user?.let { RoomManager.database.userDao().insertUser(user) }

        }
    }
}