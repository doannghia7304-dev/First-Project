# Pion-Base: Kiến Trúc Ứng Dụng Android

## Tổng Quan Về Dự Án

Pion-Base là một dự án mẫu (template) cho ứng dụng Android tuân theo kiến trúc được đề xuất bởi Google. Dự án này tập trung vào việc tạo ra một nền tảng vững chắc cho việc phát triển ứng dụng Android với kiến trúc sạch, dễ bảo trì và mở rộng.

![Kiến trúc ứng dụng](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png)

## Kiến Trúc Ứng Dụng

Dự án sử dụng kiến trúc 2 lớp chính (bỏ qua Domain Layer để đơn giản hóa):

### Lớp UI (UI Layer)
Lớp UI hiển thị dữ liệu ứng dụng lên màn hình và phản hồi tương tác của người dùng. Lớp này sử dụng mô hình MVVM (Model-View-ViewModel) với các thành phần:
- **Fragment**: Định nghĩa giao diện người dùng và vòng đời màn hình
- **FragmentEx**: Extension functions xử lý logic tách biệt như sự kiện click, khởi tạo logic
- **ViewModel**: Quản lý trạng thái UI và xử lý logic nghiệp vụ

### Lớp Dữ Liệu (Data Layer)
Lớp Dữ liệu chứa logic truy cập dữ liệu và quản lý dữ liệu từ các nguồn khác nhau:
- **Repository**: Cung cấp API đơn giản, sạch sẽ cho phần còn lại của ứng dụng
- **Data Sources**: Quản lý dữ liệu từ API, cơ sở dữ liệu Room, DataStore
- **DTO Models**: Đại diện cho dữ liệu ở lớp Data
- **UI Models**: Đại diện cho dữ liệu ở lớp UI

## Cấu Trúc Dự Án

```
app/
├── src/main/java/pion/tech/pionbase/
│   ├── app/                    # Application class và CommonViewModel
│   ├── base/                   # Các lớp cơ sở
│   │   ├── BaseFragment.kt
│   │   ├── BaseViewModel.kt
│   │   ├── BaseDialogFragment.kt
│   │   └── BaseBottomSheetDialogFragment.kt
│   ├── data/                   # Lớp dữ liệu
│   │   ├── database/           # Room database
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── remote/             # API interfaces
│   │   └── repository/         # Repository implementations
│   ├── di/                     # Dependency Injection với Hilt
│   ├── feature/                # Các tính năng của ứng dụng
│   │   ├── splash/             # Màn hình khởi động
│   │   ├── onboard/            # Màn hình giới thiệu
│   │   ├── home/               # Màn hình chính
│   │   ├── setting/            # Màn hình cài đặt
│   │   └── language/           # Màn hình chọn ngôn ngữ
│   └── util/                   # Các tiện ích và constants
├── LibAds/                     # Module quản lý quảng cáo
└── LibIAP/                     # Module mua hàng trong ứng dụng
```

## Các Lớp Cơ Sở

### BaseFragment
```kotlin
abstract class BaseFragment<Binding : ViewBinding, VM : ViewModel>(
    private val inflate: Inflate<Binding>,
    private val viewModelClass: Class<VM>,
) : Fragment()
```

BaseFragment là lớp cơ sở cho tất cả các Fragment trong ứng dụng. Nó cung cấp:
- Quản lý ViewBinding tự động với lazy initialization
- Khởi tạo ViewModel thông qua ViewModelProvider
- CommonViewModel được inject thông qua activityViewModels()
- Xử lý điều hướng thông qua Navigator pattern
- Quản lý LoadingDialog tự động
- Tích hợp Firebase Analytics Logger
- Quản lý DataStore Repository
- Xử lý quảng cáo (App Resume Ads) với premium logic
- Hỗ trợ coroutines với các extension functions (launchIO, launchMain, launchDefault)
- Xử lý nút back hệ thống với callback tùy chỉnh

### BaseViewModel
```kotlin
abstract class BaseViewModel : ViewModel()
```

BaseViewModel là lớp cơ sở cho tất cả các ViewModel trong ứng dụng. Nó cung cấp:
- Quản lý coroutines
- Xử lý lỗi thống nhất
- Các tiện ích chung cho ViewModel

### BaseDialogFragment và BaseBottomSheetDialogFragment
Các lớp cơ sở cho dialog và bottom sheet dialog, cung cấp các chức năng tương tự như BaseFragment.

## Mẫu Thành Phần Màn Hình

