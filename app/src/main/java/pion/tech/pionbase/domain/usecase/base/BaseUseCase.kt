package com.nguyennhatminh614.composeheavilyapp.usecase.base

import com.nguyennhatminh614.composeheavilyapp.domain.model.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

data class Tuple4<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val fourth: D)
data class Tuple5<out A, out B, out C, out D, out E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

interface BaseUseCase {
    abstract class OneShot<in In, out Out> {
        protected abstract fun execute(params: In): Result<Out>

        operator fun invoke(params: In): Result<Out> {
            return try {
                execute(params)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    abstract class OneShotSuspend<in In, out Out>(
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        protected abstract suspend fun execute(params: In): Result<Out>

        suspend operator fun invoke(params: In): Result<Out> = withContext(dispatcher) {
            try {
                execute(params)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    abstract class Flow<in In, out Out>(
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        protected abstract fun execute(params: In): kotlinx.coroutines.flow.Flow<Result<Out>>

        operator fun invoke(params: In): kotlinx.coroutines.flow.Flow<Result<Out>> {
            return execute(params)
                .catch { e ->
                    if (e is CancellationException) throw e
                    if (e is Exception) emit(Result.Error(e)) else throw e
                }
                .flowOn(dispatcher)
        }
    }

    // --- 0 Param ---
    abstract class OneShotNoParam<out Out> : OneShot<Unit, Out>() {
        protected abstract fun execute(): Result<Out>
        override fun execute(params: Unit): Result<Out> = execute()
        operator fun invoke(): Result<Out> = super.invoke(Unit)
    }

    abstract class OneShotSuspendNoParam<out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OneShotSuspend<Unit, Out>(dispatcher) {
        protected abstract suspend fun execute(): Result<Out>
        override suspend fun execute(params: Unit): Result<Out> = execute()
        suspend operator fun invoke(): Result<Out> = super.invoke(Unit)
    }

    abstract class FlowNoParam<out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : Flow<Unit, Out>(dispatcher) {
        protected abstract fun execute(): kotlinx.coroutines.flow.Flow<Result<Out>>
        override fun execute(params: Unit): kotlinx.coroutines.flow.Flow<Result<Out>> = execute()
        operator fun invoke(): kotlinx.coroutines.flow.Flow<Result<Out>> = super.invoke(Unit)
    }

    // --- 2 Params ---
    abstract class OneShot2Params<in P1, in P2, out Out> : OneShot<Pair<P1, P2>, Out>() {
        protected abstract fun execute(param1: P1, param2: P2): Result<Out>
        override fun execute(params: Pair<P1, P2>): Result<Out> = execute(params.first, params.second)
        operator fun invoke(param1: P1, param2: P2): Result<Out> = super.invoke(param1 to param2)
    }

    abstract class OneShotSuspend2Params<in P1, in P2, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OneShotSuspend<Pair<P1, P2>, Out>(dispatcher) {
        protected abstract suspend fun execute(param1: P1, param2: P2): Result<Out>
        override suspend fun execute(params: Pair<P1, P2>): Result<Out> = execute(params.first, params.second)
        suspend operator fun invoke(param1: P1, param2: P2): Result<Out> = super.invoke(param1 to param2)
    }

    abstract class Flow2Params<in P1, in P2, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : Flow<Pair<P1, P2>, Out>(dispatcher) {
        protected abstract fun execute(param1: P1, param2: P2): kotlinx.coroutines.flow.Flow<Result<Out>>
        override fun execute(params: Pair<P1, P2>): kotlinx.coroutines.flow.Flow<Result<Out>> = execute(params.first, params.second)
        operator fun invoke(param1: P1, param2: P2): kotlinx.coroutines.flow.Flow<Result<Out>> = super.invoke(param1 to param2)
    }

    // --- 3 Params ---
    abstract class OneShot3Params<in P1, in P2, in P3, out Out> : OneShot<Triple<P1, P2, P3>, Out>() {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3): Result<Out>
        override fun execute(params: Triple<P1, P2, P3>): Result<Out> = execute(params.first, params.second, params.third)
        operator fun invoke(param1: P1, param2: P2, param3: P3): Result<Out> = super.invoke(Triple(param1, param2, param3))
    }

    abstract class OneShotSuspend3Params<in P1, in P2, in P3, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OneShotSuspend<Triple<P1, P2, P3>, Out>(dispatcher) {
        protected abstract suspend fun execute(param1: P1, param2: P2, param3: P3): Result<Out>
        override suspend fun execute(params: Triple<P1, P2, P3>): Result<Out> = execute(params.first, params.second, params.third)
        suspend operator fun invoke(param1: P1, param2: P2, param3: P3): Result<Out> = super.invoke(Triple(param1, param2, param3))
    }

    abstract class Flow3Params<in P1, in P2, in P3, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : Flow<Triple<P1, P2, P3>, Out>(dispatcher) {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3): kotlinx.coroutines.flow.Flow<Result<Out>>
        override fun execute(params: Triple<P1, P2, P3>): kotlinx.coroutines.flow.Flow<Result<Out>> = execute(params.first, params.second, params.third)
        operator fun invoke(param1: P1, param2: P2, param3: P3): kotlinx.coroutines.flow.Flow<Result<Out>> = super.invoke(Triple(param1, param2, param3))
    }

    // --- 4 Params ---
    abstract class OneShot4Params<in P1, in P2, in P3, in P4, out Out> : OneShot<Tuple4<P1, P2, P3, P4>, Out>() {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3, param4: P4): Result<Out>
        override fun execute(params: Tuple4<P1, P2, P3, P4>): Result<Out> = execute(params.first, params.second, params.third, params.fourth)
        operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4): Result<Out> = super.invoke(Tuple4(param1, param2, param3, param4))
    }

    abstract class OneShotSuspend4Params<in P1, in P2, in P3, in P4, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OneShotSuspend<Tuple4<P1, P2, P3, P4>, Out>(dispatcher) {
        protected abstract suspend fun execute(param1: P1, param2: P2, param3: P3, param4: P4): Result<Out>
        override suspend fun execute(params: Tuple4<P1, P2, P3, P4>): Result<Out> = execute(params.first, params.second, params.third, params.fourth)
        suspend operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4): Result<Out> = super.invoke(Tuple4(param1, param2, param3, param4))
    }

