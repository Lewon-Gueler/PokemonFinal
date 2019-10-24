package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoDetails (

    @SerializedName("evolution_details")
    var evoDetail: RealmList<PokemonDetails> = RealmList(),

    @SerializedName("species")
    var species3: PokemonEvoSpecies3? = null


) : RealmObject()