Mỗi màn hình trong ứng dụng bao gồm 3 thành phần chính:

### 1. Fragment
Định nghĩa cấu trúc UI và vòng đời của màn hình. Ví dụ:

```kotlin
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
    ),
    DemoDialog.Listener {
    
    val adapter = InstallAppAdapter()

    override fun init(view: View) {
        initView()
        settingEvent()
        showDemoDialogEvent()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        // Observe installed apps state
        viewModel.installedAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = { showHideLoading(true) },
                onSuccess = { installedApps -> 
                    showHideLoading(false)
                    adapter.submitList(installedApps) 
                },
                onError = { 
                    showHideLoading(false)
                    displayToast("Failed to load installed apps") 
                }
            )
        }
    }
}
```

### 2. FragmentEx
Chứa các extension function cho Fragment, xử lý logic tách biệt như sự kiện click, khởi tạo logic. Ví dụ:

```kotlin
fun HomeFragment.initView() {
    commonViewModel.getApiData()
}

fun HomeFragment.plusEvent() {
    val listString = listOf("so1", "so2", "so3", "so4", "so5")
    adapter.submitList(listString)
}
```

### 3. ViewModel
Quản lý trạng thái UI và xử lý logic nghiệp vụ. Ví dụ:

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val installedAppsRepository: InstalledAppsRepository,
) : BaseViewModel()
```

## Phân Tách Model Theo Lớp

Ứng dụng phân tách model theo lớp để tách biệt trách nhiệm:

### DTO Models (Data Layer)
Đại diện cho dữ liệu ở lớp Data, thường được sử dụng trong Repository. Ví dụ:

```kotlin
data class InstalledAppDtoModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
)
```

### UI Models (UI Layer)
Đại diện cho dữ liệu ở lớp UI, được sử dụng trong ViewModel và Fragment. Ví dụ:

```kotlin
data class InstalledAppUIModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String?,
    val isSystemApp: Boolean,
)
```

### Mapping Functions
Chuyển đổi giữa DTO Models và UI Models thông qua extension function. Ví dụ:

```kotlin
fun InstalledAppDtoModel.toPresentation(): InstalledAppUIModel =
    InstalledAppUIModel(
        packageName = this.packageName,
        appName = this.appName,
        icon = this.icon,
        versionName = this.versionName,
        isSystemApp = this.isSystemApp,
    )
```

## Mẫu Repository

Tất cả các Repository trong ứng dụng đều tuân theo một mẫu nhất định:

### Repository Interface
Định nghĩa contract cho việc truy cập dữ liệu. Ví dụ:

```kotlin
interface InstalledAppsRepository {
    fun getInstalledApps(): Flow<Result<List<InstalledAppDtoModel>>>
}
```

### Repository Implementation
Triển khai Repository Interface, xử lý việc lấy và xử lý dữ liệu. Ví dụ:

```kotlin
class InstalledAppsRepositoryImpl(
    @ApplicationContext private val context: Context,
) : InstalledAppsRepository {
    override fun getInstalledApps(): Flow<Result<List<InstalledAppDtoModel>>> =
        flow {
            try {
                // Lấy dữ liệu
                emit(Result.Success(apps))
            } catch (exception: Exception) {
                emit(Result.Error(exception))
            }
        }.flowOn(Dispatchers.IO)
}
```

### Đặc điểm quan trọng:
- Tất cả các hàm lấy dữ liệu trong Repository đều trả về `Flow<Result<T>>`
- Sử dụng `Result.Success` và `Result.Error` để xử lý kết quả
- Sử dụng `flowOn(Dispatchers.IO)` để đảm bảo thao tác được thực hiện trên thread phù hợp

## Quản Lý Trạng Thái UI

Ứng dụng sử dụng `UiState` để quản lý trạng thái UI một cách nhất quán:

```kotlin
sealed interface UiState<out T> {
    data object None : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
}
```

### Trong ViewModel:
```kotlin
private val _installedAppsUiState = MutableStateFlow<UiState<List<InstalledAppUIModel>>>(UiState.None)
val installedAppsUiState = _installedAppsUiState.asStateFlow()

