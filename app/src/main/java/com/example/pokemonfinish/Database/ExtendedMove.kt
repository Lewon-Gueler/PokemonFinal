package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class ExtendedMove (

    @SerializedName("move")
    var move: Move? = null

) : RealmObject()
