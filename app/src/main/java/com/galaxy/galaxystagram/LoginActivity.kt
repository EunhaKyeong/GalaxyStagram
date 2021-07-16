package com.galaxy.galaxystagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.galaxy.galaxystagram.contract.SignInIntentContract
import com.galaxy.galaxystagram.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var auth: FirebaseAuth? = null
    var launcher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해
        //액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        binding = ActivityLoginBinding.inflate(layoutInflater)
        //getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의 인스턴스를 활용해 생성된 뷰를 액티비티에 표시.
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //LoginActivity -> 구글로그인 화면 -> LoginActivity로 돌아온 후 콜백 함수.
        //구글로그인 화면을 통해 얻어온 tokenId를 이용해 Firebase 사용자 인증 정보로 교환하고
        //해당 정보를 사용해 Firebase에 인증합니다.
        launcher = registerForActivityResult(SignInIntentContract()) { result: String? ->
            result?.let {
                firebaseAuthWithGoogle(it)  //tokenId를 이용해 firebase에 인증하는 함수 호출.
            }
        }

        binding.emailLoginButton.setOnClickListener {
            signUp()
        }
        binding.googleLoginButton.setOnClickListener {
            //Launcher를 실행해 LoginActivity -> 구글 로그인 화면으로 이동.
            launcher!!.launch(getString(R.string.default_web_client_id))
        }
    }

    //회원가입
    fun signUp() {
        var email: String = binding.emailEditText.text.toString()
        var password: String = binding.passwordEditText.text.toString()

        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    //로그인 성공
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.signin_complete), Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else if (task.exception!!.toString()
                        .contains("FirebaseAuthWeakPasswordException")
                ) {
                    //회원가입 시 비밀번호가 6자리 이상으로 입력하지 않은 경우
                    Toast.makeText(
                        this@LoginActivity, "비밀번호는 6자리 이상이어야 합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (task.exception!!.toString()
                        .contains("FirebaseAuthUserCollisionException")
                ) {
                    //이미 존재하는 사용자 -> 로그인 함수 호출.
                    emailSignIn()
                } else {
                    println(task.exception.toString())
                    Toast.makeText(
                        this@LoginActivity, task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //이메일 로그인
    fun emailSignIn() {
        var email: String = binding.emailEditText.text.toString()
        var password: String = binding.passwordEditText.text.toString()

        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //로그인 성공
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.signin_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    //로그인 실패
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.signout_fail_null),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //tokenId를 이용해 firebase에 인증하는 함수.
    fun firebaseAuthWithGoogle(idToken: String) {
        //it가 tokenId, credential은 Firebase 사용자 인증 정보.
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        //Firebase 사용자 인증 정보(credential)를 사용해 Firebase에 인증.
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.signin_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    println("firebaseAuthWithGoogle => ${task.exception}")
                    Toast.makeText(
                        this@LoginActivity, getString(R.string.signin_google_faile),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}