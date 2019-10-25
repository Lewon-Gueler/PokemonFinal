package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoTo  (
    @SerializedName("evolves_to")
    var evoTo: RealmList<PokemonEvoDetails> = RealmList(),

    @SerializedName("species")
    var species: PokemonEvoName? = null,

    @SerializedName("evolution_details")
    var evoDetail1: RealmList<PokemonDetailEvo> = RealmList()

) : RealmObject()





