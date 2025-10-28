package com.example.growloop.ui.screens.Auth.model

data class AiResponseDto(
    val detections: List<Detection>
)

data class Detection(
    val bounding_box: List<Double>,
    val class_id: Int,
    val class_name: String,
    val confidence: Double
)