package icu.ketal.data

typealias OFMatrix = Map<String, MutableList<Float>>

val DEFAULT: OFMatrix
    get() = HashMap<String, MutableList<Float>>().apply {
        (13..28).map { it * 0.1 }
            .forEach {
                put("%.1f".format(it), MutableList(5) { 0f })
            }
    }
