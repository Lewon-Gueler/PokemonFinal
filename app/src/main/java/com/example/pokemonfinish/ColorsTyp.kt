package com.example.pokemonfinish

import com.google.gson.annotations.SerializedName

enum class ColorsTyp(val colorType: Int, val key: String) {

    @SerializedName("fire")
    FIRE(0xF08030, "fire"),

    @SerializedName("grass")
    GRASS(0xF08030,"grass"),

    @SerializedName("ice")
    ICE(0xF08030, "ice"),

    @SerializedName("poison")
    POISON(0xF08030, "poison"),

    @SerializedName("flying")
    FLYING(0xF08030,"flying"),

    @SerializedName("bug")
    BUG(0xF08030,"bug"),

    @SerializedName("ghost")
    GHOST(0xF08030, "ghost"),

    @SerializedName("dragon")
    DRAGON(0xF08030, "dragon"),

    @SerializedName("fairy")
    FAIRY(0xF08030,"fairy"),

    @SerializedName("normal")
    NORMAL(0xF08030,"normal"),

    @SerializedName("water")
    WATER(0xF08030, "water"),

    @SerializedName("electric")
    ELECTRIC(0xF08030,"electric"),

    @SerializedName("fighting")
    FIGHTIG(0xF08030, "fighting"),

    @SerializedName("ground")
    GROUND(0xF08030, "ground"),

    @SerializedName("psychic")
    PSYCHIC(0xF08030, "psychic"),

    @SerializedName("rock")
    ROCK(0xF08030, "rock"),

    @SerializedName("dark")
    DARK(0xF08030, "dark"),

    @SerializedName("steel")
    STEEL(0xF08030, "steel")
}