    abstract class Flow4Params<in P1, in P2, in P3, in P4, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : Flow<Tuple4<P1, P2, P3, P4>, Out>(dispatcher) {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3, param4: P4): kotlinx.coroutines.flow.Flow<Result<Out>>
        override fun execute(params: Tuple4<P1, P2, P3, P4>): kotlinx.coroutines.flow.Flow<Result<Out>> = execute(params.first, params.second, params.third, params.fourth)
        operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4): kotlinx.coroutines.flow.Flow<Result<Out>> = super.invoke(Tuple4(param1, param2, param3, param4))
    }

    // --- 5 Params ---
    abstract class OneShot5Params<in P1, in P2, in P3, in P4, in P5, out Out> : OneShot<Tuple5<P1, P2, P3, P4, P5>, Out>() {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): Result<Out>
        override fun execute(params: Tuple5<P1, P2, P3, P4, P5>): Result<Out> = execute(params.first, params.second, params.third, params.fourth, params.fifth)
        operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): Result<Out> = super.invoke(Tuple5(param1, param2, param3, param4, param5))
    }

    abstract class OneShotSuspend5Params<in P1, in P2, in P3, in P4, in P5, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : OneShotSuspend<Tuple5<P1, P2, P3, P4, P5>, Out>(dispatcher) {
        protected abstract suspend fun execute(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): Result<Out>
        override suspend fun execute(params: Tuple5<P1, P2, P3, P4, P5>): Result<Out> = execute(params.first, params.second, params.third, params.fourth, params.fifth)
        suspend operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): Result<Out> = super.invoke(Tuple5(param1, param2, param3, param4, param5))
    }

    abstract class Flow5Params<in P1, in P2, in P3, in P4, in P5, out Out>(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : Flow<Tuple5<P1, P2, P3, P4, P5>, Out>(dispatcher) {
        protected abstract fun execute(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): kotlinx.coroutines.flow.Flow<Result<Out>>
        override fun execute(params: Tuple5<P1, P2, P3, P4, P5>): kotlinx.coroutines.flow.Flow<Result<Out>> = execute(params.first, params.second, params.third, params.fourth, params.fifth)
        operator fun invoke(param1: P1, param2: P2, param3: P3, param4: P4, param5: P5): kotlinx.coroutines.flow.Flow<Result<Out>> = super.invoke(Tuple5(param1, param2, param3, param4, param5))
    }
}
