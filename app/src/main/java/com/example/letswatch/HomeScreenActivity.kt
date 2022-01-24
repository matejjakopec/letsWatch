package com.example.letswatch

import android.graphics.Insets.add
import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.Control
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.example.letswatch.databinding.ActivityHomeScreenBinding
import com.example.letswatch.databinding.ActivityMainBinding
import com.example.letswatch.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class HomeScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "HomeScreenActivity.kt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        getUser()
        menuSetUp()
        replaceFragment(NotConnectedFragment())
    }

    fun replaceFragment(newFragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager?.beginTransaction()

        transaction.replace(R.id.homeScreenFragmentContainer, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun menuSetUp(){
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuBtn = findViewById<ImageView>(R.id.menuBtn)
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

    fun getUser() {
        val binding: ActivityHomeScreenBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_home_screen)
        val viewHeader = binding.navView.getHeaderView(0)
        val navViewHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(viewHeader)
        val db = Firebase.firestore
        var currentUser: User? = null
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            result ->
            currentUser = result.toObject(User::class.java)
            Log.w(TAG, "uid: ${auth.currentUser?.uid.toString()}  username: ${currentUser?.username.toString()}")
            navViewHeaderBinding.user = currentUser
        }
    }

}