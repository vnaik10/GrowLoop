package com.example.growloop.ui.screens.Auth.model

// Data classes for API requests/responses
data class UserRegistrationRequest(
    val userName: String,
    val email: String,
    val phoneNumber: String? = null,
    val addressText: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class UserResponseDTO(
    val userId: Long,
    val firebaseUid: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val addressText: String?,
    val loyaltyPoint: Int,
    val isPremium: Boolean,
    val isVerified: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

data class UserApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
