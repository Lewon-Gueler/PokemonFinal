package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonTypes (

    @SerializedName("type")
    var type: PokemonTyp? = null,

    @SerializedName("slot")
    var slot: Int? = 0

) : RealmObject()