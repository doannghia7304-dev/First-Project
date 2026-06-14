# Pion-Base - Data Layer Rules

Scope: Apply when working on Data-layer code (repositories, models, API/DB access, mappers).

## Repository

- Interface + Impl pattern in `data/repository/{featureName}Repository/`.
- All functions return `Flow<Result<T>>` (using custom `pion.tech.pionbase.util.Result`).
- Implementation uses `.flowOn(Dispatchers.IO)` and `.catch { emit(Result.Error(it)) }`.
- Repositories are consumed by **UseCases** (Domain layer), not by ViewModels directly. See `usecase.md`.

```kotlin
interface XxxRepository {
    fun getData(): Flow<Result<List<XxxDtoModel>>>
}
```

## Models

- **DtoModel** (`{Entity}DtoModel`): Data from API/DB, used in the Repository layer.
- **UIModel** (`{Entity}UIModel`): Transformed data for the UI layer.
- Map with extension function `fun XxxDtoModel.toPresentation(): XxxUIModel`.

| Type | Naming | Used in |
|------|--------|---------|
| DTO | `{Entity}DtoModel` | Repository / Data layer |
| UI | `{Entity}UIModel` | ViewModel / Fragment |

## API Call Handling

Use `handleApiCall` extensions from `ApiExtensions.kt`. In a ViewModel, `apiCall` invokes a UseCase (not a Repository):

```kotlin
// Manual state management (preferred for screen-level state):
handleApiCall(
    apiCall = { getDataUseCase() },
    onSuccess = { data -> setState { copy(items = data.map { it.toPresentation() }) } },
    onError = { throwable -> setState { copy(error = throwable) } },
)

// Automatic UiState<T> management:
handleApiCall(stateFlow, apiCall = { getDataUseCase() })
```
