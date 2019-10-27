package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class ExtendedAbility (

    @SerializedName("ability")
    var ability: Ability? = null,

    @SerializedName("slot")
    var slot: Int? = 0

) : RealmObject()