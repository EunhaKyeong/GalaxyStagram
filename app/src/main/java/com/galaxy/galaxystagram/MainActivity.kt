package com.galaxy.galaxystagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.ActivityMainBinding
import com.galaxy.galaxystagram.navigation.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()

        //앱에서 앨범에 접근을 허용할지 선택하는 메시지, 한 번 허용하면 앱이 설치돼 있는 동안 다시 뜨지 않음.
        ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
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

