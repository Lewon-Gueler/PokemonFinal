package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class PokemonEvoChain: RealmObject() {
    @PrimaryKey
    @SerializedName("id")
    var id: Int? = 0

    @SerializedName("pokemon_species")
    var pokemonSpecies: RealmList<PokemonEvoSpecies> = RealmList()
}