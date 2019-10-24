package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoTo : RealmObject() {

    @SerializedName("evolves_to")
    var evoTo: RealmList<PokemonEvoDetails> = RealmList()

    @SerializedName("species")
    var species: PokemonEvoName? = null

}

