package com.galaxy.galaxystagram.navigation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.MainActivity
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.FragmentGalleryBinding
import com.galaxy.galaxystagram.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment(uri: Uri): Fragment() {
    private lateinit var mBinding: FragmentGalleryBinding
    private var uri: Uri = uri
    private var auth: FirebaseAuth? = null
    private var store: FirebaseFirestore? = null
    private var TAG: String = "GalleryFragment: "

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentGalleryBinding.inflate(inflater, container, false)

        //firebase 서비스 초기화
        auth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()

        mBinding.postPhotoIV.setImageURI(uri)   //갤러리에서 선택한 이미지를 해당 이미지뷰에서 보여줌.
        //게시글 업로드 버튼을 누르면 Firebase Storage에 이미지를 업로드 하는 함수 실행.
        mBinding.postButton.setOnClickListener {
            uploadImageTOFirebase(uri!!)
        }

        return mBinding.root
    }

    //Firebase Storage에 이미지를 업로드 하는 함수.
    fun uploadImageTOFirebase(uri: Uri) {
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
        //파일 이름 생성.
        var fileName = "IMAGE_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"
        //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
        //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
        var imagesRef = storage!!.reference.child("images/").child(fileName)    //기본 참조 위치/images/${fileName}
        //이미지 파일 업로드
        imagesRef.putFile(uri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot>->
            //Firebase Storage에 업로드된 이미지의 downloadUrl을 리턴한다.
            return@continueWithTask imagesRef.downloadUrl
        }.addOnSuccessListener {
            //ContentDTO 데이터 클래스 생성.
            var contentDTO: ContentDTO = ContentDTO()
            contentDTO.exaplain = mBinding.postTextView.text.toString()
            contentDTO.imageUrl = it.toString()
            contentDTO.uid = auth!!.currentUser!!.uid
            contentDTO.userEmail = auth!!.currentUser!!.email
            contentDTO.timestamp = System.currentTimeMillis()

            //db에 데이터 저장.
            store!!.collection("posts").document().set(contentDTO)
            //저장 후 홈 프래그먼트로 이동.
            (activity as MainActivity).changeFragment(HomeFragment())
            (activity as MainActivity).changeNavigation()
        }.addOnFailureListener {
            Log.e(TAG, it.toString())
            Toast.makeText(activity, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                mainActivity.changeFragment(HomeFragment())
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        callback.remove()
//    }
}