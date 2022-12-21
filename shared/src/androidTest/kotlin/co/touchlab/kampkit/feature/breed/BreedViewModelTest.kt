package co.touchlab.kampkit.feature.breed

import co.touchlab.kampkit.db.Breed
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.mockito.Mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class BreedViewModelTest {

    @Mock
    lateinit var repository: BreedRepository

    private var kermit = Logger(StaticConfig())

    private val viewModel by lazy { BreedViewModel(repository, kermit) }

    companion object {
        private val appenzeller = Breed(1, "appenzeller", false)
        private val australianNoLike = Breed(2, "australian", false)
        private val australianLike = Breed(2, "australian", true)
        private val breedViewStateSuccessNoFavorite = BreedViewState(
            breeds = listOf(appenzeller, australianNoLike)
        )
        private val breedViewStateSuccessFavorite = BreedViewState(
            breeds = listOf(appenzeller, australianLike)
        )
        private val breedNames = breedViewStateSuccessNoFavorite.breeds?.map { it.name }.orEmpty()
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
