package com.example.letswatch

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.example.letswatch.databinding.ActivityHomeScreenBinding
import com.example.letswatch.databinding.FragmentNotConnectedBinding
import com.example.letswatch.databinding.NavHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.zip.Inflater


class NotConnectedFragment : Fragment(R.layout.fragment_not_connected) {

    private val TAG = "NotConnectedFragment.kt"
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val fragmentNotConnectedBinding = DataBindingUtil.inflate<FragmentNotConnectedBinding>(inflater, R.layout.fragment_not_connected, null, false)
        val view = fragmentNotConnectedBinding.root
        fragmentNotConnectedBinding.lifecycleOwner = this
        generateCodeBtnAction(view)
        getUser(fragmentNotConnectedBinding)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generateCodeBtnAction(view)
        super.onViewCreated(view, savedInstanceState)
    }

    fun generateCodeBtnAction(view: View){
        val button = view.findViewById<Button>(R.id.GenerateCodeButton)
        button.setOnClickListener(){
            (activity as HomeScreenActivity?)!!.replaceFragment(GenerateCodeFragment())
        }
    }

    fun getUser(fragmentNotConnectedBinding: FragmentNotConnectedBinding) {
        val db = Firebase.firestore
        var currentUser: User?
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                result ->
            currentUser = result.toObject(User::class.java)
            Log.w(TAG, "uid: ${auth.uid.toString()}    username: ${currentUser?.username.toString()}")
            fragmentNotConnectedBinding.user = currentUser
        }
    }
}