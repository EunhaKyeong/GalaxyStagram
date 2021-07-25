package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.MainActivity
import com.galaxy.galaxystagram.databinding.FragmentAccountBinding
import com.galaxy.galaxystagram.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class AccountFragment(uid: String): Fragment() {
    private lateinit var mBinding: FragmentAccountBinding
    private var auth: FirebaseAuth? = null
    private var store: FirebaseFirestore? = null
    private var uid: String = uid
    private var user: UserDTO? = null
    private var TAG: String = "AccountFragment: "

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAccountBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()

        //mainActivity의 toolbar 사라지도록 하는 함수 호출
        (activity as MainActivity).setVisibilityToolbar()
        //해당 계정의 이메일이 보이도록 텍스트뷰에 유저 이메일 텍스트 설정.

//        mBinding.usernameTextView.text = user!!.email

        if (uid==auth!!.currentUser!!.uid) {    //자신의 계정인 경우
            mBinding.backImageView.visibility = View.GONE
            mBinding.followingButton.visibility = View.GONE
            mBinding.messageButton.visibility = View.GONE
            mBinding.editProfileButton.visibility = View.VISIBLE
        } else {    //다른 사람의 계정일 경우
            mBinding.backImageView.visibility = View.VISIBLE
            mBinding.followingButton.visibility = View.VISIBLE
            mBinding.messageButton.visibility = View.VISIBLE
            mBinding.editProfileButton.visibility = View.GONE
        }

        return mBinding.root
    }

    //AccountFragment 화면이 가려졌을 때
    override fun onPause() {
        //toolbar가 다시 보이도록 하는 함수 호출.
        (activity as MainActivity).setVisibilityToolbar()
        super.onPause()
    }
}