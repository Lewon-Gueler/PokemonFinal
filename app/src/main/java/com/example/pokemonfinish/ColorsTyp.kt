package com.example.pokemonfinish

import android.graphics.ColorSpace
import com.google.gson.annotations.SerializedName

enum class ColorsTyp(val r: Int, val g: Int, val b: Int) {

    @SerializedName("fire")
    FIRE(240, 128, 48),

    @SerializedName("poison")
    POISON(160, 64, 160),

    @SerializedName("grass")
    GRASS(122,199,76);

    fun rgb() = r shl 16 + g shl 8 + b

   /*

    @SerializedName("grass")
    GRASS(0xF08030,"grass"),

    @SerializedName("ice")
    ICE(0xF08030, "ice"),


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

     case .normal:
                return UIColor.kio.color(red: 168, green: 168, blue: 120)

            case .fire:
                return UIColor.kio.color(red: 240, green: 128, blue: 48)

            case .fighting:
                return UIColor.kio.color(red: 192, green: 48, blue: 40)

            case .water:
                return UIColor.kio.color(red: 104, green: 144, blue: 240)

            case .flying:
                return UIColor.kio.color(red: 168, green: 144, blue: 240)

            case .grass:
                return UIColor.kio.color(red: 122, green: 199, blue: 76)

            case .poison:
                return UIColor.kio.color(red: 160, green: 64, blue: 160)

            case .electric:
                return UIColor.kio.color(red: 248, green: 208, blue: 48)

            case .ground:
                return UIColor.kio.color(red: 224, green: 192, blue: 104)

            case .psychic:
                return UIColor.kio.color(red: 248, green: 88, blue: 136)

            case .rock:
                return UIColor.kio.color(red: 184, green: 160, blue: 56)

            case .ice:
                return UIColor.kio.color(red: 152, green: 216, blue: 216)

            case .bug:
                return UIColor.kio.color(red: 168, green: 184, blue: 32)

            case .dragon:
                return UIColor.kio.color(red: 112, green: 56, blue: 248)

            case .ghost:
                return UIColor.kio.color(red: 112, green: 88, blue: 152)

            case .dark:
                return UIColor.kio.color(red: 112, green: 88, blue: 72)

            case .steel:
                return UIColor.kio.color(red: 184, green: 184, blue: 208)

            case .fairy:
                return UIColor.kio.color(red: 238, green: 153, blue: 172)
 */

}