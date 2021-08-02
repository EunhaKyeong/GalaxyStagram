package com.galaxy.galaxystagram.navigation

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.galaxy.galaxystagram.MainActivity
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.FragmentHomeBinding
import com.galaxy.galaxystagram.databinding.PostDetailBinding
import com.galaxy.galaxystagram.model.ContentDTO
import com.galaxy.galaxystagram.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment: Fragment() {
    private lateinit var mBinding: FragmentHomeBinding
    private var TAG: String = "HomeFragment: "
    private var store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var adapter: PostAdapter? = null
    private var contentsList = arrayListOf<ContentDTO>()
    private var usersHashMap = HashMap<String, UserDTO>()
    private var contentsId = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        var followingSet: ArrayList<String>? = null

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                var userDTO = store.collection("users").document(auth.uid!!).get()
                    .await().toObject(UserDTO::class.java)

                followingSet = ArrayList(userDTO!!.followings.keys)
                followingSet?.add(auth.currentUser!!.email!!)

                store.collection("users")
                    .whereIn("email", followingSet!!)
                    .addSnapshotListener { users, error ->
                        if (error != null)
                            Log.e(TAG, error.toString())
                        else {
                            usersHashMap.clear()
                            for (user in users!!.documents)
                                usersHashMap[user.id] = user.toObject(UserDTO::class.java)!!
                        }
                    }
            }

            adapter = PostAdapter(followingSet)
            mBinding.postsRecyclerView.adapter = adapter
        }

        //layoutManager : 아이템의 배치를 담당.
        //LinearLayoutManager : 가로/세로
        //GirdLayoutManager : 그리드 형식
        //StaggeredGirdLayoutManager : 지그재그형의 그리드 형식
        mBinding.postsRecyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.postsRecyclerView.addItemDecoration(VerticalItemDecorator(30))

        return mBinding.root
    }

    //Adapter : 데이터와 아이템에 관한 View를 생성.
    inner class PostAdapter(followingList: ArrayList<String>?): RecyclerView.Adapter<PostHolder>() {
        private lateinit var postDetailBinding: PostDetailBinding

        //DB에서 게시글 데이터 가져오기
        init {
            store.collection("posts")
                .whereIn("userEmail", followingList!!)
                .addSnapshotListener { posts, e ->
                    if (e != null) {
                        Log.e(TAG, e.toString())
                        Toast.makeText(activity, R.string.upload_fail, Toast.LENGTH_SHORT).show()
                    } else {
                        contentsList.clear()
                        contentsId.clear()
                        for (post in posts!!.documents) {
                            var content = post.toObject(ContentDTO::class.java)
                            contentsList.add(content!!)
                            contentsId.add(post.id)
                        }
                    }
                    notifyDataSetChanged()
                }
        }

        //post_detail.xml을 아이템뷰 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
            var inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            postDetailBinding = PostDetailBinding.inflate(inflater, parent, false)

            return PostHolder(postDetailBinding)
        }

        //아이템뷰 화면에 입력돼야 할 데이터 추가.
        override fun onBindViewHolder(holder: PostHolder, position: Int) {
            holder.setData(contentsList[position], position)
        }

        //총 몇 개의 아이템이 추가되어야 하는지 확인.
        override fun getItemCount(): Int {
            return contentsList.size
        }
    }

    //뷰를 보관하는 Holder 객체.
    //item 뷰들을 재활용하기 위해 각 요소를 저장해두고 사용.
    //아이템 생성시 뷰 바인딩은 한 번만 하고, 바인딩 된 객체를 가져다 사용해 성능이 효율적.
    inner class PostHolder(var postDetailBinding: PostDetailBinding) : RecyclerView.ViewHolder(postDetailBinding.root) {
        private var position: Int? = null

        init {
            postDetailBinding.heartIcon.setOnClickListener {
                heartIconClickEvent(position!!)
            }
            //게시글의 이메일 텍스트뷰를 누르면 계정 정보 화면으로 이동.
            postDetailBinding.usernameTextView.setOnClickListener {
                (activity as MainActivity).changeFragment(AccountFragment((it as TextView).text.toString()))
            }
        }

        fun setData(content: ContentDTO, position: Int) {
            this.position = position

            postDetailBinding.usernameTextView.text = content.userEmail
            Glide.with(postDetailBinding.root).load(content.imageUrl).into(postDetailBinding.postPhotoIV)
            postDetailBinding.favoriteCountTextView.text = "좋아요 ${content.favoriteCount}개"
            postDetailBinding.postTextView.text = content.exaplain
            
            try {   //프로필 이미지 띄워주기
                Glide.with(postDetailBinding.root)
                    .load(usersHashMap[content.uid]!!.profileImgUrl!!)
                    .circleCrop()
                    .into(postDetailBinding.profileImageView)
            } catch(e: NullPointerException) {  //프로필 이미지가 없는 경우 -> 기본 이미지
                postDetailBinding.profileImageView.setImageResource(R.drawable.user)
            }

            if (content.favorites.containsKey(auth.currentUser!!.uid)) {
                //해당 게시글에 좋아요를 누른 사람이면
                postDetailBinding.heartIcon.setImageResource(R.drawable.heart_fill)
            } else {
                //해당 게시글에 좋아요를 누르지 않은 사람이면
                postDetailBinding.heartIcon.setImageResource(R.drawable.heart)
            }
        }

        private fun heartIconClickEvent(position: Int) {
            var postDoc = store.collection("posts").document(contentsId[position])
            store.runTransaction { transaction ->
                var content:ContentDTO = transaction.get(postDoc).toObject(ContentDTO::class.java)!!
                if (content.favorites.containsKey(auth.currentUser!!.uid)) {
                    //좋아요를 누른 사람이면 -> 좋아요 취소
                    content.favoriteCount -= 1
                    content.favorites.remove(auth.currentUser!!.uid)
                } else {
                    //좋아요를 누르지 않은 사람이면 -> 좋아요!
                    content.favoriteCount += 1
                    content.favorites.put(auth.currentUser!!.uid, true)
                }

                transaction.update(postDoc, "favorites", content.favorites)
                transaction.update(postDoc, "favoriteCount", content.favoriteCount)
            }.addOnSuccessListener {
                adapter?.notifyItemChanged(position)
            }.addOnFailureListener {
                Log.e(TAG, it.toString())
                Toast.makeText(activity, getString(R.string.error_alarm), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //아이템 간 수직(위아래) 간격 설정
    inner class VerticalItemDecorator(var divHeight: Int):RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = divHeight
            outRect.bottom = divHeight
        }
    }
}

