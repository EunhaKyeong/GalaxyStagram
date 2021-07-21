package com.galaxy.galaxystagram.navigation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.FragmentHomeBinding
import com.galaxy.galaxystagram.databinding.PostDetailBinding
import com.galaxy.galaxystagram.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment: Fragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        mBinding.postsRecyclerView.adapter = Adapter()
        mBinding.postsRecyclerView.layoutManager = LinearLayoutManager(activity)

        return mBinding.root
    }

    inner class Adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private lateinit var postDetailBinding: PostDetailBinding
        private var store: FirebaseFirestore = FirebaseFirestore.getInstance()
        private var contentsList = arrayListOf<ContentDTO>()

        init {
            store.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { posts, e->
                    if (e!=null) {
                        println(e)
                        Toast.makeText(activity, R.string.upload_fail, Toast.LENGTH_SHORT).show()
                    } else {
                        contentsList.clear()
                        for (post in posts!!.documents) {
                            var content = post.toObject(ContentDTO::class.java)
                            contentsList.add(content!!)
                        }
                    }
                    notifyDataSetChanged()
                }
            println(contentsList.toString())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            postDetailBinding = PostDetailBinding.inflate(inflater, parent, false)

            return Holder(postDetailBinding.root)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            postDetailBinding.usernameTextView.text = contentsList.get(position)!!.userEmail
            Glide.with(holder.itemView).load(contentsList.get(position).imageUrl).into(postDetailBinding.postPhotoIV)
            postDetailBinding.favoriteCountTextView.text = "좋아요 ${contentsList.get(position)!!.favoriteCount}개"
            postDetailBinding.postTextView.text = contentsList.get(position).exaplain
        }

        override fun getItemCount(): Int {
            return contentsList.size
        }
    }

    inner class Holder(root: View) : RecyclerView.ViewHolder(root)
}

