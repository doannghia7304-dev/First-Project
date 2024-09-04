package pion.tech.pionbase.framework.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

}

/**
 *
 * **sample:**
 * ```
 * class SampleViewModel : ViewModel() {
 *
 *     fun sample() {
 *         launchIO {
 *             // Logic
 *         }
 *         launchIO(exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
 *             // exception handling
 *         }) {
 *             // Logic
 *         }
 *     }
 * }
 * @return Job
 */

fun ViewModel.launchIO(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
    },
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(Dispatchers.IO + exceptionHandler, block = block)

fun ViewModel.launchDefault(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
    },
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(Dispatchers.Default + exceptionHandler, block = block)

fun ViewModel.launchMain(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${this::class.java.simpleName} error: $throwable")
    },
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(Dispatchers.Main + exceptionHandler, block = block)
