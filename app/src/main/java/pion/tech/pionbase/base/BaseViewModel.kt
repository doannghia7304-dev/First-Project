package pion.tech.pionbase.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel()

/**
 * Launch a coroutine with exception handling
 * @param dispatcher The dispatcher to use
 * @param onError Callback for error handling
 * @param block The coroutine block to execute
 */
private fun ViewModel.launchWithExceptionHandler(
    dispatcher: CoroutineDispatcher,
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
    return viewModelScope.launch(dispatcher + exceptionHandler, block = block)
}

fun ViewModel.launchIO(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.IO, onError, block)

fun ViewModel.launchDefault(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.Default, onError, block)

fun ViewModel.launchMain(
    onError: (Throwable) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
): Job = launchWithExceptionHandler(Dispatchers.Main, onError, block)
