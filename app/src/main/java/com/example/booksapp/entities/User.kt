package com.example.booksapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var uid: String = "",
                var name: String? = null,
                var email: String? = null,
                var image: String? = null) : Serializable