package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.FragmentGalleryBinding

class GalleryFragment: Fragment() {
    private lateinit var mBinding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentGalleryBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}