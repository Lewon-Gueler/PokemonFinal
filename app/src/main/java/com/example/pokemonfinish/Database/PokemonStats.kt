package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonStats (

    @SerializedName("stats")
    var stats: PokemonStat? = null

) : RealmObject()
