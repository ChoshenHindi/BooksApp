package com.example.booksapp.ui.create_profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.booksapp.R
import com.example.booksapp.databinding.ActivityCreateProfileActiviyBinding
import com.example.booksapp.entities.User

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileActiviyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val user = intent.getSerializableExtra("user") as? User
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_profile) as NavHostFragment
        val navController = navHostFragment.navController
        navController
            .setGraph(R.navigation.profile_graph, Bundle().apply {
                putSerializable("user", user)
            })
    }
}