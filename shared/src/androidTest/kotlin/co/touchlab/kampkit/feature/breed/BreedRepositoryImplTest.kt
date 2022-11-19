package co.touchlab.kampkit.feature.breed

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.base.StaleDataDelegate
import co.touchlab.kampkit.base.StaleDataDelegateImpl
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.ktor.Api
import co.touchlab.kampkit.ktor.ApiImpl
import co.touchlab.kampkit.testDbConnection
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class BreedRepositoryImplTest {

    @Mock
    lateinit var staleDataDelegate: StaleDataDelegateImpl

    @Mock
    lateinit var api: ApiImpl

    private val kermit = Logger(StaticConfig())

    private val testDbConnection = testDbConnection()
    private val dbHelper = DatabaseHelper(
        kermit,
        testDbConnection,
        Dispatchers.Default
    )

    private lateinit var ut: BreedRepositoryImpl

    companion object {
        private val appenzeller = Breed(1, "appenzeller", false)
        private val australianNoLike = Breed(2, "australian", false)
        private val australianLike = Breed(2, "australian", true)
        private val breedsNoFavorite = listOf(appenzeller, australianNoLike)
        private val breedsFavorite = listOf(appenzeller, australianLike)
        private val breedNames = breedsFavorite.map { it.name }
    }

    @BeforeTest
    fun init() {
        openMocks(this)
        ut = BreedRepositoryImpl(
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
    fun `Toggle favorite cached breed`() = runTest {
        dbHelper.insertBreeds(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)
        //
        // repository.getData().test {
        //     assertEquals(breedsFavorite, awaitItem())
        //     expectNoEvents()
        //
        //     repository.updateBreedFavorite(australianLike)
        //     assertEquals(breedsNoFavorite, awaitItem())
        // }
    }
}
