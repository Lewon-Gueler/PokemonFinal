package com.example.pokemonfinish.Networking

import com.example.pokemonfinish.Database.PokemonDatas
import com.example.pokemonfinish.Database.PokemonList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {

    @GET
    fun getPokeURL(@Url url: String): Call<PokemonDatas>

    @GET("pokemon")
    fun getAllPokemonDatas(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<PokemonList>

}