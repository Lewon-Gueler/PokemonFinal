package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class PokemonAbilities (

    @SerializedName("ability")
    var ability: PokemonAbility? = null,

    @SerializedName("slot")
    var slot: Int? = 0

) : RealmObject()