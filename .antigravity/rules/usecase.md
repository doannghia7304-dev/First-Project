# Pion-Base - Domain Layer (UseCase) Rules

Scope: Apply when working on Domain-layer code (UseCases) or any ViewModel that consumes data. ViewModels MUST depend on UseCases, never on Repositories directly.

## Conventions

- **Location**: `domain/usecase/{featureName}/`.
- **Naming**: `{Action}{Entity}UseCase` (e.g. `GetInstalledAppsUseCase`, `GetLanguageUseCase`).
- **Single Responsibility**: each UseCase does exactly ONE operation. Expose a single `operator fun invoke(...)`. Do not bundle multiple unrelated operations into one UseCase.
- **Return type**: `Flow<Result<T>>` (same shape as Repository, so it stays compatible with `handleApiCall`).
- A UseCase receives one or more Repositories via constructor injection and must NOT reference the UI/ViewModel layer (no `BaseViewModel`, no Android UI types).

## Example

```kotlin
// domain/usecase/home/GetInstalledAppsUseCase.kt
class GetInstalledAppsUseCase(
    private val installedAppsRepository: InstalledAppsRepository,
) {
    operator fun invoke(): Flow<Result<List<InstalledAppDtoModel>>> =
        installedAppsRepository.getInstalledApps()
}
```

```kotlin
// ViewModel depends on the UseCase, not the Repository
class HomeViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {
    fun getInstalledApps() {
        handleApiCall(
            apiCall = { getInstalledAppsUseCase() },
            onSuccess = { dtoList -> setState { copy(installedApps = dtoList.map { it.toPresentation() }) } },
            onError = { throwable -> setState { copy(error = throwable) } },
        )
    }
}
```

## Dependency Injection

Register each UseCase in `useCaseModule` (in the `di/` package, added to `appModules` between `repositoryModule` and `viewModelModule`):

```kotlin
val useCaseModule = module {
    factoryOf(::GetInstalledAppsUseCase)
}
```
