package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class EvolutionDetails  (

    @SerializedName("min_level")
    var minLvl:Int?  = null,

    @SerializedName("item")
    var item :ExtendedTrigger?  = null,

    @SerializedName("trigger")
    var trigger :ExtendedTrigger?  = null

) : RealmObject()