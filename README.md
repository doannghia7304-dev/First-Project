# Pion-Base: Kiến Trúc Ứng Dụng Android

## Tổng Quan Về Kiến Trúc

Dự án này tuân theo kiến trúc ứng dụng được đề xuất bởi Google, tập trung vào hai lớp chính:

![Kiến trúc ứng dụng](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png)

### Lớp UI (UI Layer)
Lớp UI hiển thị dữ liệu ứng dụng lên màn hình và phản hồi tương tác của người dùng. Lớp này sử dụng mô hình MVVM (Model-View-ViewModel) với các thành phần:
- **Fragment**: Định nghĩa giao diện người dùng
- **FragmentEx**: Xử lý logic tách biệt như xử lý sự kiện click, khởi tạo logic
- **ViewModel**: Quản lý trạng thái UI và xử lý logic nghiệp vụ

### Lớp Dữ Liệu (Data Layer)
Lớp Dữ liệu chứa logic nghiệp vụ và quản lý dữ liệu từ các nguồn khác nhau. Lớp này bao gồm:
- **Repository**: Cung cấp API đơn giản, sạch sẽ cho phần còn lại của ứng dụng
- **Data Sources**: Quản lý dữ liệu từ các nguồn khác nhau (API, cơ sở dữ liệu, bộ nhớ đệm)
- **Model**: Đại diện cho dữ liệu trong ứng dụng

## Cấu Trúc Mã Nguồn

Dự án được tổ chức thành các module chính:

### Module Core
Chứa các thành phần cơ bản và tiện ích được sử dụng trong toàn bộ ứng dụng:
- **base**: Các lớp cơ sở như BaseFragment, BaseViewModel, BaseDialogFragment
- **di**: Cấu hình Dependency Injection với Hilt
- **utils**: Các tiện ích và extension functions
- **navigator**: Xử lý điều hướng trong ứng dụng

### Module App
Chứa các tính năng cụ thể của ứng dụng:
- **feature**: Các tính năng được tổ chức theo package riêng biệt
- **data**: Chứa repositories, data sources và models
- **util**: Các tiện ích cụ thể cho ứng dụng

## Các Lớp Cơ Sở

### BaseFragment
```kotlin
abstract class BaseFragment<Binding : ViewBinding, VM : ViewModel, CommonVM : ViewModel>(
    private val inflate: Inflate<Binding>,
    private val viewModelClass: Class<VM>,
    private val commonViewModelClass: Class<CommonVM>,
) : Fragment()
```

BaseFragment là lớp cơ sở cho tất cả các Fragment trong ứng dụng. Nó cung cấp:
- Quản lý ViewBinding tự động
- Khởi tạo ViewModel và CommonViewModel
- Xử lý điều hướng thông qua Navigator
- Quản lý dialog loading
- Xử lý nút back hệ thống

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
    BaseFragment<FragmentHomeBinding, HomeViewModel, CommonViewModel>(
        FragmentHomeBinding::inflate,
        HomeViewModel::class.java,
        CommonViewModel::class.java,
    )
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

## Nguyên Tắc SOLID

Dự án áp dụng các nguyên tắc SOLID để tạo ra mã nguồn dễ bảo trì và mở rộng:

### Single Responsibility (Trách nhiệm đơn lẻ)
Mỗi lớp chỉ có một trách nhiệm duy nhất. Ví dụ:
- Fragment: Hiển thị UI
- ViewModel: Quản lý trạng thái và logic
- Repository: Truy cập dữ liệu

### Open/Closed (Mở/Đóng)
Các lớp mở rộng nhưng đóng sửa đổi. Ví dụ:
- Sử dụng interface cho Repository để có thể thay đổi implementation mà không ảnh hưởng đến code sử dụng nó

### Liskov Substitution (Thay thế Liskov)
Các lớp con có thể thay thế lớp cha mà không làm thay đổi tính đúng đắn của chương trình. Ví dụ:
- Tất cả các Fragment đều kế thừa từ BaseFragment và tuân theo cùng một contract

### Interface Segregation (Phân tách Interface)
Sử dụng nhiều interface nhỏ thay vì một interface lớn. Ví dụ:
- Repository interface chỉ định nghĩa các phương thức cần thiết cho một tính năng cụ thể

### Dependency Inversion (Đảo ngược phụ thuộc)
Phụ thuộc vào abstraction, không phụ thuộc vào implementation. Ví dụ:
- ViewModel phụ thuộc vào Repository interface, không phụ thuộc vào implementation cụ thể
- Sử dụng Hilt để inject các dependency

## Lưu Ý Khi Phát Triển

1. **Tổ chức code**:
    - Tổ chức code theo tính năng (feature)
    - Mỗi tính năng có 3 thành phần: Fragment, FragmentEx, ViewModel

2. **Dependency Injection**:
    - Sử dụng Hilt cho dependency injection
    - Đánh dấu các lớp với @AndroidEntryPoint, @HiltViewModel, @Inject khi cần thiết

3. **Coroutines và Flow**:
    - Sử dụng coroutines cho các tác vụ bất đồng bộ
    - Sử dụng Flow để xử lý dữ liệu reactive
    - Sử dụng StateFlow để quản lý trạng thái UI

4. **Xử lý lỗi**:
    - Sử dụng Result và UiState để xử lý lỗi một cách nhất quán
    - Luôn xử lý các trường hợp lỗi trong UI

5. **Mở rộng**:
    - Khi thêm tính năng mới, tạo package mới trong feature
    - Tuân theo mẫu Fragment, FragmentEx, ViewModel
    - Tạo Repository mới nếu cần

## Hình Ảnh Minh Họa

![Kiến trúc MVVM](https://miro.medium.com/v2/resize:fit:1400/1*BpxMFh7DdX0_hqX6ABkDgw.png)

![Luồng dữ liệu](https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview-ui.png)