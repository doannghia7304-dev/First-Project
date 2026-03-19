package pion.tech.pionbase.base.firebaseAnalytics

/**
 * Utility object để sanitize event name theo chuẩn Firebase Analytics.
 *
 * Firebase Analytics Event Name Restrictions:
 * 1. Độ dài tối đa: 40 ký tự
 * 2. Ký tự đầu tiên: phải là chữ cái (a-z, A-Z)
 * 3. Ký tự được phép: chữ cái, số, và dấu gạch dưới (a-z, A-Z, 0-9, _)
 * 4. Không được bắt đầu bằng: firebase_, google_, ga_ (reserved prefixes)
 * 5. Tự động loại bỏ tất cả từ "Fragment" ở bất kỳ vị trí nào
 * 6. Chuyển đổi camelCase/PascalCase sang snake_case
 *
 * @see <a href="https://firebase.google.com/docs/analytics/events">Firebase Analytics Events</a>
 */
object FirebaseEventNameSanitizer {
    private const val MAX_LENGTH = 40
    private val RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")
    private val INVALID_CHARS_REGEX = Regex("[^a-zA-Z0-9_]")
    private val LEADING_NON_ALPHA_REGEX = Regex("^[^a-zA-Z]+")
    private val MULTIPLE_UNDERSCORES_REGEX = Regex("_+")

    // Loại bỏ tất cả từ "Fragment" ở bất kỳ vị trí nào (đầu, giữa, cuối)
    private val FRAGMENT_REGEX = Regex("fragment", RegexOption.IGNORE_CASE)

    // Match chữ hoa để chuyển camelCase → snake_case
    private val CAMEL_CASE_REGEX = Regex("([a-z0-9])([A-Z])")

    /**
     * Sanitize một chuỗi thành event name hợp lệ cho Firebase Analytics.
     *
     * @param input Chuỗi đầu vào cần sanitize
     * @return Event name hợp lệ, hoặc null nếu không thể tạo event name hợp lệ
     *
     * Quy trình xử lý:
     * 1. Loại bỏ tất cả từ "Fragment" (case-insensitive)
     * 2. Chuyển camelCase/PascalCase sang snake_case
     * 3. Thay thế ký tự không hợp lệ bằng "_"
     * 4. Loại bỏ ký tự không phải chữ cái ở đầu chuỗi
     * 5. Gộp nhiều "_" liên tiếp thành 1 "_"
     * 6. Loại bỏ "_" ở đầu và cuối
     * 7. Chuyển thành lowercase
     * 8. Xử lý reserved prefixes
     * 9. Cắt ngắn nếu vượt quá 40 ký tự
     */
    fun sanitize(input: String?): String? {
        if (input.isNullOrBlank()) return null

        var result =
            input
                // Bước 1: Loại bỏ tất cả từ "Fragment"
                .replace(FRAGMENT_REGEX, "")
                // Bước 2: Chuyển camelCase → snake_case (VD: changeLanguage → change_Language)
                .replace(CAMEL_CASE_REGEX, "$1_$2")
                // Bước 3: Thay thế ký tự không hợp lệ bằng "_"
                .replace(INVALID_CHARS_REGEX, "_")
                // Bước 4: Loại bỏ ký tự không phải chữ cái ở đầu
                .replace(LEADING_NON_ALPHA_REGEX, "")
                // Bước 5: Gộp nhiều "_" liên tiếp thành 1
                .replace(MULTIPLE_UNDERSCORES_REGEX, "_")
                // Bước 6: Loại bỏ "_" ở đầu và cuối
                .trim('_')
                // Bước 7: Chuyển thành lowercase
                .lowercase()

        // Kiểm tra nếu kết quả rỗng hoặc không bắt đầu bằng chữ cái
        if (result.isEmpty() || !result.first().isLetter()) {
            return null
        }

        // Bước 8: Xử lý reserved prefixes
        result = handleReservedPrefixes(result)

        // Bước 9: Cắt ngắn nếu vượt quá MAX_LENGTH
        if (result.length > MAX_LENGTH) {
            result = result.take(MAX_LENGTH).trimEnd('_')
        }

        return result.ifEmpty { null }
    }

    /**
     * Sanitize và thêm suffix vào event name.
     *
     * @param input Chuỗi đầu vào
     * @param suffix Suffix cần thêm (ví dụ: "_show", "_view")
     * @return Event name với suffix, hoặc null nếu không hợp lệ
     */
    fun sanitizeWithSuffix(
        input: String?,
        suffix: String,
    ): String? {
        // Validate và normalize suffix trước
        val trimmedSuffix = suffix.trim()
        if (trimmedSuffix.isBlank()) return null
        if (trimmedSuffix.contains(INVALID_CHARS_REGEX)) return null

        val sanitized = sanitize(input) ?: return null

        // Tính toán độ dài tối đa cho phần chính (trừ đi độ dài suffix đã trim)
        val maxMainLength = MAX_LENGTH - trimmedSuffix.length
        if (maxMainLength <= 0) return null

        val truncated =
            if (sanitized.length > maxMainLength) {
                sanitized.take(maxMainLength).trimEnd('_')
            } else {
                sanitized
            }

        return if (truncated.isEmpty()) null else "$truncated$trimmedSuffix"
    }

    /**
     * Kiểm tra xem một event name có hợp lệ hay không.
     *
     * @param eventName Event name cần kiểm tra
     * @return true nếu hợp lệ, false nếu không
     */
    fun isValid(eventName: String?): Boolean {
        if (eventName.isNullOrBlank()) return false
        if (eventName.length > MAX_LENGTH) return false
        if (!eventName.first().isLetter()) return false
        if (eventName.contains(INVALID_CHARS_REGEX)) return false
        if (RESERVED_PREFIXES.any { eventName.lowercase().startsWith(it) }) return false
        return true
    }

    private fun handleReservedPrefixes(input: String): String {
        val lowerInput = input.lowercase()
        for (prefix in RESERVED_PREFIXES) {
            if (lowerInput.startsWith(prefix)) {
                // Thêm "app_" vào đầu để tránh reserved prefix
                return "app_$input"
            }
        }
        return input
    }
}
