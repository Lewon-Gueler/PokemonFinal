package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Pokemon (
    @PrimaryKey
    @SerializedName("id")
    var id: Int? = 0,

    @SerializedName("height")
    var height: Int? = 0,

    @SerializedName("weight")
    var weight: Int? = 0,

    @SerializedName("url")
    var url: String? = null,

    @SerializedName("order")
    var order: Int? = 0,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("types")
    var types: RealmList<ExtendedType>? = RealmList(),

    @SerializedName("moves")
    var moves: RealmList<ExtendedMove>? = RealmList(),

    @SerializedName("stats")
    var stats: RealmList<ExtendedStat>? = RealmList(),

    @SerializedName("sprites")
    var sprites: Sprites? = null,

    @SerializedName("abilities")
    var abilities: RealmList<ExtendedAbility>? = RealmList(),

    @SerializedName("species")
    var species: Species?  = null


): RealmObject() {

    val imageUri: String
    get() = "https://pokeres.bastionbot.org/images/pokemon/$id.png"

}