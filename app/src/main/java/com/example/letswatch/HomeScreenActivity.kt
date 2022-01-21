package com.example.letswatch

import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "HomeScreenActivity.kt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuBtn = findViewById<ImageView>(R.id.menuBtn)
        val usernameHeader = findViewById<TextView>(R.id.welcomeUser)
        auth = Firebase.auth
        usernameHeader.text = getUsername()
        menuBtn.setOnClickListener() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun getUsername(): String? {

        val db = Firebase.firestore
        var currentUser: User? = null
        var users: List<User?> = arrayListOf()
        currentUser = User("novi","novi","novi")
//        var query = db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
//            result ->
//            username = result.get("username").toString()
//        }


//        .get().addOnSuccessListener { result ->
//            users = result.toObjects(User::class.java)
//            for(user in users){
//                if(user?.uid.toString().contains(auth.currentUser?.uid.toString())){
//                    currentUser = user
//                }
//            }
//        }

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users = result.toObjects(User::class.java)
                var currentUser: User ? = null
                for(user in users){
                    Log.d(TAG, "user ${user?.email.toString()}")
                    if(user?.email.toString().contains(auth.currentUser?.email.toString())){
                        currentUser =  user
                        Log.d(TAG, "valja ${currentUser?.email.toString()}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        return currentUser?.username.toString()
    }
}