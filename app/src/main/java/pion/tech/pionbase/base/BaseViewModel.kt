package pion.tech.pionbase.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel()

fun ViewModel.launchIO(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e("${this::class.java.simpleName} error: $throwable")
            viewModelScope.launch(Dispatchers.Main) {
                onError(throwable)
            }
        }
    return viewModelScope.launch(Dispatchers.IO + exceptionHandler, block = block)
}

fun ViewModel.launchDefault(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e("${this::class.java.simpleName} error: $throwable")
            viewModelScope.launch(Dispatchers.Main) {
                onError(throwable)
            }
        }
    return viewModelScope.launch(Dispatchers.Default + exceptionHandler, block = block)
}

fun ViewModel.launchMain(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e("${this::class.java.simpleName} error: $throwable")
            viewModelScope.launch(Dispatchers.Main) {
                onError(throwable)
            }
        }
    return viewModelScope.launch(Dispatchers.Main + exceptionHandler, block = block)
}
