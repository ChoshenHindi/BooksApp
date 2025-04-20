package com.example.booksapp.ui.create_profile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booksapp.database.DataBaseManager
import com.example.booksapp.utils.convertToString
import com.example.booksapp.entities.User
import com.google.firebase.auth.FirebaseAuth

class CreateProfileViewModel : ViewModel() {

    val createSuccess : MutableLiveData<Unit> = MutableLiveData()
    val onError : MutableLiveData<String> = MutableLiveData()
    val currentUserLD: MutableLiveData<User> = MutableLiveData()
    private var currentUser : User?= null


    fun createOrUpdateProfile(email: String, password: String?, fullName: String, imageBitmap: Bitmap?) {
        if (!password.isNullOrEmpty()) {
            createUserViaAuth(email, password){
                saveUserOnDataBase(FirebaseAuth.getInstance().uid ?: "", email, fullName, imageBitmap)
            }
        } else {
            saveUserOnDataBase(FirebaseAuth.getInstance().uid ?: "", email, fullName, imageBitmap)
        }

    }

    private fun saveUserOnDataBase(uid: String, email: String?, fullName: String, imageBitmap: Bitmap?) {
        imageBitmap?.let {
            saveUser(uid, fullName, imageBitmap.convertToString())
        } ?: run {
            saveUser(uid, fullName, currentUserLD.value?.image)
        }
    }

    private fun createUserViaAuth(email: String, password: String, onDone: () -> Any) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onDone.invoke()
            } else {
                showError(it.exception?.message.toString())
            }
        }

    }

    private fun saveUser(uid: String, fullName: String, image: String?) {
        val user = User(uid, fullName, FirebaseAuth.getInstance().currentUser?.email, image)
        DataBaseManager.createUser(user) {
            if (it.isSuccessful) {
                createSuccess.postValue(Unit)
            } else {
                showError(it.exception?.message ?: "")
            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun showError(error: String) {
        onError.postValue(error)
    }

    fun setUser(user: User) {
        currentUser = user
        currentUserLD.postValue(user)
    }
}