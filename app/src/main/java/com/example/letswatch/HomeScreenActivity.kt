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
        usernameHeader.text = "Welcome " + getUsername()
        menuBtn.setOnClickListener(){
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
    fun getUsername(): String{

        val db = Firebase.firestore
        var username = ""
        db.collection("users").whereEqualTo("uid", auth.currentUser!!.uid).get().addOnSuccessListener { documents ->
            for (document in documents) {
                username = document.data["username"].toString()
            }
        }.toString()

        return username
    }
}