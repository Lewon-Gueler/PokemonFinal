package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class EvolutionDetails  (

    @SerializedName("min_level")
    var minLvl:Int?  = null


) : RealmObject()