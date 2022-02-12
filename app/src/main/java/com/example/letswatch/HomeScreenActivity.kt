package com.example.letswatch




import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.letswatch.databinding.ActivityHomeScreenBinding
import com.example.letswatch.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class HomeScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var auth: FirebaseAuth
    private var navViewHeaderBinding : NavHeaderBinding? = null
    private var user: User? = null
    private var connectedUser: User? = null
    private lateinit var drawerLayout: DrawerLayout
    private val TAG = "HomeScreenActivity.kt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        getUser()
        menuSetUp()

    }


    fun getConnectedUser(){
        val db = Firebase.firestore
        db.collection("connections").document(user?.connectionId.toString()).get()
            .addOnSuccessListener{
                result ->
                val connection: Connection?
                val connectedUid: String?
                connection = result.toObject(Connection::class.java)
                if(connection?.firstUid == auth.uid){
                    connectedUid = connection?.secondUid
                }else{
                    connectedUid = connection?.firstUid
                }
                db.collection("users").document(connectedUid.toString()).get()
                    .addOnSuccessListener {
                    result ->
                        connectedUser = result.toObject(User::class.java)
                        navViewHeaderBinding!!.connectedUser = connectedUser
                        Log.w(TAG,"Connected User: ${connectedUser?.username.toString()}")
                }
            }
    }

    fun setUpFragment() {
        if(user?.connectionId?.length!! > 0) {
            replaceFragment(StartFragment())
            addFragment(BeginMovieSuggestion())
            getConnectedUser()
        }else{
            replaceFragment(NotConnectedFragment())
            connectedUser = User("", "Not Connected To Anyone")
            navViewHeaderBinding!!.connectedUser = connectedUser
            setConnectedListener()
        }
    }

    fun setConnectedListener() {
        val db = Firebase.firestore
        db.collection("users").document(user!!.uid.toString()).addSnapshotListener{snapshot,
            e ->
            db.collection("users").document(user!!.uid.toString()).get().addOnSuccessListener{
                result ->
                var newData: User = result.toObject(User::class.java)!!
                if(newData.connectionId!!.length > 1){
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun setNavigationViewListener() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.bringToFront()

        val menu: Menu = navigationView.getMenu()

        val moviesGroup: MenuItem = menu.findItem(R.id.moviesGroup)
        val moreGroup: MenuItem = menu.findItem(R.id.moreGroup)
        val generalGroup: MenuItem = menu.findItem(R.id.generalGroup)
        val s = SpannableString(moviesGroup.title)
        val s1 = SpannableString(moreGroup.title)
        val s2 = SpannableString(generalGroup.title)
        s.setSpan(TextAppearanceSpan(this, R.style.text), 0, s.length, 0)
        s1.setSpan(TextAppearanceSpan(this, R.style.text), 0, s1.length, 0)
        s2.setSpan(TextAppearanceSpan(this, R.style.text), 0, s2.length, 0)
        moviesGroup.title = s
        moreGroup.title = s1
        generalGroup.title = s2
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()
    }

    override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.home -> {
                replaceFragment(StartFragment())
            }
            R.id.toWatch -> {
                replaceFragment(MatchesRecyclerFragment())
            }
            R.id.watchedTogetherList -> {
                replaceFragment(WatchedTogetherRecyclerFragment())
            }
            R.id.watchedAloneList -> {
                replaceFragment(WatchedSoloFragment())
            }
            R.id.settings -> {
                Toast.makeText(this, "settings",Toast.LENGTH_SHORT).show()
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun replaceFragment(newFragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager?.beginTransaction()

        transaction.replace(R.id.homeScreenFragmentContainer, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun addFragment(newFragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager?.beginTransaction()
        transaction.add(R.id.drawer_layout, newFragment)
        transaction.commit()
    }



    fun menuSetUp(){
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        setNavigationViewListener()
        val menuBtn = findViewById<ImageView>(R.id.menuBtn)
        menuBtn.setOnClickListener(){
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            navigationView.bringToFront()
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun getUser() {
        val binding: ActivityHomeScreenBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_home_screen)
        val viewHeader = binding.navView.getHeaderView(0)
        navViewHeaderBinding = NavHeaderBinding.bind(viewHeader)
        navViewHeaderBinding!!.lifecycleOwner = this
        val db = Firebase.firestore
        var currentUser: User? = null
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            result ->
            currentUser = result.toObject(User::class.java)
            Log.w(TAG, "uid: ${auth.currentUser?.uid.toString()}  username: ${currentUser?.username.toString()}")
            navViewHeaderBinding!!.user = currentUser
            user = currentUser
            Picasso.get()
                .load("https://avatars.dicebear.com/api/bottts/" + currentUser!!.username!! + ".png?background=%23212121")
                .into(navViewHeaderBinding!!.avatarIcon)
            setUpFragment()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        navViewHeaderBinding = null
    }

}