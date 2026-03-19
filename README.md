# Pion-Base

Android template project sử dụng kiến trúc MVVM 2 lớp (UI + Data), single-Activity với Navigation Component.

![Kiến trúc ứng dụng](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png)

## Tech Stack

| Thành phần | Công nghệ |
|-----------|-----------|
| Language | Kotlin, JVM 17 |
| DI | Koin |
| Navigation | Jetpack Navigation Component |
| Network | Retrofit2 + OkHttp3 + Gson |
| Database | Room |
| Preferences | DataStore |
| Image | Glide |
| Async | Coroutines + Flow + StateFlow |
| Analytics | Firebase Analytics, Crashlytics, Remote Config |
| Logging | Timber (app), Chucker (HTTP debug) |
| SDK | Min 24, Target/Compile 35 |

## Modules

| Module | Mô tả |
|--------|--------|
| `:app` | Ứng dụng chính (`pion.tech.pionbase`) |
| `:LibAds` | Quản lý quảng cáo |

## Cấu Trúc Feature

Mỗi feature gồm 3 file + thư mục con tùy chọn:

```
feature/{name}/
├── {Name}Fragment.kt       # UI + observer
├── {Name}FragmentEx.kt     # Extension functions (click, init)
├── {Name}ViewModel.kt      # State + logic
├── adapter/                # RecyclerView adapters
├── dialog/                 # Dialogs
└── bottomSheet/            # Bottom sheets
```

## Base Classes

> **Bắt buộc** dùng base classes, không dùng class gốc Android.

| Thay vì | Dùng | Generic params |
|---------|------|----------------|
| `Fragment` | `BaseFragment` | `<ViewBinding, ViewModel>(inflate, KClass)` |
| `ViewModel` | `BaseViewModel` | `<State, Event>(initialState)` |
| `ListAdapter` | `BaseListAdapter` | `<Item, ViewDataBinding>(diffCallback)` |
| `DialogFragment` | `BaseDialogFragment` | `<ViewDataBinding>(layoutRes)` |
| `BottomSheetDialogFragment` | `BaseBottomSheetDialogFragment` | `<ViewDataBinding>(layoutRes)` |

### BaseFragment

Override 2 method:
- `init(view)` -- setup view, gắn event
- `subscribeObserver(view)` -- observe StateFlow

Cung cấp sẵn: `binding`, `viewModel`, `commonViewModel`, `navigator`, `dataStoreRepository`, `logger`, `showHideLoading()`, `onSystemBack { }`

### BaseViewModel

State quản lý qua `uiState` (StateFlow), event one-shot qua `uiEvent` (Channel):

```kotlin
class HomeViewModel(...) : BaseViewModel<HomeUiState, Nothing>(HomeUiState()) {
    fun load() {
        setState { copy(isLoading = true) }
        handleApiCall(
            apiCall = { repo.getData() },
            onSuccess = { setState { copy(data = it, isLoading = false) } },
            onError = { setState { copy(error = it, isLoading = false) } },
        )
    }
}
```

### BaseDialogFragment / BaseBottomSheetDialogFragment

Override 3 method tùy chọn: `initData()`, `initView()`, `addEvent()`

### BaseListAdapter

Override: `getLayoutRes(viewType)`, `bindView(binding, item, position)`. Dùng `createDiffCallback()` helper.

## Luồng Điều Hướng

```
Splash ──→ Language (lần đầu) ──→ Onboard ──→ Home
Splash ──→ Home (đã dùng)
Home ──→ Setting ──→ ChangeLanguage
```

## Data Layer

### Repository Pattern

Tất cả repository trả về `Flow<Result<T>>`:

```kotlin
interface XxxRepository {
    fun getData(): Flow<Result<List<XxxDtoModel>>>
}
```

Implementation dùng `flow { }.flowOn(Dispatchers.IO)`.

### Model

| Loại | Naming | Dùng ở |
|------|--------|--------|
| DTO | `{Entity}DtoModel` | Repository / Data layer |
| UI | `{Entity}UIModel` | ViewModel / Fragment |

Mapping qua extension: `fun XxxDtoModel.toPresentation(): XxxUIModel`

## State Management

Mỗi màn hình có `data class XxxUiState(...)`. Observe trong Fragment:

```kotlin
viewModel.uiState
    .map { it.field }
    .distinctUntilChanged()
    .collectFlowOnView(viewLifecycleOwner) { /* update UI */ }
```

`CommonViewModel` (shared toàn app qua `activityViewModel`): quản lý categories, templates, premium status.

## Koin DI

| Module | Nội dung |
|--------|----------|
| `coreModule` | Firebase RemoteConfig, DataStore |
| `networkModule` | Gson, OkHttp, Retrofit, ApiInterface |
| `databaseModule` | Room, DAOs |
| `repositoryModule` | Repository bindings |
| `platformModule` | Firebase Analytics |
| `viewModelModule` | ViewModel registrations |

Thêm mới:
- ViewModel: `viewModelOf(::NewVM)` trong `viewModelModule`
- Repository: `singleOf(::NewRepoImpl) bind NewRepo::class` trong `repositoryModule`

## Quy Tắc Bắt Buộc

| Quy tắc | Đúng | Sai |
|---------|------|-----|
| Click listener | `view.setPreventDoubleClick { }` | `view.setOnClickListener { }` |
| Show dialog | `safeShowDialog(dialog)` | `dialog.show(...)` trực tiếp |
| Fragment params | `arguments` Bundle / shared ViewModel | Constructor params |
| Adapter listener | `adapter.setListener(this)` | Constructor lambda |
| Navigation | `navigator.navigateTo(actionId)` | `findNavController()` trực tiếp |
| Coroutines | `launchIO { }` / `launchMain { }` / `launchDefault { }` | `viewModelScope.launch()` / `lifecycleScope.launch()` |

## Hình Ảnh Minh Họa

![Kiến trúc MVVM](https://miro.medium.com/v2/resize:fit:1400/1*BpxMFh7DdX0_hqX6ABkDgw.png)

![Luồng dữ liệu](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview-ui.png)
