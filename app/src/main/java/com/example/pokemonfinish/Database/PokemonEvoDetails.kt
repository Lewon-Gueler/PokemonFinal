package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonEvoDetails (

    @SerializedName("min_level")
    var minLvl: Int? = 0

) : RealmObject()
