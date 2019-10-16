package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonMoves (

    @SerializedName("move")
    var move: PokemonMove? = null

) : RealmObject()
