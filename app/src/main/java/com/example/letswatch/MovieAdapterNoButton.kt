package com.example.letswatch

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letswatch.databinding.MovieRowBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MovieAdapterNoButton(private val movies: MutableList<MovieData>):
    RecyclerView.Adapter<MovieAdapterNoButton.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.movieTitleRowNB)
        val genres = itemView.findViewById<TextView>(R.id.movieGenresRowNB)
        val releaseDate = itemView.findViewById<TextView>(R.id.movieReleaseDateRowNB)
        val coverImage = itemView.findViewById<ImageView>(R.id.coverImageRowNB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_row_nobutton, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500" + (movies[position].backdrop_path))
            .into(holder.coverImage)
        holder.title.text = movies[position].title.toString()
        holder.genres.text = movies[position].genres.toString()
        holder.releaseDate.text = movies[position].releaseDate.toString()

        }


    override fun getItemCount(): Int {
        return movies.size
    }
}