package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class EvolutionChain (

    @PrimaryKey
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("chain")
    var chain: Chain? = null,

    @SerializedName("url")
    var url:String? = null


) : RealmObject() {

    val imageUri: String
        get() = "https://pokeres.bastionbot.org/images/pokemon/$id.png"
}

