package com.example.letswatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ConnectCodeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect_code, container, false)
        setUpButton(view)
        return view

    }

    fun setUpButton(view: View){
        val button = view.findViewById<Button>(R.id.connectCodeButton)
        button.setOnClickListener {
            val input = view.findViewById<TextView>(R.id.editTextNumber)
            val number = input.text.toString()
            if(number.length != 6){
                Toast.makeText((activity as HomeScreenActivity?),
                    "Check code, MUST be 6 digits", Toast.LENGTH_SHORT).show()
            }else{
                connectUser(number)
            }
        }
    }
    fun connectUser(number: String) {
        val db = Firebase.firestore
        var code: Code?
        db.collection("codes").document(number).get().addOnSuccessListener{ value ->
            code = value.toObject(Code::class.java)
            if(code == null){
                Toast.makeText((activity as HomeScreenActivity), "Check code, user with this code doesn't exist",
                Toast.LENGTH_LONG).show()
            }else{
                if(code?.uid == auth.uid){
                    Toast.makeText((activity as HomeScreenActivity),"You can't connect to yourself",
                    Toast.LENGTH_LONG).show()
                }else{
                    val connection = Connection(auth.uid, code?.uid, mutableListOf<String>(),
                        mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>())
                    var docId: String?
                    db.collection("connections").add(connection).addOnSuccessListener{ doc ->
                        docId = doc.id
                        db.collection("users").document(auth.uid.toString()).update("connectionId",docId)
                        db.collection("users").document(code?.uid.toString()).update("connectionId",docId)
                        (activity as HomeScreenActivity?)?.recreate()
                    }

                }
            }
        }
    }
}