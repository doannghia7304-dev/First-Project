package pion.tech.pionbase.util

import com.piontech.core.base.BaseViewModel
import com.piontech.core.base.launchIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Extension functions to reduce boilerplate code for data handling (API and local operations)
 */

sealed interface UiState<out T> {
    data object None : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val exception: Throwable,
    ) : UiState<Nothing>
}

/**
 * Extension function to handle API calls with automatic state management
 */
inline fun <T, R> BaseViewModel.handleApiCall(
    stateFlow: MutableStateFlow<UiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline transform: (R) -> T,
    skipIfInProgress: Boolean = true,
) {
    launchIO {
        if (skipIfInProgress && (stateFlow.value is UiState.Loading || stateFlow.value is UiState.Success)) {
            return@launchIO
        }

        stateFlow.value = UiState.Loading
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    stateFlow.value = UiState.Success(transform(data))
                }.onError { exception ->
                    stateFlow.value = UiState.Error(exception)
                }
        }
    }
}

/**
 * Extension function for simple API calls without transformation
 */
inline fun <T> BaseViewModel.handleApiCall(
    stateFlow: MutableStateFlow<UiState<T>>,
    crossinline apiCall: suspend () -> Flow<Result<T>>,
    skipIfInProgress: Boolean = true,
) {
    handleApiCall(stateFlow, apiCall, { it }, skipIfInProgress)
}

/**
 * Extension function for API calls without needing to track UI state
 * Useful when you just need to process the result without updating UI state
 */
inline fun <R> BaseViewModel.handleApiCall(
    crossinline apiCall: suspend () -> Flow<Result<R>>,
    crossinline onSuccess: (R) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {},
) {
    launchIO {
        apiCall().collect { result ->
            result
                .onSuccess { data ->
                    onSuccess(data)
                }.onError { exception ->
                    onError(exception)
                }
        }
    }
}

/**
 * Extension function for handling local data operations (non-API)
 */
inline fun <T> BaseViewModel.handleLocalDataCall(
    stateFlow: MutableStateFlow<UiState<T>>,
    crossinline dataCall: suspend () -> T,
    skipIfInProgress: Boolean = true,
) {
    launchIO {
        if (skipIfInProgress && (stateFlow.value is UiState.Loading || stateFlow.value is UiState.Success)) {
            return@launchIO
        }

        stateFlow.value = UiState.Loading
        try {
            val data = dataCall()
            stateFlow.value = UiState.Success(data)
        } catch (e: Exception) {
            stateFlow.value = UiState.Error(e)
        }
    }
}

/**
 * Extension function to handle UI state changes with loading management
 */
inline fun <T> UiState<T>.handleUiState(
    crossinline onNone: () -> Unit = {},
    crossinline onLoading: () -> Unit = {},
    crossinline onSuccess: (T) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {},
) {
    when (this) {
        is UiState.None -> onNone()
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(exception)
    }
}
