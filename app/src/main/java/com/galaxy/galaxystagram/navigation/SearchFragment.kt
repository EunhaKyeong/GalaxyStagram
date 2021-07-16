package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.FragmentSearchBinding

class SearchFragment: Fragment() {
    private lateinit var mBinding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}