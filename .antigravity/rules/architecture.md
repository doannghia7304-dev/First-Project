# Pion-Base - UI Layer & Architecture Rules

Scope: Apply when working on UI-layer code (Fragments, ViewModels, adapters, dialogs) and dependency injection.

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

## BaseFragment

- Generic params: `<ViewBinding, ViewModel>`; constructor takes `inflate` function reference and ViewModel `KClass`.
- Override `init(view: View)` for setup and `subscribeObserver(view: View)` for Flow collection.
- Provides `binding`, `viewModel`, `commonViewModel`, `navigator`, `dataStoreRepository`, `logger`.
- Built-in `showHideLoading(Boolean)` and `onSystemBack { }`.

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

## FragmentEx Pattern

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

## BaseViewModel

- Generic params: `<State, Event>` with `initialState`.
- Built-in `_uiState`/`uiState` (StateFlow) and `_uiEvent`/`uiEvent` (Channel).
- Use `setState { copy(...) }` to update state, `setEvent(event)` to emit one-shot events.
- Each screen defines its own `data class XxxUiState(...)` passed as `initialState`.
- Inject **UseCases** via the constructor, never Repositories. `handleApiCall`'s `apiCall` invokes a UseCase. See `usecase.md` for the UseCase convention.

## State Observation

Always use `collectFlowOnView` with `map + distinctUntilChanged` to observe specific state fields:

```kotlin
viewModel.uiState
    .map { it.fieldName }
    .distinctUntilChanged()
    .collectFlowOnView(viewLifecycleOwner) { value -> /* handle */ }
```

`CommonViewModel` is shared across all fragments via `activityViewModel()` and manages app-wide state (categories, templates, premium status).

## Coroutine Extensions

Use the built-in coroutine launchers (they include exception handling), available in both Fragment and ViewModel:

```kotlin
launchIO { }       // IO dispatcher
launchMain { }     // Main dispatcher
launchDefault { }  // Default dispatcher
```

Do NOT use `viewModelScope.launch()` or `lifecycleScope.launch()` directly.

## Dependency Injection (Koin)

Modules defined in `di/` package, aggregated in `AppModule.kt`:

| Module | Contents |
|--------|----------|
| `coreModule` | Firebase RemoteConfig, DataStore |
| `networkModule` | Gson, OkHttp, Retrofit, ApiInterface |
| `databaseModule` | Room database + DAOs |
| `repositoryModule` | Repository bindings (Impl bind Interface) |
| `useCaseModule` | UseCase registrations via `factoryOf(::XxxUseCase)` |
| `platformModule` | Firebase Analytics |
| `viewModelModule` | ViewModel registrations via `viewModelOf(::XxxViewModel)` |

The `useCaseModule` is registered in `appModules` between `repositoryModule` and `viewModelModule`.

- Register new ViewModel: add `viewModelOf(::NewViewModel)` in `viewModelModule`.
- Register new UseCase: add `factoryOf(::NewUseCase)` in `useCaseModule`.
- Register new Repository: add `singleOf(::NewRepositoryImpl) bind NewRepository::class` in `repositoryModule`.
