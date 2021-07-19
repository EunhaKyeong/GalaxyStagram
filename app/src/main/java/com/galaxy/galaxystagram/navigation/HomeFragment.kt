package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.ActivityMainBinding
import com.galaxy.galaxystagram.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}