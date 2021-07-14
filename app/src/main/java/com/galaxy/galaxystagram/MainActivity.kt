package com.galaxy.galaxystagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.galaxy.galaxystagram.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var email = FirebaseAuth.getInstance().currentUser!!.email.toString()
    }
}