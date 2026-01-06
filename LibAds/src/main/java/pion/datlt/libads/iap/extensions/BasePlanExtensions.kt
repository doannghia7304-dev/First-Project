package pion.datlt.libads.iap.extensions

import pion.datlt.libads.iap.model.BasePlanModel
import pion.datlt.libads.iap.model.PricingPhaseModel

/**
 * Extension functions for BasePlanModel.
 */

/**
 * Recurrence mode indicating a finite billing period.
 */
private const val RECURRENCE_MODE_FINITE = 2

/**
 * Checks if this base plan has a free trial phase.
 * @param isProductPurchased Whether the product is already purchased
 * @return true if free trial is available
 */
fun BasePlanModel.hasFreeTrial(isProductPurchased: Boolean = false): Boolean {
    if (isProductPurchased) return false

    return listPricingPhase.any { phase ->
        phase.isFreeTrialPhase()
    }
}

/**
 * Gets the duration of free trial in days.
 * @return Number of free trial days, or 0 if no free trial
 */
fun BasePlanModel.getFreeTrialDays(): Int {
    val freeTrialPhase = listPricingPhase.find { it.isFreeTrialPhase() }
    return freeTrialPhase?.let { billingPeriodToDays(it.billingPeriod) } ?: 0
}

/**
 * Gets the regular price (non-trial) for this base plan.
 * @return Formatted price string or null if not found
 */
fun BasePlanModel.getRegularPrice(): String? = listPricingPhase.find { !it.isFreeTrialPhase() }?.formattedPrice

/**
 * Checks if this pricing phase represents a free trial.
 */
private fun PricingPhaseModel.isFreeTrialPhase(): Boolean =
    priceAmountMicros == 0L &&
        recurrenceMode == RECURRENCE_MODE_FINITE &&
        billingCycleCount >= 1 &&
        billingPeriodToDays(billingPeriod) > 0

/**
 * Converts Google Play BillingPeriod (ISO-8601) to days.
 *
 * Examples:
 *  - P7D  -> 7
 *  - P1M  -> 30
 *  - P3M  -> 90
 *  - P1Y  -> 365
 */
fun billingPeriodToDays(period: String): Int {
    val regex = Regex("""P(\d+)([DWMY])""")
    val match = regex.matchEntire(period) ?: return 0

    val value = match.groupValues[1].toInt()
    val unit = match.groupValues[2]

    return when (unit) {
        "D" -> value

        "W" -> value * 7

        "M" -> value * 30

        // Google Play convention
        "Y" -> value * 365

        // Google Play convention
        else -> 0
    }
}
