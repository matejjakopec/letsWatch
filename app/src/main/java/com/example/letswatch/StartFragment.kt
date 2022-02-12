package com.example.letswatch

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.letswatch.databinding.FragmentNotConnectedBinding
import com.example.letswatch.databinding.FragmentStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.random.Random.Default.nextInt

class StartFragment : Fragment() {
    private val TAG = "StartFragment.kt"
    private lateinit var auth: FirebaseAuth
    private var currentUser: User? = null
    private var connectedUser: User? = null
    private var connection: Connection? = null
    private var randomMovieId: String? = null
    private var fragmentStartDataBinding: FragmentStartBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        fragmentStartDataBinding = DataBindingUtil.inflate<FragmentStartBinding>(inflater, R.layout.fragment_start, null, false)
        val view = fragmentStartDataBinding!!.root
        fragmentStartDataBinding!!.lifecycleOwner = this
        getUser()
        // Inflate the layout for this fragment
        return view
    }

    fun getUser() {
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                result ->
            currentUser = result.toObject(User::class.java)
            Log.w(TAG, "uid: ${auth.uid.toString()}    username: ${currentUser?.username.toString()}")
            fragmentStartDataBinding!!.user = currentUser
            getConnectedUser()
        }
    }

    fun getConnectedUser(){
        val db = Firebase.firestore
        db.collection("connections").document(currentUser?.connectionId.toString()).get()
            .addOnSuccessListener{
                    result ->
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
                        fragmentStartDataBinding!!.connectedUser = connectedUser
                        fragmentStartDataBinding!!.invalidateAll()
                        getRandomIdToWatch()
                        getLastWatchedTogetherId()
                        getTotalInfo()
                        Log.w(TAG,"Connected User: ${connectedUser?.username.toString()}")
                    }
            }
    }

    fun getRandomIdToWatch() {
        var toWatchIds: MutableList<String> = connection!!.firstLikedIds!!.intersect(connection!!.secondLikedIds!!).minus(connection!!.watchedTogetherIds!!.toList()).toMutableList()
        if(toWatchIds.size>0){
            var randomIndex: Int = Random().nextInt(toWatchIds.size)
            randomMovieId = toWatchIds[randomIndex!!]
            getRandomMovieToWatch(randomMovieId!!)
        }else{
            fragmentStartDataBinding!!.watchTogetherInfoText.text = "You dont have any matched, please Start Discovering"
            fragmentStartDataBinding!!.randomSuggestedMovieImage.setVisibility(View.GONE)
            fragmentStartDataBinding!!.randomMovieTogetherTitle.setVisibility(View.GONE)
            fragmentStartDataBinding!!.randomMovieTogetherReleaseDate.setVisibility(View.GONE)
            fragmentStartDataBinding!!.watchedTogetherRandomButton.setVisibility(View.GONE)
        }
    }

    fun getRandomMovieToWatch(movieId: String){
            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/movie/").build()
            val movieapi = retrofit.create(IMovieAPI::class.java)
            val mcall: Call<MovieAPI> = movieapi.getMovie(movieId, "921e7839ab441454d094115e5356728f", "en-US")
            mcall.enqueue(object: Callback<MovieAPI> {
                override fun onResponse(call: Call<MovieAPI>, response: Response<MovieAPI>) {
                    var movieAPI: MovieAPI = response.body()!!
                    var movieData: MovieData = MovieData(movieAPI.title, "Release date: " + movieAPI.release_date,
                        getGenres(movieAPI.genres!!), (movieAPI.vote_average),
                        movieAPI.overview, movieAPI.backdrop_path, movieAPI.id)
                        fragmentStartDataBinding!!.toWatchMovie = movieData
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + (movieAPI.backdrop_path))
                        .into(fragmentStartDataBinding!!.randomSuggestedMovieImage)
                        fragmentStartDataBinding!!.watchedTogetherRandomButton.setOnClickListener {
                            var newData: MutableList<String>? = connection!!.watchedTogetherIds
                            newData!!.add(randomMovieId!!)
                            val db = Firebase.firestore
                            db.collection("connections")
                                .document(currentUser?.connectionId.toString()).update("watchedTogetherIds", newData)
                            getRandomIdToWatch()
                    }
                }

                override fun onFailure(call: Call<MovieAPI>, t: Throwable) {
                    Log.e("Error",t.message.toString())
                }
            })
    }

    fun getGenres(genres: Array<Genre>): String{
        var builder = StringBuilder()
        var first = true
        for(genre in genres){
            if(first){
                first = false
                builder.append("Genres: "+genre.name)
            }else{
                builder.append(", " + genre.name)
            }
        }
        return builder.toString()
    }

    fun getLastWatchedTogetherId(){
        if(connection!!.watchedTogetherIds!!.size > 0){
            getLastWatchedTogetherMovie(connection!!.watchedTogetherIds!!.last())
        }else{
            fragmentStartDataBinding!!.watchedlastTextInfo.text = "You havent watched anything together"
            fragmentStartDataBinding!!.lastWatchedMovieImage.setVisibility(View.GONE)
            fragmentStartDataBinding!!.lastWatchedMovieTitle.setVisibility(View.GONE)
            fragmentStartDataBinding!!.lastWatchedMovieRelaseDate.setVisibility(View.GONE)
        }
    }

    fun getLastWatchedTogetherMovie(movieId: String){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/3/movie/").build()
        val movieapi = retrofit.create(IMovieAPI::class.java)
        val mcall: Call<MovieAPI> = movieapi.getMovie(movieId, "921e7839ab441454d094115e5356728f", "en-US")
        mcall.enqueue(object: Callback<MovieAPI> {
            override fun onResponse(call: Call<MovieAPI>, response: Response<MovieAPI>) {
                var movieAPI: MovieAPI = response.body()!!
                var movieData: MovieData = MovieData(movieAPI.title, "Release date: " + movieAPI.release_date,
                    getGenres(movieAPI.genres!!), (movieAPI.vote_average),
                    movieAPI.overview, movieAPI.backdrop_path, movieAPI.id)
                fragmentStartDataBinding!!.lastWatchedMovie = movieData
                Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + (movieAPI.backdrop_path))
                    .into(fragmentStartDataBinding!!.lastWatchedMovieImage)
            }

            override fun onFailure(call: Call<MovieAPI>, t: Throwable) {
                Log.e("Error",t.message.toString())
            }
        })
    }

    fun getTotalInfo(){
        fragmentStartDataBinding!!.suggestedMoviesNumber.text = ((currentUser!!.currectMoviePage!!.toInt() - 1)*20 + currentUser!!.movieOnPageIndex!!.toInt()).toString()
        var liked: String? = null
        if(connection!!.firstUid == auth.uid){
            liked = connection!!.firstLikedIds!!.size.toString()
        }else{
            liked = connection!!.secondLikedIds!!.size.toString()
        }
        fragmentStartDataBinding!!.likedMoviesNumber.text = liked
        fragmentStartDataBinding!!.likedbyBothNumber.text = (connection!!.firstLikedIds!!.intersect(connection!!.secondLikedIds!!).size).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentStartDataBinding = null
    }

}