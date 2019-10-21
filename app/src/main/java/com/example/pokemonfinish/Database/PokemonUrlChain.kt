package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonUrlChain (
    @SerializedName("url")
    var urlChain: String? = null

): RealmObject()