package com.example.letswatch

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.letswatch.databinding.FragmentGenerateCodeBinding
import com.example.letswatch.databinding.FragmentNotConnectedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class GenerateCodeFragment : Fragment() {
    private val TAG = "GenerateCodeFragment.kt"
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val fragmentGenerateCodeFragment = DataBindingUtil.inflate<FragmentGenerateCodeBinding>(inflater, R.layout.fragment_generate_code, null, false)
        val view = fragmentGenerateCodeFragment.root
        fragmentGenerateCodeFragment.lifecycleOwner = this
        getGeneratedCode(fragmentGenerateCodeFragment)
        // Inflate the layout for this fragment
        return view
    }

    fun getGeneratedCode(fragmentGenerateCodeFragment: FragmentGenerateCodeBinding){
        val db = Firebase.firestore
        var code: Code?
        db.collection("codes").whereEqualTo("uid",auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                result ->
            var codes = result.toObjects(Code::class.java)
            if(codes.isEmpty()){
                generateCode(fragmentGenerateCodeFragment)
            }else{
                code = codes[0]
                Log.w(TAG, "Found code for uid: ${auth.uid.toString()}    code: ${code?.code}")
                fragmentGenerateCodeFragment.code = code
            }
        }
    }
    fun generateCode(fragmentGenerateCodeFragment: FragmentGenerateCodeBinding){
        val potentialCode: Int = Random().nextInt(900000) + 100000
        val db = Firebase.firestore
        var code: Code?
        db.collection("users").document(potentialCode.toString()).get().addOnSuccessListener {
                result ->
            code = result.toObject(Code::class.java)
            if(code == null){
                code = Code(auth.currentUser?.uid.toString(), potentialCode.toString())
                db.collection("codes").document(potentialCode.toString()).set(code!!)
                fragmentGenerateCodeFragment.code = code
                Log.w(TAG, "Generated new code for uid: ${auth.uid.toString()}  code: ${code?.code}")
            }else{
                generateCode(fragmentGenerateCodeFragment)
            }
        }
    }

}