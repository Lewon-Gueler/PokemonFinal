package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonURL (
    @SerializedName("url")
    var url:String = ""

): RealmObject()