package com.galaxy.galaxystagram.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.galaxy.galaxystagram.MainActivity
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.FragmentAccountBinding
import com.galaxy.galaxystagram.model.ContentDTO
import com.galaxy.galaxystagram.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AccountFragment(email: String): Fragment() {
    private lateinit var mBinding: FragmentAccountBinding
    private var auth: FirebaseAuth? = null
    private var store: FirebaseFirestore? = null
    private var currentUser: UserDTO? = null    //현재 사용자
    private var fragmentUser: UserDTO? = null   //현재 화면에 보이고 있는 계정의 userDTO
    private var email: String = email   //계정 화면에 보이는 이메일
    private var postList = arrayListOf<ContentDTO>()
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
        mBinding.usernameTextView.text = email

        if (email==auth!!.currentUser!!.email) { //자신의 계정인 경우 -> 프로필 편집 버튼만
            mBinding.backImageView.visibility = View.GONE
            mBinding.followingButton.visibility = View.GONE
            mBinding.messageButton.visibility = View.GONE
            mBinding.editProfileButton.visibility = View.VISIBLE
        } else {    //다른 사람의 계정인 경우 -> 팔로우/팔로우 취소 버튼, 메세지 버튼
            mBinding.followingButton.visibility = View.VISIBLE
            mBinding.messageButton.visibility = View.VISIBLE
            mBinding.editProfileButton.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.Main).launch {
            currentUser = store!!.collection("users").document(auth!!.uid!!)
                .get().await().toObject(UserDTO::class.java)

            //현재 사용자의 userDTO 바인딩
            if (email==currentUser!!.email) { //자신의 계정인 경우
                setUserInfo(currentUser!!)
            } else {    //다른 사람의 계정인 경우
                fragmentUser = store!!.collection("users").whereEqualTo("email", email)
                    .get().await().documents[0].toObject(UserDTO::class.java)
                setUserInfo(fragmentUser!!)
            }
        }

        //post 테이블 데이터 조회하고 recyclerview에 바인딩 시작.
        store!!.collection("posts")
            .whereEqualTo("userEmail", email)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { posts, error ->
                if (error!=null)
                    Log.e(TAG, error.toString())
                else {
                    postList.clear()
                    for (post in posts!!.documents) {
                        postList!!.add(post.toObject(ContentDTO::class.java)!!)
                    }

                    setPostsInfo()
                }
            }

        //팔로우 버튼을 누른다
        mBinding.followingButton.setOnClickListener {
            var userCollection = store!!.collection("users")
            var clickingDoc = userCollection.document(currentUser!!.uid!!) //팔로우 버튼을 누른 사람 -> 현재 사용자
            var clickedDoc = userCollection.document(fragmentUser!!.uid!!)  //팔로우 버튼이 눌린 계정 -> 현재 화면에 보이는 사용자

            if (mBinding.followingButton.text.equals(getString(R.string.follow))) {  //팔로우 버튼일 경우
                //누른 사람 : 팔로잉 추가
                currentUser!!.followings[fragmentUser!!.email!!] = true
                //누름을 당한 사람 : 팔로워 추가
                Log.d(TAG, fragmentUser!!.followers.toString())
                fragmentUser!!.followers[currentUser!!.email!!] = true

                store!!.runTransaction {
                    it.update(clickingDoc, "followings", currentUser!!.followings)
                    it.update(clickedDoc, "followers", fragmentUser!!.followers)
                }.addOnSuccessListener {
                    mBinding.followingButton.text = getString(R.string.follow_cancel)
                }.addOnFailureListener {
                    Log.e(TAG, it.toString())
                }
            } else {    //팔로우 취소 버튼일 경우
                //누른 사람 : 팔로잉 취소
                currentUser!!.followings.remove(fragmentUser!!.email)
                //누름을 당한 사람 : 팔로워 추가
                fragmentUser!!.followers.remove(currentUser!!.email)

                store!!.runTransaction {
                    it.update(clickingDoc, "followings", currentUser!!.followings)
                    it.update(clickedDoc, "followers", fragmentUser!!.followers)
                }.addOnSuccessListener {
                    mBinding.followingButton.text = getString(R.string.follow)
                }.addOnFailureListener {
                    Log.e(TAG, it.toString())
                }
            }

            setUserInfo(fragmentUser!!)
        }

        return mBinding.root
    }

    inner class PostGridAdapter: RecyclerView.Adapter<PostGridHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostGridHolder {
            var postThumbnailImageView: ImageView = ImageView(parent.context)
            postThumbnailImageView.scaleType = ImageView.ScaleType.FIT_XY
            postThumbnailImageView.layoutParams = ViewGroup.LayoutParams(parent.width/3, parent.width/3)

            return PostGridHolder(postThumbnailImageView)
        }

        override fun onBindViewHolder(holder: PostGridHolder, position: Int) {
            holder.setData(postList[position].imageUrl!!)
        }

        override fun getItemCount(): Int {
            return postList.size
        }

    }

    inner class PostGridHolder(postThumbnailImageView: ImageView) : RecyclerView.ViewHolder(postThumbnailImageView) {
        private var postThumbnailImageView = postThumbnailImageView

        fun setData(imageUrl: String) {
            Glide.with(postThumbnailImageView).load(imageUrl).into(postThumbnailImageView)
        }
    }

    //게시글 관련 화면 설정.
    private fun setPostsInfo() {
        mBinding.postTextView.text = postList.size.toString()

        if (postList.size!=0) {
            //recycler view 화면 설정
            mBinding.postGridRecyclerView.visibility = View.VISIBLE
            mBinding.noPostImageView.visibility = View.GONE
            mBinding.postGridRecyclerView.adapter = PostGridAdapter()
            mBinding.postGridRecyclerView.layoutManager = GridLayoutManager(context, 3)
        } else {    //게시물이 없으면 recyclerview 숨기고 게시물 없음 이미지뷰 보이게
            mBinding.postGridRecyclerView.visibility = View.GONE
            mBinding.noPostImageView.visibility = View.VISIBLE
        }

    }

    //프로필(프로필 이미지, 팔로워 수, 팔로잉 수) 관련 화면 설정.
    private fun setUserInfo(fragmentUser: UserDTO) {
        //프로필 이미지
        if (fragmentUser!!.profileImgUrl!=null)
            Glide.with(requireContext()).load(fragmentUser!!.profileImgUrl).circleCrop().into(mBinding.userProfileImageView)
        //팔로우 수
        mBinding.followerTextView.text = fragmentUser.followers.size.toString()
        //팔로잉 수
        mBinding.followingTextView.text = fragmentUser.followings.size.toString()

        //버튼 설정
        if (fragmentUser.email!=auth!!.currentUser!!.email) {
            if (fragmentUser!!.followers.containsKey(currentUser!!.email)) {
                //이미 팔로우한 계정이면 팔로우 취소
                mBinding.followingButton.text = getString(R.string.follow_cancel)
            } else {
                //팔로우를 하지 않은 계정이면 팔로우
                mBinding.followingButton.text = getString(R.string.follow)
            }
        }
    }

    //AccountFragment 화면이 가려졌을 때
    override fun onPause() {
        //toolbar가 다시 보이도록 하는 함수 호출.
        (activity as MainActivity).setVisibilityToolbar()
        super.onPause()
    }
}