package com.example.splashpokemon.network

import com.example.splashpokemon.data.pokemonDetail
import com.example.splashpokemon.data.pokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface apiService {
    @GET("pokemon?limit=1000")
    suspend fun getPokemonList(): pokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail( @Path ("id") id: Int): pokemonDetail
}