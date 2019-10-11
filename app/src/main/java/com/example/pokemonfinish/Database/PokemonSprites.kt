package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonSprites (

    @SerializedName("back_shiny")
    var back_shiny:String? = null,

    @SerializedName("back_shiny_female")
    var back_shiny_female:String? = null,

    @SerializedName("front_shiny")
    var front_shiny:String? = null,

    @SerializedName("front_shiny_female")
    var front_shiny_female: String? = null

) : RealmObject()