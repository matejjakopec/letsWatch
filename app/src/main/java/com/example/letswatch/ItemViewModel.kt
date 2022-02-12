package com.example.letswatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    private var moviesVM: MutableLiveData<Array<Movie>> = MutableLiveData()
    private var user: MutableLiveData<User> = MutableLiveData()
    private var connection: MutableLiveData<Connection> = MutableLiveData()
    private var genres: MutableLiveData<Array<Genre>> = MutableLiveData()


    fun setmovesVM(movie: Array<Movie>){
        moviesVM.setValue(movie)
    }

    fun setUser(userR: User){
        user.setValue(userR)
    }

    fun setConnection(connectionR: Connection){
        connection.setValue(connectionR)
    }

    fun setGenres(genresR: Array<Genre>){
        genres.setValue(genresR)
    }

    fun getMoviesVM(): LiveData<Array<Movie>> {
        return moviesVM
    }

    fun getUser(): LiveData<User> {
        return user
    }

    fun getConnection(): LiveData<Connection> {
        return connection
    }
    fun getGenres(): LiveData<Array<Genre>> {
        return genres
    }
}