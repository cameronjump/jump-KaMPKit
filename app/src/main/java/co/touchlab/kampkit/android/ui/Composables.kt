package co.touchlab.kampkit.android.ui

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.feature.breed.BreedViewModel
import co.touchlab.kampkit.feature.breed.BreedViewState
import co.touchlab.kermit.Logger
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MainScreen(
    viewModel: BreedViewModel,
    log: Logger
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareDogsFlow = remember(viewModel.breedState, lifecycleOwner) {
        viewModel.breedState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val dogsState by lifecycleAwareDogsFlow.collectAsState(viewModel.breedState.value)

    MainScreenContent(
        dogsState = dogsState,
        onRefresh = { viewModel.refreshBreeds() },
        onFavorite = { viewModel.updateBreedFavorite(it) }
    )
}

@Composable
fun MainScreenContent(
    dogsState: BreedViewState,
    onRefresh: () -> Unit = {},
    onFavorite: (Breed) -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = dogsState.isLoading),
            onRefresh = onRefresh
        ) {
            if (dogsState.isEmpty) {
                Empty()
            }
            val breeds = dogsState.breeds
            if (breeds != null) {
                Success(successData = breeds, favoriteBreed = onFavorite)
            }
            if (dogsState.isError) {
                Error()
            }
        }
    }
}

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

@Composable
fun Error() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Something went wrong")
    }
}

@Composable
fun Success(
    successData: List<Breed>,
    favoriteBreed: (Breed) -> Unit
) {
    DogList(breeds = successData, favoriteBreed)
}

@Composable
fun DogList(breeds: List<Breed>, onItemClick: (Breed) -> Unit) {
    LazyColumn {
        items(breeds) { breed ->
            DogRow(breed) {
                onItemClick(it)
            }
            Divider()
        }
    }
}

@Composable
fun DogRow(breed: Breed, onClick: (Breed) -> Unit) {
    Row(
        Modifier
            .clickable { onClick(breed) }
            .padding(10.dp)
    ) {
        Text(breed.name, Modifier.weight(1F))
        FavoriteIcon(breed)
    }
}

@Composable
fun FavoriteIcon(breed: Breed) {
    Crossfade(
        targetState = !breed.favorite,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    ) { fav ->
        if (fav) {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_border_24px),
                contentDescription = stringResource(R.string.favorite_breed, breed.name),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_24px),
                contentDescription = stringResource(R.string.unfavorite_breed, breed.name),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
            )
        }
    }
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    MainScreenContent(
        dogsState = BreedViewState(
            breeds = listOf(
                Breed(0, "appenzeller", false),
                Breed(1, "australian", true)
            )
        )
    )
}
