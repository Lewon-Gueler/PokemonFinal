package com.example.pokemonfinish.Networking

import com.example.pokemonfinish.Database.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {

    @GET
    fun getPokeURL(@Url url: String): Call<Pokemon>

    @GET("pokemon")
    fun getAllPokemonDatas(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<PokemonList>

    @GET
    fun getChain(@Url url: String): Call<Species>

    @GET
    fun getEvos(@Url url: String): Call<EvolutionChain>

}