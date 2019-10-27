package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class Type: RealmObject() {
    @SerializedName("name")
    var name: String? = null
}