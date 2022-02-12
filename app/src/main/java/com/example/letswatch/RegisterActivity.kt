package com.example.letswatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val TAG = "RegisterActivity.kt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database.reference
        val signInBtn = findViewById<Button>(R.id.registerButton)
        val switchToLogin = findViewById<TextView>(R.id.gotoLogIn)

        switchToLogin.setOnClickListener(){ goToLogIn() }
        signInBtn.setOnClickListener(){ startRegister() }
    }


    fun goToLogIn(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun startRegister(){
        val db = Firebase.firestore
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val repeatPassInput = findViewById<EditText>(R.id.repeatPassInput)
        val username = findViewById<EditText>(R.id.usernameInput)
        if(passwordInput.text.toString() != repeatPassInput.text.toString())
        {
            Toast.makeText(this, "Passwords do NOT match", Toast.LENGTH_SHORT).show()
            return
        }
        var email = emailInput.text.toString()
        var password = passwordInput.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val userInfo = User(user!!.uid,username.text.toString(),user.email,"","1","0")

// Add a new document with a generated ID
                    db.collection("users").document(user!!.uid.toString()).set(userInfo)

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
}