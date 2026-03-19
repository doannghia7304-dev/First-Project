# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single test class
./gradlew testDebugUnitTest --tests "pion.tech.pionbase.ExampleUnitTest"

# Clean build
./gradlew clean assembleDebug

# Check lint
./gradlew lint
```

## Architecture Overview

**Pion-Base** is an Android template project using a 2-layer architecture (UI Layer + Data Layer, no Domain Layer) with MVVM pattern. Built with Kotlin, ViewBinding/DataBinding, Koin DI, and Navigation Component (single-Activity).

### Modules

- **`:app`** - Main application module (`pion.tech.pionbase`)
- **`:LibAds`** - Ads management library module

### Key Tech Stack

| Component | Technology |
|-----------|-----------|
| DI | Koin (NOT Hilt/Dagger) |
| Navigation | Jetpack Navigation Component (single `nav_main.xml`) |
| Network | Retrofit2 + OkHttp3 + Gson |
| Database | Room |
| Preferences | DataStore |
| Image Loading | Glide |
| Logging | Timber |
| Analytics | Firebase Analytics + Crashlytics + Remote Config |
| Coroutines | Kotlin Coroutines + Flow + StateFlow |
| Min SDK | 24, Target/Compile SDK 35 |
| JVM | Java 17 |

## Base Classes (MANDATORY)

Never use raw Android classes. Always extend from these base classes:

| Instead of | Use |
|-----------|-----|
| `Fragment` | `BaseFragment<Binding, VM>(inflate, vmClass)` |
| `ViewModel` | `BaseViewModel<State, Event>(initialState)` |
| `ListAdapter` | `BaseListAdapter<Item, ViewBinding>(diffCallback)` |
| `DialogFragment` | `BaseDialogFragment<T>(layoutRes)` |
| `BottomSheetDialogFragment` | `BaseBottomSheetDialogFragment<T>(layoutRes)` |

### BaseFragment

- Generic params: `<ViewBinding, ViewModel>`
- Constructor params: `inflate` function reference and ViewModel `KClass`
- Override `init(view: View)` for setup and `subscribeObserver(view: View)` for Flow collection
- Provides `binding`, `viewModel`, `commonViewModel`, `navigator`, `dataStoreRepository`, `logger`
- Built-in `showHideLoading(Boolean)` and `onSystemBack { }`

### BaseViewModel

- Generic params: `<State, Event>` with `initialState`
- Built-in `_uiState`/`uiState` (StateFlow) and `_uiEvent`/`uiEvent` (Channel)
- Use `setState { copy(...) }` to update state, `setEvent(event)` to emit one-shot events
- Each screen defines its own `data class XxxUiState(...)` passed as `initialState`

## Feature Structure Pattern

Each feature follows a strict 3-file pattern:

```
feature/{featureName}/
├── {Feature}Fragment.kt      # UI + observer subscriptions
├── {Feature}FragmentEx.kt    # Extension functions for click events, view init
├── {Feature}ViewModel.kt     # State management + business logic
├── adapter/                  # RecyclerView adapters
├── dialog/                   # DialogFragments
└── bottomSheet/              # BottomSheetDialogFragments
```

### Fragment Pattern

```kotlin
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class,
) {
    override fun init(view: View) {
        initView()        // from FragmentEx
        settingEvent()    // from FragmentEx
    }
    override fun subscribeObserver(view: View) {
        viewModel.uiState
            .map { it.someField }
            .distinctUntilChanged()
            .collectFlowOnView(viewLifecycleOwner) { value -> /* update UI */ }
    }
}
```

### FragmentEx Pattern

Extension functions on the Fragment class, separating UI logic:

```kotlin
fun HomeFragment.initView() {
    adapter.setListener(this)
    binding.rvMain.adapter = adapter
}
fun HomeFragment.settingEvent() {
    binding.btnSetting.setPreventDoubleClickScaleView { navigator.navigateTo(R.id.action_...) }
}
```

## Coroutine Extensions

Use the built-in coroutine launchers (they include exception handling):

```kotlin
// In Fragment (from BaseFragment.kt extensions):
launchIO { }       // IO dispatcher
launchMain { }     // Main dispatcher
launchDefault { }  // Default dispatcher

// In ViewModel (from BaseViewModel.kt extensions):
launchIO { }
launchMain { }
launchDefault { }
```

**Do NOT use** `viewModelScope.launch()` or `lifecycleScope.launch()` directly.

## State Observation

Always use `collectFlowOnView` with `map + distinctUntilChanged` to observe specific state fields:

```kotlin
viewModel.uiState
    .map { it.fieldName }
    .distinctUntilChanged()
    .collectFlowOnView(viewLifecycleOwner) { value -> /* handle */ }
```

## Data Layer Patterns

### Repository

- Interface + Impl pattern in `data/repository/{featureName}Repository/`
- All functions return `Flow<Result<T>>` (using custom `pion.tech.pionbase.util.Result`)
- Implementation uses `.flowOn(Dispatchers.IO)` and `.catch { emit(Result.Error(it)) }`

### Models

- **DtoModel** (`{Entity}DtoModel`): Data from API/DB, used in Repository layer
- **UIModel** (`{Entity}UIModel`): Transformed data for UI layer
- Extension function `fun XxxDtoModel.toPresentation(): XxxUIModel` for mapping

### API Call Handling

Use `handleApiCall` extensions from `ApiExtensions.kt`:

```kotlin
// With manual state management (preferred for screen-level state):
handleApiCall(
    apiCall = { repository.getData() },
    onSuccess = { data -> setState { copy(items = data.map { it.toPresentation() }) } },
    onError = { throwable -> setState { copy(error = throwable) } },
)

// With automatic UiState<T> management:
handleApiCall(stateFlow, apiCall = { repository.getData() })
```

## Dependency Injection (Koin)

Modules defined in `di/` package, aggregated in `AppModule.kt`:

- `coreModule` - Firebase RemoteConfig, DataStore
- `networkModule` - Gson, OkHttp, Retrofit, ApiInterface
- `databaseModule` - Room database + DAOs
- `repositoryModule` - Repository bindings (Impl bind Interface)
- `platformModule` - Firebase Analytics
- `viewModelModule` - ViewModel registrations via `viewModelOf(::XxxViewModel)`

Register new ViewModel: add `viewModelOf(::NewViewModel)` in `viewModelModule`.
Register new Repository: add `singleOf(::NewRepositoryImpl) bind NewRepository::class` in `repositoryModule`.

## Critical Conventions

- **Click listeners**: Use `setPreventDoubleClick { }` or `setPreventDoubleClickScaleView { }` instead of `setOnClickListener`
- **Dialog show**: Use `safeShowDialog(dialog)` / `safeShowBottomSheet(dialog)` from `Utils.kt`
- **Fragment params**: Never pass via constructor. Use `arguments` Bundle or shared ViewModel
- **Adapter listeners**: Never pass via constructor. Use `setListener()` method
- **Navigation**: Use `navigator.navigateTo(actionId)` / `navigator.navigateUp()` (Navigator interface wraps NavController)
- **CommonViewModel**: Shared across all fragments via `activityViewModel()`, manages app-wide state (categories, templates, premium status)
