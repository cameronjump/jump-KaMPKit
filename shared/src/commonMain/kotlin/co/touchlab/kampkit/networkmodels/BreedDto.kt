package co.touchlab.kampkit.networkmodels

import kotlinx.serialization.Serializable

@Serializable
data class BreedDto(
    val message: Map<String, List<String>>,
    var status: String
)
