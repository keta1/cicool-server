package icu.ketal.data

import kotlinx.serialization.Serializable

@Serializable
data class Version(
    val version: String = "0.0.1",
    val msg: String = "Welcome to CiCool V $version!"
)
