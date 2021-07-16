package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.databinding.FragmentAccountBinding

class AccountFragment: Fragment() {
    private lateinit var mBinding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAccountBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}