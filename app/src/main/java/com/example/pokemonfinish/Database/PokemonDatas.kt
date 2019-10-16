package com.example.pokemonfinish.Database

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PokemonDatas (
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
    var name: String = "ABRA",

    @SerializedName("types")
    var types: RealmList<PokemonTypes> = RealmList(),

    @SerializedName("moves")
    var moves: RealmList<PokemonMoves> = RealmList(),

    @SerializedName("stats")
    var stats: RealmList<PokemonStats> = RealmList(),

    @SerializedName("sprites")
    var sprites: PokemonSprites? = null,

    @SerializedName("abilities")
    var abilities: RealmList<PokemonAbilities> = RealmList()

): RealmObject() {

    val imageUri: String
    get() = "https://pokeres.bastionbot.org/images/pokemon/$id.png"

}