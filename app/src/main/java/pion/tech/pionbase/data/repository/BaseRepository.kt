package pion.tech.pionbase.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import pion.tech.pionbase.util.Result

abstract class BaseRepository {

    /**
     * Dùng cho các API call hoặc truy vấn DB trả về một kết quả duy nhất (One-shot)
     */
    protected fun <T> executeDataCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        dataCall: suspend () -> T
    ): Flow<Result<T>> = flow<Result<T>> {
        emit(Result.Success(dataCall()))
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(Result.Error(e))
    }.flowOn(dispatcher)

    /**
     * Dùng cho Flow (VD: Room, DataStore) KHI KHÔNG CẦN biến đổi dữ liệu
     */
    protected fun <T> Flow<T>.executeDataWithFlowCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<Result<T>> = this
        .map { Result.Success(it) as Result<T> }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(Result.Error(e))
        }
        .flowOn(dispatcher)

    /**
     * Dùng cho Flow (VD: Room, DataStore) KHI CÓ CẦN biến đổi dữ liệu (Mapping)
     */
    protected fun <T, R> Flow<T>.executeDataWithFlowCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        mapper: suspend (T) -> R
    ): Flow<Result<R>> = this
        .map { Result.Success(mapper(it)) as Result<R> }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(Result.Error(e))
        }
        .flowOn(dispatcher)
}
