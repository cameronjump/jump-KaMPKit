package co.touchlab.kampkit.base

import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
    val viewModelScope: CoroutineScope
    protected open fun onCleared()
}