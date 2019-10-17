package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonStats (

    @SerializedName("base_stat")
    var baseStat: Int? = null,

    @SerializedName("stat")
    var stat: PokemonStat? = null


) : RealmObject()
