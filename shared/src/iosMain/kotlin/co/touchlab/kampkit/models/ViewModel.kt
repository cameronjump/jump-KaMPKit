package co.touchlab.kampkit.models

import co.touchlab.kampkit.FlowAdapter
import co.touchlab.kampkit.base.ViewModel
import kotlinx.coroutines.flow.Flow

abstract class CallbackViewModel {
    protected abstract val viewModel: ViewModel

    /**
     * Create a [FlowAdapter] from this [Flow] to make it easier to interact with from Swift.
     */
    fun <T : Any> Flow<T>.asCallbacks() =
        FlowAdapter(viewModel.viewModelScope, this)

    @Suppress("Unused") // Called from Swift
    fun clear() = viewModel.clear()
}
