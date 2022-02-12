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

class MovieAdapter(private val movies: MutableList<MovieData>):
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.movieTitleRow)
        val genres = itemView.findViewById<TextView>(R.id.movieGenresRow)
        val releaseDate = itemView.findViewById<TextView>(R.id.movieReleaseDateRow)
        val coverImage = itemView.findViewById<ImageView>(R.id.coverImageRow)
        val watchedButton = itemView.findViewById<Button>(R.id.watchedTogetherButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500" + (movies[position].backdrop_path))
            .into(holder.coverImage)
        holder.title.text = movies[position].title.toString()
        holder.genres.text = movies[position].genres.toString()
        holder.releaseDate.text = movies[position].releaseDate.toString()
        holder.watchedButton.setOnClickListener{
            var removedItem = movies[position]
            movies.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            val user = Firebase.auth
            val db = Firebase.firestore
            db.collection("users").document(user.uid.toString()).get().addOnSuccessListener{
                result ->
                val currentUser: User = result.toObject(User::class.java)!!
                db.collection("connections").document(currentUser.connectionId.toString())
                    .get().addOnSuccessListener{
                    result1 ->
                        var connection: Connection = result1.toObject(Connection::class.java)!!
                        var newData: MutableList<String>? = connection.watchedTogetherIds
                        newData!!.add(removedItem.id.toString())
                        db.collection("connections").document(currentUser.connectionId.toString())
                            .update("watchedTogetherIds", newData)
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return movies.size
    }
}