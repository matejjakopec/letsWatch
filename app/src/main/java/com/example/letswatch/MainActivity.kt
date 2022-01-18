package com.example.letswatch

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val switchToRegister = findViewById<TextView>(R.id.gotoRegister)
        switchToRegister.setOnClickListener(){ goToRegister() }
    }

    fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}