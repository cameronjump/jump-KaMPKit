package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.base.ApiStatus
import co.touchlab.kampkit.networkmodels.BreedDto

/**
 * Interface to represent all network calls
 */
interface Api {

    suspend fun getBreeds(): ApiStatus<BreedDto>

}
