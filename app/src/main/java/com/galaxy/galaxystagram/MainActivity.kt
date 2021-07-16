package com.galaxy.galaxystagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.ActivityMainBinding
import com.galaxy.galaxystagram.navigation.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
    }

    fun initNavigationBar() {
        binding.myNavigation.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.homeItem -> {
                        changeFragment(HomeFragment())
                    }
                    R.id.searchItem -> {
                        changeFragment(SearchFragment())
                    }
                    R.id.photoItem -> {
                        changeFragment(GalleryFragment())
                    }
                    R.id.favoriteItem -> {
                        changeFragment(FavoriteFragment())
                    }
                    R.id.accountItem -> {
                        changeFragment(AccountFragment())
                    }
                }
                true
            }
            selectedItemId = R.id.homeItem
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContent.id, fragment).commit()
    }
}

