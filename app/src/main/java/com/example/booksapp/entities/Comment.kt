package com.example.booksapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.booksapp.database.UserConverter
import java.io.Serializable

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey var uid: String = "",
    var comment: String? = null,
    @TypeConverters(UserConverter::class) var createdUser: User? = null,
    var created: Long? = null,
) : Serializable