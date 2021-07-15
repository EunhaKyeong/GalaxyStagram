package com.galaxy.galaxystagram.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInIntentContract : ActivityResultContract<String, String>() {
    var googleSignInClient: GoogleSignInClient? = null

    //LoginActivity에서 구글 아이디로 로그인 버튼을 누르면 구글 로그인 화면으로 이동하는 intent를 만드는 함수.
    override fun createIntent(context: Context, clientId: String): Intent {
        //구글로그인 초기설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        //구글로그인 화면 intent
        val signInIntent = googleSignInClient!!.signInIntent

        return signInIntent
    }

    //구글 로그인 화면에서 다시 LoginActivity로 돌아올 때 호춯되는 함수.
    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode) {
            //정상적으로 로그인이 이뤄진 경우 getTokenId 함수를 통해 얻은 tokenId를 LoginActivity에 전달.
            Activity.RESULT_OK -> getTokenId(intent)
            else -> null
        }
    }

    //tokenId를 리턴해주는 함수.
    fun getTokenId(data: Intent?): String? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            return account.idToken
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            println("getTokenId() tokenId를 정상적으로 얻어오지 못함=>\n${e}")
            return null
        }
    }

}


