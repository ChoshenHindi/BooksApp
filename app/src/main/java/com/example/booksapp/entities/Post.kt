package com.example.booksapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.booksapp.database.CommentConverter

import com.example.booksapp.database.StringsConverter
import com.example.booksapp.database.UserConverter
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey var uid: String = "",
    var description: String? = null,
    var image: String? = null,
    @TypeConverters(UserConverter::class) var createdUser: User? = null,
    @TypeConverters(StringsConverter::class)  var likeUserIds: ArrayList<String>? = arrayListOf(),
    @TypeConverters(StringsConverter::class)  var savedByUserIds: ArrayList<String>? = arrayListOf(),
    @TypeConverters(CommentConverter::class) var comments: ArrayList<Comment>? = arrayListOf(),
    var created: Long? = null,
    var rating: Float = 0f,
    var bookType: String = "",
    var bookName: String = ""
) : Serializable
