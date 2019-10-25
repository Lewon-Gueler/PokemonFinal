package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoSpecies3 (

    @SerializedName("name")
    var name: String = "",

    @SerializedName("url")
    var url:String = ""

) : RealmObject()