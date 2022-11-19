package co.touchlab.kampkit.feature.breed

import app.cash.turbine.test
import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.base.ApiStatus
import co.touchlab.kampkit.base.StaleDataDelegate
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.ktor.Api
import co.touchlab.kampkit.testDbConnection
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.whenever
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BreedRepositoryTest {

    @Mock
    lateinit var staleDataDelegate: StaleDataDelegate

    @Mock
    lateinit var api: Api

    private val kermit = Logger(StaticConfig())

    private val testDbConnection = testDbConnection()
    private val dbHelper = DatabaseHelper(
        kermit,
        testDbConnection,
        Dispatchers.Default
    )

    private lateinit var ut: BreedRepository

    companion object {
        private val appenzeller = Breed(1, "appenzeller", false)
        private val australianNoLike = Breed(2, "australian", false)
        private val australianLike = Breed(2, "australian", true)
        private val breedsNoFavorite = listOf(appenzeller, australianNoLike)
        private val breedsNoFavoriteMap = mapOf(
            appenzeller.name to listOf<String>(),
            australianNoLike.name to listOf()
        )
        private val breedsFavorite = listOf(appenzeller, australianLike)
        private val breedNames = breedsFavorite.map { it.name }
    }

    @BeforeTest
    fun init() {
        openMocks(this)
        ut = BreedRepository(
            kermit,
            dbHelper,
            api,
            staleDataDelegate
        )
    }

    @AfterTest
    fun tearDown() = runTest {
        testDbConnection.close()
    }

    @Test
    fun `init with breeds`() = runTest {
        // given
        val expected = BreedRepoData(data = breedsNoFavorite)
        dbHelper.insertBreeds(breedNames)

        // when
        ut.getData().test {
            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `favorite`() = runTest {
        // given
        val expected = BreedRepoData(data = breedsFavorite)
        dbHelper.insertBreeds(breedNames)

        // when
        ut.getData().test {
            assertEquals(BreedRepoData(data = breedsNoFavorite), awaitItem())

            ut.updateBreedFavorite(australianNoLike)

            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `init with no breeds`() = runTest {
        // given
        val expected = BreedRepoData(
            data = emptyList()
        )

        // when
        ut.getData().test {
            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `error api result`() = runTest {
        // given
        val expected = BreedRepoData(isError = true)
        whenever(api.getBreeds()).thenReturn(
            ApiStatus.Error(404, "uh oh")
        )

        // when
        ut.getData().test {
            assertEquals(BreedRepoData(isLoading = false), awaitItem())

            ut.refreshData()

            assertEquals(BreedRepoData(isLoading = true), awaitItem())

            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `network api result no items`() = runTest {
        // given
        val expected = BreedRepoData(isError = true)
        whenever(api.getBreeds()).thenReturn(
            ApiStatus.NetworkError
        )

        // when
        ut.getData().test {
            assertEquals(BreedRepoData(isLoading = false), awaitItem())

            ut.refreshData()

            assertEquals(BreedRepoData(isLoading = true), awaitItem())

            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `network error api result has items`() = runTest {
        // given
        val expected = BreedRepoData(data = breedsNoFavorite)
        whenever(api.getBreeds()).thenReturn(
            ApiStatus.NetworkError
        )
        dbHelper.insertBreeds(breedNames)

        // when
        ut.getData().test {
            assertEquals(BreedRepoData(isLoading = false, data = breedsNoFavorite), awaitItem())

            ut.refreshData()

            assertEquals(BreedRepoData(isLoading = true, data = breedsNoFavorite), awaitItem())

            assertEquals(expected, awaitItem())
        }
    }

    // @Test
    // fun `success api result has items`() = runTest {
    //     // given
    //     whenever(api.getBreeds()).thenReturn(
    //         ApiStatus.Success(
    //             BreedDto(
    //                 message = breedsNoFavoriteMap,
    //                 status = "good"
    //             )
    //         )
    //     )
    //
    //     // when
    //     ut.getData().test {
    //         assertEquals(BreedRepoData(isLoading = false), awaitItem())
    //
    //         ut.refreshData()
    //
    //         assertEquals(BreedRepoData(isLoading = true), awaitItem())
    //     }
    // }
}
