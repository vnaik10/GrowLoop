package com.example.growloop.ui.screens.bag

import BagViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.growloop.ui.screens.Auth.model.BagResponseDTO
import com.example.growloop.ui.screens.Auth.model.BagStatus
import com.example.growloop.ui.screens.Auth.model.ItemResponseDTO

@Composable
fun BagDetailsScreen(
    bagId: Long,
    navController: NavController,
    viewModel: BagViewModel = viewModel()
) {
    val bag by viewModel.currentBag.collectAsState()
    val items by viewModel.bagItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(bagId) {
        viewModel.loadBagDetails(bagId)
        viewModel.loadBagItems(bagId)
    }

    // Handle success message
    LaunchedEffect(success) {
        if (success != null) {
            // Show toast or snackbar
            viewModel.clearSuccess()
        }
    }

    // Handle error message
    LaunchedEffect(error) {
        if (error != null) {
            // Show toast or snackbar
            viewModel.clearError()
        }
    }

    if (isLoading && bag == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        }
        return
    }

    bag?.let { currentBag ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Header
            BagDetailsHeader(
                bag = currentBag,
                onBackClick = { navController.popBackStack() },
                onShareClick = {
                    // TODO: Implement share functionality
                    // You can share the shareable link here
                }
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pickup Status Card
                item {
                    PickupStatusCard(
                        bag = currentBag,
                        isLoading = isLoading,
                        onSchedulePickup = {
                            viewModel.schedulePickup(currentBag.bagId)
                        }
                    )
                }

                // Items Section Header
                item {
                    ItemsSection(
                        items = items,
                        canAddItems = currentBag.canAcceptItems,
                        onAddItem = {
                            navController.navigate("add_item/${currentBag.bagId}")
                        }
                    )
                }

                // Items List
                if (items.isNotEmpty()) {
                    items(items) { item ->
                        ItemCard(item = item)
                    }
                }

                // Bag Info Card
                item {
                    BagInfoCard(bag = currentBag)
                }
            }
        }
    } ?: run {
        // Error state when bag is null
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFE57373)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bag not found",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Go Back")
            }
        }
    }
}

@Composable
fun PickupStatusCard(
    bag: BagResponseDTO,
    isLoading: Boolean = false,
    onSchedulePickup: () -> Unit
) {
    val isEligibleForFree = bag.totalItems >= 5
    val deliveryFee = if (isEligibleForFree) 0.0 else bag.deliveryCharge

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEligibleForFree) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isEligibleForFree) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isEligibleForFree) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isEligibleForFree) "FREE Pickup Available!" else "Pickup Available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEligibleForFree) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )

                    Text(
                        text = bag.pickupMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }

            if (!isEligibleForFree) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Delivery Fee:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )

                    Text(
                        text = "₹${deliveryFee.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show pickup button only if bag can be scheduled for pickup
            if (bag.status == "OPEN" && bag.totalItems > 0) {
                Button(
                    onClick = onSchedulePickup,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isEligibleForFree) "Schedule FREE Pickup" else "Schedule Pickup - ₹${deliveryFee.toInt()}"
                    )
                }
            } else {
                // Show status message for non-schedulable bags
                Surface(
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (bag.status) {
                            "AWAITING_PICKUP" -> "Pickup Scheduled"
                            "COLLECTED" -> "Items Collected"
                            "CLOSED" -> "Bag Closed"
                            else -> "Add items to schedule pickup"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun BagDetailsHeader(
    bag: BagResponseDTO,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column {
                    Text(
                        text = bag.bagName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${bag.totalItems} items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575)
                    )
                }
            }

            IconButton(onClick = onShareClick) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun PickupStatusCard(
    bag: BagResponseDTO,
    onSchedulePickup: () -> Unit
) {
    val isEligibleForFree = bag.totalItems >= 5
    val deliveryFee = if (isEligibleForFree) 0.0 else 50.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEligibleForFree) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isEligibleForFree) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isEligibleForFree) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isEligibleForFree) "FREE Pickup Available!" else "Pickup Available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEligibleForFree) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )

                    Text(
                        text = if (isEligibleForFree) {
                            "Your bag qualifies for free pickup"
                        } else {
                            "Add ${5 - bag.totalItems} more items for FREE pickup"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }

            if (!isEligibleForFree) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Delivery Fee:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )

                    Text(
                        text = "₹${deliveryFee.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSchedulePickup,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEligibleForFree) "Schedule FREE Pickup" else "Schedule Pickup - ₹${deliveryFee.toInt()}"
                )
            }
        }
    }
}

@Composable
fun ItemsSection(
    items: List<ItemResponseDTO>,
    canAddItems: Boolean,
    onAddItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items (${items.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (canAddItems) {
                    TextButton(
                        onClick = onAddItem,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item")
                    }
                }
            }

            if (items.isEmpty()) {
                Text(
                    text = "No items added yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ItemCard(item: ItemResponseDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.itemType,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E2E2E)
                )

                if (!item.conditionDescription.isNullOrBlank()) {
                    Text(
                        text = item.conditionDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!item.ageGroup.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = item.ageGroup,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }

                    if (!item.gender.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFF3E5F5),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = item.gender,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7B1FA2)
                            )
                        }
                    }
                }

                Text(
                    text = "Added by ${item.contributorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Grade badge
                Surface(
                    color = when (item.grade) {
                        "A" -> Color(0xFFE8F5E8)
                        "B" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Grade ${item.grade}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (item.grade) {
                            "A" -> Color(0xFF2E7D32)
                            "B" -> Color(0xFFE65100)
                            else -> Color(0xFF666666)
                        }
                    )
                }

                if (item.loyaltyPoint > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Points",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.loyaltyPoint.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BagInfoCard(bag: BagResponseDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Bag Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Created", formatDate(bag.createdAt))
            InfoRow("Status", BagStatus.fromString(bag.status).displayName)
            InfoRow("Creator", bag.creatorName)
            if (bag.pointsAwarded > 0) {
                InfoRow("Points Earned", "${bag.pointsAwarded} points")
            }

            if (bag.sharableLinkToken.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Shareable Link",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Share with neighbors within 1km",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }

                    TextButton(
                        onClick = {
                            // TODO: Copy link to clipboard or share
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper function for date formatting
fun formatDate(dateString: String): String {
    // For now, return a simple formatted date
    // You can enhance this with proper date parsing later
    return try {
        // Simple date formatting - you can improve this
        val parts = dateString.split("T")[0].split("-")
        if (parts.size >= 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}
