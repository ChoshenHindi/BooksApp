package com.example.booksapp.ui.posts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp.R
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User
import com.example.booksapp.ui.main.CreateOrUpdatePostFragment

class CreateOrUpdateBookPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
        val position: Int? = intent.getIntExtra("position", -1)
        val post : Post? = intent.getSerializableExtra("post") as? Post
        val user : User? = intent.getSerializableExtra("user") as? User

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CreateOrUpdatePostFragment.newInstance(if (position == -1) null else position, post,user))
                .commitNow()
        }
    }
}