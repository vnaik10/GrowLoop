
data class BagCreateRequest(
    val bagName: String,
    val purpose: String  // "RESALE" or "DONATION"
)

data class BagResponseDTO(
    val bagId: Long,
    val bagName: String,
    val sharableLink: String,
    val shareableUrl: String,
    val status: String,
    val statusDisplayName: String,
    val purpose: String,           // "RESALE", "DONATION"
    val purposeDisplayName: String, // "Resale - Earn Money"
    val createdAt: String,
    val totalItems: Int,
    val pointsAwarded: Int,
    val deliveryCharge: Double,
    val canAcceptItems: Boolean,
    val eligibleForFreePickup: Boolean,
    val pickupCost: Double,
    val pickupMessage: String,
    val creatorName: String,
    val creatorId: Long
) {
    fun isResaleBag(): Boolean = purpose == "RESALE"
    fun isDonationBag(): Boolean = purpose == "DONATION"

    fun getPurposeIcon(): String = when (purpose) {
        "RESALE" -> "💰"
        "DONATION" -> "❤️"
        else -> "📦"
    }

    fun getPurposeColor(): androidx.compose.ui.graphics.Color = when (purpose) {
        "RESALE" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)  // Green
        "DONATION" -> androidx.compose.ui.graphics.Color(0xFFE91E63) // Pink
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}

enum class BagPurpose(val displayName: String, val description: String) {
    RESALE("Resale", "Earn money from sales"),
    DONATION("Donation", "Help families in need");

    val icon: String get() = when (this) {
        RESALE -> "💰"
        DONATION -> "❤️"
    }

    val color: androidx.compose.ui.graphics.Color get() = when (this) {
        RESALE -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        DONATION -> androidx.compose.ui.graphics.Color(0xFFE91E63)
    }
}
