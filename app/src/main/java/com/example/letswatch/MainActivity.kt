package com.example.letswatch

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "MainActivity.kt"
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val switchToRegister = findViewById<TextView>(R.id.gotoRegister)
        val signInBtn = findViewById<Button>(R.id.signinButton)
        signInBtn.setOnClickListener(){goToSignIn()}
        switchToRegister.setOnClickListener(){ goToRegister() }
    }



    fun checkIfLoggedIn(){
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }

    fun goToSignIn(){
        val emailText = findViewById<EditText>(R.id.emailSignInInput)
        val passwordText = findViewById<EditText>(R.id.passwordInput)
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        if(!isValidEmail(email)){
            Toast.makeText(this, "Email is NOT correct", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.length < 6){
            Toast.makeText(this, "Password length is too short, check again", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "login:success uid: ${auth.currentUser?.uid.toString()}")

                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
    fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}