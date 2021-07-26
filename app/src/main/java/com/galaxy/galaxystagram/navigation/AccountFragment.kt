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
import com.galaxy.galaxystagram.databinding.FragmentAccountBinding
import com.galaxy.galaxystagram.model.ContentDTO
import com.galaxy.galaxystagram.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AccountFragment(email: String): Fragment() {
    private lateinit var mBinding: FragmentAccountBinding
    private var auth: FirebaseAuth? = null
    private var store: FirebaseFirestore? = null
    private var email: String = email
    private var userDTO: UserDTO? = null
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

        //users, posts DB 데이터를 조회하는 트랜잭션 실행.
        var postCollection = store!!.collection("posts")
        var userCollection = store!!.collection("users")
        store!!.runTransaction { transaction ->
            postCollection
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

            userCollection.whereEqualTo("email", email).addSnapshotListener { value, error ->
                if (error!=null)
                    Log.e(TAG, error.toString())
                else {
                    userDTO = UserDTO()
                    userDTO = value!!.documents[0].toObject(UserDTO::class.java)
                }
                //프로필 사진, 게시글, 팔로워, 팔로잉 화면 설정 함수 호출.
                setUserInfo()
            }
        }

        //mainActivity의 toolbar 사라지도록 하는 함수 호출
        (activity as MainActivity).setVisibilityToolbar()

        //해당 계정의 이메일이 보이도록 텍스트뷰에 유저 이메일 텍스트 설정.
        mBinding.usernameTextView.text = email

        //버튼 설정
        if (email==auth!!.currentUser!!.email) {    //자신의 계정인 경우
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
    private fun setUserInfo() {
        //프로필 이미지
        if (userDTO!!.profileImgUrl!=null)
            Glide.with(requireContext()).load(userDTO!!.profileImgUrl).circleCrop().into(mBinding.userProfileImageView)

        //팔로우 수
        mBinding.followerTextView.text = userDTO!!.follower.size.toString()

        //팔로잉 수
        mBinding.followingTextView.text = userDTO!!.following.size.toString()
    }

    //AccountFragment 화면이 가려졌을 때
    override fun onPause() {
        //toolbar가 다시 보이도록 하는 함수 호출.
        (activity as MainActivity).setVisibilityToolbar()
        super.onPause()
    }
}


