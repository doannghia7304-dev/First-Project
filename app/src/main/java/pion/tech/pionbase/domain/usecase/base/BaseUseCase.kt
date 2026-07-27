package pion.tech.pionbase.domain.usecase.base

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pion.tech.pionbase.util.Result

abstract class BaseUseCase {

    /**
     * Dùng cho các UseCase chạy một lần (One-shot) và có suspend.
     * Khối [action] mặc định phải trả về Result<T>.
     */
    protected suspend fun <T> executeSuspend(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        action: suspend () -> Result<T>
    ): Result<T> = withContext(dispatcher) {
        try {
            action()
        } catch (e: CancellationException) {
            throw e // Bắt buộc throw lại để Coroutine hoạt động đúng
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Dùng cho các UseCase trả về Flow.
     */
    protected fun <T> executeFlow(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        action: () -> Flow<Result<T>>
    ): Flow<Result<T>> {
        return action()
            .catch { e ->
                if (e is CancellationException) throw e
                if (e is Exception) emit(Result.Error(e)) else throw e
            }
            .flowOn(dispatcher)
    }

    /**
     * Dùng cho các UseCase đồng bộ (Sync - không có suspend).
     */
    protected fun <T> executeSync(
        action: () -> Result<T>
    ): Result<T> {
        return try {
            action()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}