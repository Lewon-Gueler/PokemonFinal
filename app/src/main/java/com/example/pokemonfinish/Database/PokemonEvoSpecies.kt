package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class PokemonEvoSpecies : RealmObject() {
    @SerializedName("evoles_to")
    var evoles: RealmList<PokemonEvoTo> = RealmList()
}