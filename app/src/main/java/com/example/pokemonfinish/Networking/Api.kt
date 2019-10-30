package com.example.pokemonfinish.Networking

import com.example.pokemonfinish.Database.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {

    @GET
    suspend fun getPokeURL(@Url url: String): Response<Pokemon>

    @GET("pokemon")
    suspend fun getAllPokemonDatas(@Query("limit") limit: Int, @Query("offset") offset: Int): Response<PokemonList>

    @GET
    suspend fun getChain(@Url url: String): Response<Species>

    @GET
    suspend fun getEvos(@Url url: String): Response<EvolutionChain>

}