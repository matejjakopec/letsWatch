package com.example.letswatch

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WatchedSoloFragment : Fragment() {
    private var movies: MutableList<MovieData>? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movies = mutableListOf<MovieData>()
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watched_solo, container, false)
        getMovieIds(view)
        val explanation = view.findViewById<TextView>(R.id.youWatchSoloText)
        Handler().postDelayed({
            explanation.animate().alpha(0.0f).setDuration(1000).withEndAction {
                explanation.setVisibility(View.GONE)
            }
        }, 5000)
        return view
    }
    fun getMovieIds(view: View){
        val db = Firebase.firestore
        var connectionId: String? = null
        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
                result ->
            var user: User? = result.toObject(User::class.java)
            connectionId = user!!.connectionId
            db.collection("connections").document(connectionId!!).get().addOnSuccessListener {
                    result1 ->
                var connection: Connection? = result1.toObject(Connection::class.java)
                var watchedIds: MutableList<String>
                if(user.uid == connection!!.firstUid){
                    watchedIds = connection.firstWatchedIds!!
                }else{
                    watchedIds = connection.secondWatchedIds!!
                }
                getMovies(watchedIds, view)
            }
        }

    }

    fun getMovies(list: MutableList<String>, view: View){
        for(movieId in list){
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
                    movies!!.add(movieData)
                    if(movies!!.size == list.size){
                        setUpRecyclerView(view)
                    }
                }

                override fun onFailure(call: Call<MovieAPI>, t: Throwable) {
                    Log.e("Error",t.message.toString())
                }
            })
        }
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

    fun setUpRecyclerView(view: View) {
        val recyclerview = view.findViewById<RecyclerView>(R.id.soloWatchrecyclerview)
        recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view.context)
            adapter = MovieAdapterNoButton(movies!!)
            var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(
                AppCompatResources.getDrawable(
                    this.context,
                    R.drawable.divider
                )!!)
            addItemDecoration(itemDecoration)
        }
    }


}