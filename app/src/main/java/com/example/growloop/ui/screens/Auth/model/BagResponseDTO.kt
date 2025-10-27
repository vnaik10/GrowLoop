package com.example.growloop.ui.screens.Auth.model

// BagResponseDTO.kt
data class BagResponseDTO(
    val bagId: Long,
    val bagName: String,
    val sharableLinkToken: String,
    val status: String,
    val createdAt: String,
    val totalItems: Int,
    val pointsAwarded: Int,
    val deliveryCharge: Double,
    val canAcceptItems: Boolean,
    val eligibleForFreePickup: Boolean,
    val pickupCost: Double,
    val pickupMessage: String,
    val creatorName: String
)

// BagCreateRequest.kt
data class BagCreateRequest(
    val bagName: String
)

// ItemResponseDTO.kt
data class ItemResponseDTO(
    val itemId: Long,
    val itemType: String,
    val conditionDescription: String?,
    val gender: String?,
    val ageGroup: String?,
    val grade: String,
    val status: String,
    val loyaltyPoint: Double,
    val addedAt: String,
    val contributorName: String
)

// ApiResponse.kt (generic wrapper)
data class BagApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// BagStatus enum for Android
enum class BagStatus(val displayName: String) {
    OPEN("Open - Adding Items"),
    AWAITING_PICKUP("Awaiting Pickup"),
    COLLECTED("Items Collected"),
    CLOSED("Bag Closed");

    companion object {
        fun fromString(status: String): BagStatus {
            return values().find { it.name.equals(status, ignoreCase = true) } ?: OPEN
        }
    }
}
