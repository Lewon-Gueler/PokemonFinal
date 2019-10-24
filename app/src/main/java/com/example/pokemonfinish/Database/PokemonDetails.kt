package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonDetails (

    @SerializedName("min_level")
    var level: Int? = 0

) : RealmObject()