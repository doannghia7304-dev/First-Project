package pion.tech.pionbase.util

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private inline fun <reified T> get(key: String, defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> sharedPreferences.getBoolean(key, defaultValue as Boolean)
            Int::class -> sharedPreferences.getInt(key, defaultValue as Int)
            Long::class -> sharedPreferences.getLong(key, defaultValue as Long)
            Float::class -> sharedPreferences.getFloat(key, defaultValue as Float)
            String::class -> sharedPreferences.getString(key, defaultValue as? String) ?: defaultValue
            Set::class -> sharedPreferences.getStringSet(key, defaultValue as? Set<String>) ?: defaultValue
            else -> throw IllegalArgumentException("Unsupported type")
        } as T
    }

    private inline fun <reified T> put(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                else -> throw IllegalArgumentException("Unsupported type")
            }.apply()
        }
    }

    var isPremium: Boolean
        get() = get("isPremium", false)
        set(value) = put("isPremium", value)

    var token: String?
        get() = get("token", null)
        set(value) = put("token", value)

    var floatValue: Float
        get() = get("floatValue", 0.0f)
        set(value) = put("floatValue", value)

    var stringSet: Set<String>
        get() = get("stringSet", emptySet())
        set(value) = put("stringSet", value)
}
