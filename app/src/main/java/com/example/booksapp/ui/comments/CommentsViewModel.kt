package com.example.booksapp.ui.comments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booksapp.database.DataBaseManager
import com.example.booksapp.entities.Comment
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User

class CommentsViewModel : ViewModel() {

    val createSuccess : MutableLiveData<Unit> = MutableLiveData()
    val onError : MutableLiveData<String> = MutableLiveData()
    val comments: MutableLiveData<ArrayList<Comment>> = MutableLiveData()
    var currentPost : Post?= null
        private set
    var currentUser : User?= null
        private set
    var currentPosition: Int?= null
        private set


    fun saveComment(uid: String ,comment: String, time: Long) {
        val comment = Comment(uid = uid, comment = comment, createdUser = currentUser, created = time)
        currentPost?.comments?.also {
            it.add(comment)
            DataBaseManager.savePost(currentPost!!) {
                if (it.isSuccessful) {
                    createSuccess.postValue(Unit)
                } else {
                    showError(it.exception?.message ?: "")
                }
            }
        }

    }



    private fun showError(error: String) {
        onError.postValue(error)
    }

    fun setPost(post: Post, user: User, position: Int) {
        currentPost = post
        currentUser = user
        currentPosition = position
        comments.postValue(post.comments ?: arrayListOf())
    }
}