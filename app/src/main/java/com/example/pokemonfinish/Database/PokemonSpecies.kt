package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class PokemonSpecies: RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    var id: Int? = 1

    @SerializedName("evolution_chain")
    var evoChainURL: RealmList<PokemonUrlChain > = RealmList()

}