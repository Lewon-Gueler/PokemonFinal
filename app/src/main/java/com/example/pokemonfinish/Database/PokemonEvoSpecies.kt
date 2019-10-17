package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PokemonEvoSpecies (
    @SerializedName("name")
    var name: String = "ABRA"
) : RealmObject()