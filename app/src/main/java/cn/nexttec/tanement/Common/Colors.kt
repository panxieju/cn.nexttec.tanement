package cn.nexttec.tanement.Common

import android.graphics.Color
import java.util.*


val colors = listOf<String>(
        "#f68922",
        "#ffbd11",
        "#fcf103"
        )

fun getRandomColor(): Int {
    val index = Random().nextInt(100) % 3
    return Color.parseColor(colors[index])
}

fun getPositionColor(position:Int): Int {
    return Color.parseColor(colors[position%3])
}



