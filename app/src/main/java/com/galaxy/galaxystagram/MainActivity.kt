package com.galaxy.galaxystagram

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.ActivityMainBinding
import com.galaxy.galaxystagram.navigation.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //갤러리 앱으로 이동하는 launcher 등록
    private var launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it-> changeFragment(GalleryFragment(it))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar() //네이게이션 바의 각 메뉴 탭을 눌렀을 때 화면이 전환되도록 하는 함수.

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
                        //앱이 갤러리에 접근햐는 것을 허용했을 경우
                        if (ContextCompat.checkSelfPermission(this@MainActivity.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                            launcher.launch("image/*")
                        } else {    //앱이 갤러리에 접근햐는 것을 허용하지 않았을 경우
                            Toast.makeText(this@MainActivity,
                                "갤러리 접근 권한이 거부돼 있습니다. 설정에서 접근을 허용해 주세요.",
                                Toast.LENGTH_SHORT).show()
                        }
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

