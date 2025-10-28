package com.example.growloop.ui.screens.Auth.model

data class ItemResponseDTO(
    val itemId: Long,
    val bagId: Long?,
    val bagName: String?,
    val contributorId: Long,
    val contributorName: String,
    val itemType: String,
    val conditionDescription: String?,
    val gender: String?,
    val ageGroup: String?,
    val grade: String,            
    val gradeDisplayName: String,
    val status: String,
    val statusDisplayName: String,
    val loyaltyPoint: String,
    val addedAt: String,
    val isGradeA: Boolean,
    val isReadyForListing: Boolean
)

data class ItemCreateRequest(
    val itemType: String,
    val conditionDescription: String? = null,
    val gender: String? = null,
    val ageGroup: String? = null,
    val isDirectRecycle: Boolean = false
)

data class ItemUpdateRequest(
    val grade: String? = null,
    val status: String? = null,
    val loyaltyPoint: String? = null,
    val qcNotes: String? = null
)