fun getInstalledApps() {
    handleApiCall(
        stateFlow = _installedAppsUiState,
        apiCall = { installedAppsRepository.getInstalledApps() },
        transform = { dtoList: List<InstalledAppDtoModel> -> 
            dtoList.map { it.toPresentation() } 
        }
    )
}
```

### Trong Fragment:
```kotlin
viewModel.installedAppsUiState.collectFlowOnView(viewLifecycleOwner) {
    it.handleUiState(
        onLoading = { showHideLoading(true) },
        onSuccess = { installedApps -> 
            showHideLoading(false)
            // Xử lý dữ liệu
        },
        onError = { exception ->
            showHideLoading(false)
            // Xử lý lỗi
        },
    )
}
```

## Các Tính Năng Được Triển Khai

Dự án Pion-Base bao gồm các màn hình và tính năng cơ bản sau:

### 1. Splash Screen (SplashFragment)
- Màn hình khởi động ứng dụng
- Hiển thị logo và loading
- Điều hướng tự động đến onboard hoặc home

### 2. Onboard Screen (OnboardFragment) 
- Màn hình giới thiệu ứng dụng cho người dùng mới
- Hướng dẫn sử dụng cơ bản
- Lưu trạng thái đã xem onboard vào DataStore

### 3. Home Screen (HomeFragment)
- Màn hình chính của ứng dụng
- Hiển thị danh sách ứng dụng đã cài đặt trên thiết bị
- Tích hợp với InstallAppAdapter để hiển thị danh sách
- Kết nối với API để lấy categories và templates
- Hỗ trợ dialog demo với callback interface

### 4. Settings Screen (SettingFragment)
- Màn hình cài đặt ứng dụng
- Quản lý các tùy chọn người dùng
- Tích hợp premium functionality

### 5. Language Screen (LanguageFragment)
- Màn hình chọn ngôn ngữ
- Hỗ trợ đa ngôn ngữ
- Lưu lựa chọn ngôn ngữ vào DataStore

### Các Repository Được Triển Khai:
- **ApiRepository**: Quản lý các API calls
- **DataStoreRepository**: Lưu trữ preferences và settings
- **InstalledAppsRepository**: Quản lý danh sách ứng dụng đã cài đặt
- **LanguageRepository**: Quản lý ngôn ngữ ứng dụng
- **RemoteConfigRepository**: Quản lý Firebase Remote Config

### Tích Hợp Thư Viện:
- **LibAds**: Module quản lý quảng cáo (App Resume Ads, Banner, Interstitial)
- **LibIAP**: Module mua hàng trong ứng dụng (In-App Purchase)
- **Firebase Analytics**: Theo dõi và phân tích hành vi người dùng
- **Room Database**: Cơ sở dữ liệu local với DummyDAO
- **DataStore**: Lưu trữ preferences thay thế SharedPreferences

## Lưu Ý Khi Phát Triển

1. **Tổ chức code**:
    - Tổ chức code theo tính năng (feature)
    - Mỗi tính năng có 3 thành phần chính: Fragment, FragmentEx (extension functions), ViewModel

2. **Dependency Injection**:
    - Sử dụng Hilt cho dependency injection
    - Đánh dấu các lớp với @AndroidEntryPoint, @HiltViewModel, @Inject khi cần thiết

3. **Coroutines và Flow**:
    - Sử dụng coroutines cho các tác vụ bất đồng bộ
    - Sử dụng Flow để xử lý dữ liệu reactive
    - Sử dụng StateFlow để quản lý trạng thái UI
    - Sử dụng các extension functions: launchIO, launchMain, launchDefault

4. **Xử lý lỗi và trạng thái**:
    - Sử dụng Result<T> và UiState<T> để xử lý lỗi một cách nhất quán
    - Luôn xử lý các trường hợp loading, success, error trong UI
    - Sử dụng handleUiState extension function

5. **Model và Repository**:
    - Phân tách DTO Models (Data Layer) và UI Models (UI Layer)
    - Sử dụng toPresentation() extension function để mapping
    - Repository functions phải return Flow<Result<T>>

6. **Mở rộng dự án**:
    - Khi thêm tính năng mới, tạo package mới trong feature/
    - Tuân theo pattern Fragment + FragmentEx + ViewModel
    - Tạo Repository và DTO/UI Models nếu cần
    - Sử dụng các base classes (BaseFragment, BaseViewModel, etc.)

7. **Performance và Memory**:
    - Sử dụng ViewBinding thay vì findViewById
    - Quản lý lifecycle đúng cách với viewLifecycleOwner
    - Cancel coroutines khi không cần thiết

## Hình Ảnh Minh Họa

![Kiến trúc MVVM](https://miro.medium.com/v2/resize:fit:1400/1*BpxMFh7DdX0_hqX6ABkDgw.png)

![Luồng dữ liệu](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview-ui.png)