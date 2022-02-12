package com.example.letswatch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class BeginMovieSuggestion : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_begin_movie_suggestion, container, false)
        val button = view.findViewById<Button>(R.id.startMovieSuggestionButton)
        button.setOnClickListener {
            val intent = Intent((activity as HomeScreenActivity), SwipeSuggestionActivity::class.java)
            startActivity(intent)
        }
        return view
    }


}