package com.example.libiap.model

data class PricingPhaseModel(
    val priceCurrencyCode: String,
    val recurrenceMode: Int,
    val priceAmountMicros: Long,
    val formattedPrice: String,
    val billingPeriod: String,
    val billingCycleCount: Int
)