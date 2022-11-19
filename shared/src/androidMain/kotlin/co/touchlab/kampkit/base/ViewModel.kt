package co.touchlab.kampkit.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual abstract class ViewModel actual constructor() : ViewModel() {
    actual val viewModelScope: CoroutineScope = androidXViewModelScope

    actual override fun onCleared() {
        super.onCleared()
    }
}