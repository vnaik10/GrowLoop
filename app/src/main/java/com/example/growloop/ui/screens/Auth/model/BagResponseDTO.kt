import androidx.compose.ui.graphics.Color
import com.example.growloop.ui.screens.Auth.model.ItemResponseDTO
import com.example.growloop.ui.screens.home.ContributorInfo

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
    val purpose: String,
    val purposeDisplayName: String,
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

    fun getPurposeColor(): Color = when (purpose) {
        "RESALE" -> Color(0xFF4CAF50)
        "DONATION" -> Color(0xFFE91E63)
        else -> Color.Gray
    }

    fun getContributorsFromItems(items: List<ItemResponseDTO>): List<ContributorInfo> {
        return items
            .filter { it.contributorId != creatorId }
            .groupBy { it.contributorId }
            .map { (id, contributorItems) ->
                ContributorInfo(
                    userId = id,
                    userName = contributorItems.first().contributorName,
                    itemCount = contributorItems.size
                )
            }
    }
}

enum class BagPurpose(val displayName: String, val description: String) {
    RESALE("Resale", "Earn money from sales"),
    DONATION("Donation", "Help families in need");

    val icon: String get() = when (this) {
        RESALE -> "💰"
        DONATION -> "❤️"
    }

    val color: Color get() = when (this) {
        RESALE -> Color(0xFF4CAF50)
        DONATION -> Color(0xFFE91E63)
    }
}
