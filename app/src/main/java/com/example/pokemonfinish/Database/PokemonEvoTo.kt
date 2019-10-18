package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonEvoTo (
    @SerializedName("evoles_to")
    var evolesSS: PokemonEvoDetails? = null,

    @SerializedName("species")
    var species: PokemonEvoName? = null

    ) : RealmObject()
