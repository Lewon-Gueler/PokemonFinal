package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class Chain  (
    @SerializedName("evolves_to")
    var evolves: RealmList<Chain> = RealmList(),

    @SerializedName("species")
    var species: Species? = null,

    @SerializedName("evolution_details")
    var evoDetails: RealmList<EvolutionDetails> = RealmList()

) : RealmObject()

