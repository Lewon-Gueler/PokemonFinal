package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoSpecies  (
    @SerializedName("evolves_to")
    var evoles: RealmList<PokemonEvoTo> = RealmList(),

    @SerializedName("species")
    var species: PokemonEvoSpecies2? = null

) : RealmObject()

