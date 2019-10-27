package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Species (
    @PrimaryKey
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name:String? = null,

    @SerializedName("url")
    var url:String? = null,

    @SerializedName("evolution_chain")
    var evoChain: EvolutionChain? = null,

    @SerializedName("evolves_from_species")
    var evolvesfrom:Species? = null


): RealmObject() {
    val imageUri: String
        get() = "https://pokeres.bastionbot.org/images/pokemon/$id.png"
}