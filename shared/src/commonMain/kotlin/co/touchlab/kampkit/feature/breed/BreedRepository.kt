package co.touchlab.kampkit.feature.breed

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.base.ApiStatus
import co.touchlab.kampkit.base.StaleDataDelegate
import co.touchlab.kampkit.base.StaleDataKey
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.ktor.Api
import co.touchlab.kampkit.networkmodels.BreedDto
import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class BreedRepository(
    private val log: Logger,
    private val dbHelper: DatabaseHelper,
    private val api: Api,
    private val staleDataDelegate: StaleDataDelegate
) {

    init {
        ensureNeverFrozen()
    }

    private val requestStatus = MutableStateFlow<ApiStatus<BreedDto>>(ApiStatus.NoAction)

    fun getData(): Flow<BreedRepoData> =
        combine(dbHelper.selectAllItems(), requestStatus.asStateFlow()) { items, requestStatus ->
            when (requestStatus) {
                is ApiStatus.Error -> {
                    BreedRepoData(isError = true)
                }
                ApiStatus.NoAction,
                is ApiStatus.Success -> {
                    BreedRepoData(
                        data = items.sortedBy { it.name }
                    )
                }
                ApiStatus.NetworkError -> {
                    if (items.isNotEmpty()) {
                        BreedRepoData(
                            data = items.sortedBy { it.name }
                        )
                    } else {
                        BreedRepoData(isError = true)
                    }
                }
                ApiStatus.Loading -> BreedRepoData(
                    isLoading = true,
                    data = items.sortedBy { it.name }
                )
            }
        }

    suspend fun refreshDataIfStale() {
        log.d { "Checking if data is stale." }
        if (staleDataDelegate.isDataStale(StaleDataKey.BreedStaleData)) {
            refreshData()
        }
    }

    suspend fun refreshData() {
        requestStatus.tryEmit(ApiStatus.Loading)

        log.d { "Refreshing data." }

        val result = api.getBreeds()
        staleDataDelegate.updateLastTime(StaleDataKey.BreedStaleData)

        val data = result as? ApiStatus.Success
        data?.let {
            persistData(it.data)
        }

        log.d { "Network result: $result" }

        requestStatus.tryEmit(result)
    }

    private suspend fun persistData(data: BreedDto) {
        dbHelper.insertBreeds(data.message.keys.toList())
    }

    suspend fun updateBreedFavorite(breed: Breed) {
        dbHelper.updateFavorite(breed.id, !breed.favorite)
    }
}
