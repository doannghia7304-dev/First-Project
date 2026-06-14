# Pion-Base - Global Rules

Scope: Project-wide rules. Apply to all files in this repository. These are always-on core conventions for the Pion-Base Android template.

## Project Overview

Pion-Base is an Android template using a **3-layer architecture** (UI Layer + Domain Layer + Data Layer) with the **MVVM** pattern. Kotlin, ViewBinding/DataBinding, Koin DI, single-Activity Navigation Component.

Data flow: `Fragment -> ViewModel -> UseCase -> Repository -> (API/DB)`. ViewModels depend on single-responsibility UseCases, never on Repositories directly.

### Modules

- `:app` - Main application module (`pion.tech.pionbase`)
- `:LibAds` - Ads management library module

### Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin, JVM (Java 17) |
| DI | Koin (NOT Hilt/Dagger) |
| Navigation | Jetpack Navigation Component (single `nav_main.xml`) |
| Network | Retrofit2 + OkHttp3 + Gson |
| Database | Room |
| Preferences | DataStore |
| Image Loading | Glide |
| Logging | Timber (app), Chucker (HTTP debug) |
| Analytics | Firebase Analytics + Crashlytics + Remote Config |
| Async | Kotlin Coroutines + Flow + StateFlow |
| SDK | Min 24, Target/Compile 35 |

## Build Commands

```bash
./gradlew assembleDebug       # Build debug APK
./gradlew assembleRelease     # Build release APK
./gradlew test                # Run unit tests
./gradlew testDebugUnitTest --tests "pion.tech.pionbase.ExampleUnitTest"  # Single test class
./gradlew clean assembleDebug # Clean build
./gradlew lint                # Check lint
```

## Base Classes (MANDATORY)

Never use raw Android classes. Always extend from these base classes:

| Instead of | Use |
|-----------|-----|
| `Fragment` | `BaseFragment<Binding, VM>(inflate, vmClass)` |
| `ViewModel` | `BaseViewModel<State, Event>(initialState)` |
| `ListAdapter` | `BaseListAdapter<Item, ViewBinding>(diffCallback)` |
| `DialogFragment` | `BaseDialogFragment<T>(layoutRes)` |
| `BottomSheetDialogFragment` | `BaseBottomSheetDialogFragment<T>(layoutRes)` |

## Critical Conventions

| Rule | Correct | Wrong |
|------|---------|-------|
| Click listener | `view.setPreventDoubleClick { }` / `setPreventDoubleClickScaleView { }` | `view.setOnClickListener { }` |
| Show dialog | `safeShowDialog(dialog)` / `safeShowBottomSheet(dialog)` | `dialog.show(...)` directly |
| Fragment params | `arguments` Bundle / shared ViewModel | Constructor params |
| Adapter listener | `adapter.setListener(this)` | Constructor lambda |
| Navigation | `navigator.navigateTo(actionId)` / `navigator.navigateUp()` | `findNavController()` directly |
| Coroutines | `launchIO { }` / `launchMain { }` / `launchDefault { }` | `viewModelScope.launch()` / `lifecycleScope.launch()` |
| Data access | ViewModel uses single-responsibility `UseCase` | ViewModel injects/calls `Repository` directly |
