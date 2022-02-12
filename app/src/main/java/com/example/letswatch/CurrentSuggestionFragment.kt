package com.example.letswatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.letswatch.databinding.FragmentCurrentSuggestionBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class CurrentSuggestionFragment : Fragment() {
    private val TAG = "CurrentSuggestion.kt"
    var movies: Array<Movie>? = null
    var index: Int? = null
    var viewModel: ItemViewModel? = null
    var fragmentCurrentSuggestionBinding: FragmentCurrentSuggestionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCurrentSuggestionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_suggestion, null, false)
        val view = fragmentCurrentSuggestionBinding!!.root
        fragmentCurrentSuggestionBinding!!.lifecycleOwner = this
        fragmentCurrentSuggestionBinding!!.deniedMovieButton.setOnClickListener { deniedAction() }
        fragmentCurrentSuggestionBinding!!.acceptedMovieButton.setOnClickListener { acceptedAction() }
        fragmentCurrentSuggestionBinding!!.watchedMovieButton.setOnClickListener { watchedAction() }
//         Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ItemViewModel::class.java)
        movies = viewModel!!.getMoviesVM().value
        index = viewModel!!.getUser().value!!.movieOnPageIndex!!.toInt()
        showMovie()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    val intent = Intent((activity as SwipeSuggestionActivity), HomeScreenActivity::class.java)
                    startActivity(intent)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }


    fun nextFragment(){
        (activity as SwipeSuggestionActivity).replaceFragment(CurrentSuggestionFragment())
    }

    fun nextIndex(){
        val db = Firebase.firestore
        if(index!! < 19){
            db.collection("users")
                .document(viewModel!!.getUser().value!!.uid.toString())
                .update("movieOnPageIndex", (index!! + 1).toString())
            var newIndex = index!! + 1
            var newUser = viewModel!!.getUser().value
            newUser!!.movieOnPageIndex = newIndex.toString()
            viewModel!!.setUser(newUser)
        }else{
            var newPage = viewModel!!.getUser().value!!.currectMoviePage!!.toInt() + 1
            db.collection("users")
                .document(viewModel!!.getUser().value!!.uid.toString())
                .update("movieOnPageIndex", "0")
            db.collection("users")
                .document(viewModel!!.getUser().value!!.uid.toString())
                .update("currectMoviePage", newPage.toString())
            var newUser = viewModel!!.getUser().value
            newUser!!.movieOnPageIndex = "0"
            newUser!!.currectMoviePage = newPage.toString()

            viewModel!!.setUser(newUser)
            viewModel!!.setUser(newUser)
        }
        nextFragment()
    }

    fun deniedAction(){
        val db = Firebase.firestore
        var newData: Connection? = viewModel!!.getConnection().value
        newData!!.deniedIds!!.add(movies!![index!!].id.toString())
        viewModel!!.setConnection(newData)
        db.collection("connections").document(viewModel!!.getUser().value!!.connectionId.toString()).update("deniedIds", newData.deniedIds)
        nextIndex()
    }

    fun acceptedAction(){
        val db = Firebase.firestore
        var userString: String
        var newData: Connection? = null
        if(viewModel!!.getUser().value!!.uid.toString() == viewModel!!.getConnection().value!!.firstUid.toString()){
            userString = "firstLikedIds"
            newData = viewModel!!.getConnection().value
            newData!!.firstLikedIds!!.add(movies!![index!!].id.toString())
            db.collection("connections").document(viewModel!!.getUser().value!!.connectionId.toString()).update(userString, newData.firstLikedIds)
        }
        else{
            userString = "secondLikedIds"
            newData = viewModel!!.getConnection().value
            newData!!.secondLikedIds!!.add(movies!![index!!].id.toString())
            db.collection("connections").document(viewModel!!.getUser().value!!.connectionId.toString()).update(userString, newData.secondLikedIds)
        }
        viewModel!!.setConnection(newData)
        nextIndex()
    }

    fun watchedAction(){
        val db = Firebase.firestore
        var userString: String
        var newData: Connection? = null
        if(viewModel!!.getUser().value!!.uid.toString() == viewModel!!.getConnection().value!!.firstUid.toString()){
            userString = "firstWatchedIds"
            newData = viewModel!!.getConnection().value
            newData!!.firstWatchedIds!!.add(movies!![index!!].id.toString())
            db.collection("connections").document(viewModel!!.getUser().value!!.connectionId.toString()).update(userString, newData.firstWatchedIds)
        }
        else{
            userString = "secondWatchedIds"
            newData = viewModel!!.getConnection().value
            newData!!.secondWatchedIds!!.add(movies!![index!!].id.toString())
            db.collection("connections").document(viewModel!!.getUser().value!!.connectionId.toString()).update(userString, newData.secondWatchedIds)
        }
        viewModel!!.setConnection(newData)
        nextIndex()
    }

    fun showMovie(){
        var genreConverted = convertGenres(movies!![index!!].genre_ids)
        val movieData = MovieData(movies!![index!!].title, movies!![index!!].release_date,
            genreConverted,
            movies!![index!!].vote_average + "  (" + movies!![index!!].vote_count + ")", movies!![index!!].overview)
        fragmentCurrentSuggestionBinding!!.movieData = movieData
        fragmentCurrentSuggestionBinding!!.descriptionText.movementMethod = ScrollingMovementMethod()
        Picasso.get()
            .load("https://image.tmdb.org/t/p/original" + (movies!![index!!].backdrop_path  ))
            .into(fragmentCurrentSuggestionBinding!!.imageCover)
    }

    fun convertGenres(genresToConvert: Array<String>?): String{
        var builder = StringBuilder()
        var genres: Array<Genre> = viewModel!!.getGenres().value!!
        var first: Boolean = true
        for(genre in genresToConvert!!){
            for(g in genres){
                if(genre == g.id){
                    if(first){
                        first = false
                        builder.append(g.name)
                    }else{
                        builder.append(", " + g.name)
                    }
                }
            }
        }
        return builder.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCurrentSuggestionBinding = null
    }



}