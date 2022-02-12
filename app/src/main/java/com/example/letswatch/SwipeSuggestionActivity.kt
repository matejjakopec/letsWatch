package com.example.letswatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SwipeSuggestionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var user: User? = null
    var connection: Connection? = null
    var movies: Array<Movie>? = null
    var apiData: APIData? = null
    var viewModel: ItemViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_swipe_suggestion)
        getInfo()
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

    }


    fun getInfo(){
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                result ->
            user = result.toObject(User::class.java)
            db.collection("connections").document(user!!.connectionId.toString()).get().addOnSuccessListener {
                    result1 ->
                connection = result1.toObject(Connection::class.java)
                viewModel!!.setUser(user!!)
                viewModel!!.setConnection(connection!!)
                getGenres()
                getMovies()
            }
        }

    }

    fun getMovies(){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/3/discover/").build()
        val jsonapi = retrofit.create(JsonAPI::class.java)
        val mcall: Call<APIData> = jsonapi.getMovies("921e7839ab441454d094115e5356728f",
            "en-US", "popularity.desc", "false", "false",
            viewModel!!.getUser().value!!.currectMoviePage.toString(), "flatrate")
        mcall.enqueue(object: Callback<APIData> {
            override fun onResponse(call: Call<APIData>, response: Response<APIData>) {
                apiData = response.body()!!
                movies = apiData!!.results!!
                viewModel!!.setmovesVM(movies!!)
                replaceFragment(CurrentSuggestionFragment())
            }

            override fun onFailure(call: Call<APIData>, t: Throwable) {
                Log.e("Error",t.message.toString())
            }
        })
    }

    fun getGenres(){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/").build()
        val jsonapi = retrofit.create(GenreJsonAPI::class.java)
        val mcall: Call<genreAPIdata> = jsonapi.getGenres("921e7839ab441454d094115e5356728f",
            "en-US")
        mcall.enqueue(object: Callback<genreAPIdata> {
            override fun onResponse(call: Call<genreAPIdata>, response: Response<genreAPIdata>) {
                var genres: Array<Genre> = response.body()!!.genres!!
                viewModel!!.setGenres(genres)
            }

            override fun onFailure(call: Call<genreAPIdata>, t: Throwable) {
                Log.e("Error",t.message.toString())
            }
        })
    }

    fun replaceFragment(newFragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager?.beginTransaction()

        transaction.replace(R.id.fragmentContainerView, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}