# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build project
./gradlew build

# Clean and build
./gradlew clean build

# Assemble debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "pion.tech.pionbase.YourTestClass"

# Check for lint errors
./gradlew lint
```

## Architecture Overview

Pion-Base follows Google's recommended app architecture with 2 layers (Domain Layer is skipped for simplicity):

### UI Layer (MVVM Pattern)
- **Fragment**: Defines UI structure and lifecycle (`init()`, `subscribeObserver()`)
- **FragmentEx**: Extension functions for separated logic (click events, view initialization)
- **ViewModel**: Manages UI state with `MutableStateFlow`/`StateFlow`

### Data Layer
- **Repository**: Returns `Flow<Result<T>>` for all data operations
- **DTO Models**: Data layer models (e.g., `LanguageDtoModel`)
- **UI Models**: UI layer models (e.g., `LanguageUIModel`)
- Mapping via `toPresentation()` extension functions

## Project Structure

```
app/src/main/java/pion/tech/pionbase/
├── app/              # Application, MainActivity, CommonViewModel
├── base/             # Base classes (MUST use these)
├── data/
│   ├── model/        # DTO and UI models
│   ├── remote/       # API interfaces
│   └── repository/   # Repository interfaces and implementations
├── di/               # Hilt dependency injection modules
├── feature/          # Feature packages (each contains Fragment + FragmentEx + ViewModel)
└── util/             # Extensions and utilities
```

## Required Base Classes

**Always extend from these base classes:**

| Instead of | Use |
|------------|-----|
| `Fragment` | `BaseFragment<Binding, VM>` |
| `ViewModel` | `BaseViewModel` |
| `ListAdapter` | `BaseListAdapter<Item, ViewBinding>` |
| `DialogFragment` | `BaseDialogFragment<T>` |
| `BottomSheetDialogFragment` | `BaseBottomSheetDialogFragment<T>` |

## Key Patterns

### Feature Structure
Each feature requires 3 files:
```
feature/{feature_name}/
├── {Feature}Fragment.kt      # UI definition
├── {Feature}FragmentEx.kt    # Extension functions for logic
└── {Feature}ViewModel.kt     # State management
```

### ViewModel State Pattern
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SomeRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: List<SomeUIModel> = emptyList(),
    val error: Throwable? = null,
)
```

### Fragment Observer Pattern
Use `map + distinctUntilChanged` to observe individual state properties:
```kotlin
viewModel.uiState
    .map { it.isLoading }
    .distinctUntilChanged()
    .collectFlowOnView(viewLifecycleOwner) { isLoading ->
        showHideLoading(isLoading)
    }
```

### Repository Pattern
```kotlin
// Interface
interface SomeRepository {
    fun getData(): Flow<Result<List<SomeDtoModel>>>
}

// Implementation - MUST use .catch and .flowOn(Dispatchers.IO)
class SomeRepositoryImpl @Inject constructor() : SomeRepository {
    override fun getData(): Flow<Result<List<SomeDtoModel>>> =
        flow<Result<List<SomeDtoModel>>> {
            emit(Result.Success(apiInterface.getData()))
        }.catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
}
```

### Coroutine Extensions
Use built-in extensions with exception handlers (NOT `viewModelScope.launch()` directly):
- `launchIO {}` - for network/database operations
- `launchDefault {}` - for CPU-intensive work
- `launchMain {}` - for UI updates

### Click Listener
Use `setPreventDoubleClick` instead of `setOnClickListener`:
```kotlin
binding.btnNext.setPreventDoubleClick { /* action */ }
```

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| ViewModel | `{Feature}ViewModel` | `HomeViewModel` |
| Fragment | `{Feature}Fragment` | `HomeFragment` |
| Fragment Extension | `{Feature}FragmentEx` | `HomeFragmentEx` |
| UI Model | `{Entity}UIModel` | `LanguageUIModel` |
| DTO Model | `{Entity}DtoModel` | `LanguageDtoModel` |
| Repository Interface | `{Feature}Repository` | `LanguageRepository` |
| Repository Impl | `{Feature}RepositoryImpl` | `LanguageRepositoryImpl` |

## Important Rules

1. **Fragment params**: Never pass params via constructor. Use `arguments` Bundle or shared ViewModel
2. **Adapter listeners**: Use `setListener()` method instead of constructor params
3. **Dependency Injection**: Use Hilt with `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`
4. **No testing required**: Skip writing tests unless explicitly requested
5. **Comments**: Keep short, clear, in English
