package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class ExtendedTrigger  (

    @SerializedName("name")
    var name:String? = null,

    @SerializedName("url")
    var url:String? = null



) : RealmObject()