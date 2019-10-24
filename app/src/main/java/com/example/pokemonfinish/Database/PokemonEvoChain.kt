package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class PokemonEvoChain (

    @PrimaryKey
    @SerializedName("id")
    var id: Int? = 1,

    @SerializedName("chain")
    var chain: PokemonEvoSpecies? = null

) : RealmObject() {

    val imageUri: String
        get() = "https://pokeres.bastionbot.org/images/pokemon/$id.png"
}

