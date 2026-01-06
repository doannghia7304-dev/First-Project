package com.example.libiap.utils

import kotlin.text.contains
import kotlin.text.isEmpty
import kotlin.text.startsWith
import kotlin.text.substringAfter
import kotlin.text.substringBefore
import kotlin.text.toInt

/**
 * Utility object to parse ISO 8601 duration format to days
 *
 * Supported formats:
 * - P7D -> 7 days
 * - P1W -> 7 days (1 week)
 * - P1M -> 30 days (approximate, 1 month)
 * - P3M -> 90 days (approximate, 3 months)
 * - P1Y -> 365 days (approximate, 1 year)
 *
 * Examples from Google Play Billing:
 * - Free trial: "P7D" (7 days free)
 * - Weekly subscription: "P1W"
 * - Monthly subscription: "P1M"
 */
object ISODurationParser {
    /**
     * Parse ISO 8601 duration string to number of days
     *
     * @param isoDuration ISO 8601 duration format (e.g., "P7D", "P1W", "P1M")
     * @return Number of days, or 0 if parsing fails
     */
    fun parseToDays(isoDuration: String): Int {
        if (isoDuration.isEmpty() || !isoDuration.startsWith("P")) {
            return 0
        }

        return try {
            when {
                // Days: P7D -> 7
                isoDuration.contains("D") -> {
                    isoDuration.substringAfter("P").substringBefore("D").toInt()
                }

                // Weeks: P1W -> 7
                isoDuration.contains("W") -> {
                    isoDuration.substringAfter("P").substringBefore("W").toInt() * 7
                }

                // Months: P1M -> 30 (approximate)
                // Note: This doesn't handle years before months (e.g., P1Y2M)
                isoDuration.contains("M") && !isoDuration.contains("Y") -> {
                    isoDuration.substringAfter("P").substringBefore("M").toInt() * 30
                }

                // Years: P1Y -> 365 (approximate)
                isoDuration.contains("Y") -> {
                    val yearPart = isoDuration.substringAfter("P").substringBefore("Y").toInt()
                    var totalDays = yearPart * 365

                    // Handle months after years: P1Y2M
                    if (isoDuration.contains("M")) {
                        val monthPart = isoDuration.substringAfter("Y").substringBefore("M").toInt()
                        totalDays += monthPart * 30
                    }

                    totalDays
                }

                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Format days to human-readable string
     *
     * @param days Number of days
     * @return Formatted string (e.g., "7 days", "1 week", "1 month")
     */
    fun formatDaysToString(days: Int): String =
        when {
            days == 0 -> ""
            days == 1 -> "1 day"
            days == 7 -> "1 week"
            days == 14 -> "2 weeks"
            days == 30 -> "1 month"
            days == 90 -> "3 months"
            days == 365 -> "1 year"
            days % 7 == 0 -> "${days / 7} weeks"
            days % 30 == 0 -> "${days / 30} months"
            else -> "$days days"
        }
}
