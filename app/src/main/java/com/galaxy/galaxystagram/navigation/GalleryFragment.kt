package com.galaxy.galaxystagram.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.galaxy.galaxystagram.R
import com.galaxy.galaxystagram.databinding.FragmentGalleryBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment(uri: Uri): Fragment() {
    private lateinit var mBinding: FragmentGalleryBinding
    private var uri: Uri? = uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentGalleryBinding.inflate(inflater, container, false)

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
        imagesRef.putFile(uri!!).addOnSuccessListener {
            Toast.makeText(activity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            println(it)
            Toast.makeText(activity, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
        }
    }
}