package com.galaxy.galaxystagram.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class SignInIntentContract: ActivityResultContract<String, String>() {
    var googleSignInClient: GoogleSignInClient? = null
    var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    var context: Context? = null

    override fun createIntent(context: Context, clientId: String): Intent {
        this.context = context
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

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode) {
            Activity.RESULT_OK -> getTokenId(intent)
            else -> null
        }
    }

    fun getTokenId(data: Intent?):String? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!

            return account.idToken!!
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            println("Google sign in failed=>${e}")
            return null
        }
    }

//    fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth!!.signInWithCredential(credential)
//            .addOnCompleteListener(context) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user = auth!!.currentUser
//                } else {
//                    // If sign in fails, display a message to the user.
//                }
//            }
//    }
}
