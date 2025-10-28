package com.example.growloop.ui.screens.bags

import BagPurpose
import BagResponseDTO
import BagViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

// ============= MY BAGS MAIN SCREEN =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBagsScreen(
    navController: NavHostController,
    bagViewModel: BagViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(BagTab.DONATION) }
    val bags by bagViewModel.bags
    val isLoading by bagViewModel.isLoading
    var showCreateBagDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bagViewModel.loadUserBags()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Bags",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { bagViewModel.loadUserBags() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateBagDialog = true },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Bag")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            // Tab Row
            BagTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                bags = bags
            )

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                when (selectedTab) {
                    BagTab.DONATION -> DonationBagsList(
                        bags = bags.filter { it.purpose == "DONATION" },
                        onBagClick = { bag ->
                            navController.navigate("bag_details/${bag.bagId}")
                        },
                        onCreateBag = { showCreateBagDialog = true }
                    )
                    BagTab.RESALE -> ResaleBagsList(
                        bags = bags.filter { it.purpose == "RESALE" },
                        onBagClick = { bag ->
                            navController.navigate("bag_details/${bag.bagId}")
                        },
                        onCreateBag = { showCreateBagDialog = true }
                    )
                }
            }
        }
    }

    // Create Bag Dialog
    if (showCreateBagDialog) {
        CreateBagDialog(
            onDismiss = { showCreateBagDialog = false },
            onCreateBag = { bagName, purpose ->
                bagViewModel.createBag(bagName, purpose)
                showCreateBagDialog = false
            }
        )
    }
}

// ============= BAG TAB ENUM =============
enum class BagTab(val title: String, val icon: ImageVector, val color: Color) {
    DONATION("Donation", Icons.Default.Favorite, Color(0xFFE91E63)),
    RESALE("Resale", Icons.Default.ShoppingBag, Color(0xFF4CAF50))
}

// ============= TAB ROW =============
@Composable
fun BagTabRow(
    selectedTab: BagTab,
    onTabSelected: (BagTab) -> Unit,
    bags: List<BagResponseDTO>
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BagTab.values().forEach { tab ->
                val bagCount = bags.count { it.purpose == tab.name }
                BagTabItem(
                    tab = tab,
                    isSelected = selectedTab == tab,
                    bagCount = bagCount,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BagTabItem(
    tab: BagTab,
    isSelected: Boolean,
    bagCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                tab.color.copy(alpha = 0.15f)
            else
                Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected)
            BorderStroke(2.dp, tab.color)
        else
            BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                tab.icon,
                contentDescription = null,
                tint = if (isSelected) tab.color else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                tab.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) tab.color else Color.Gray
            )
            Text(
                "$bagCount bags",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) tab.color.copy(alpha = 0.7f) else Color.Gray
            )
        }
    }
}

// ============= DONATION BAGS LIST =============
@Composable
fun DonationBagsList(
    bags: List<BagResponseDTO>,
    onBagClick: (BagResponseDTO) -> Unit,
    onCreateBag: () -> Unit
) {
    if (bags.isEmpty()) {
        EmptyBagsState(
            icon = "❤️",
            title = "No Donation Bags Yet",
            message = "Create a donation bag to help families in need",
            buttonText = "Create Donation Bag",
            color = Color(0xFFE91E63),
            onCreateBag = onCreateBag
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DonationStatsCard(bags = bags)
            }

            items(bags) { bag ->
                DonationBagCard(
                    bag = bag,
                    onClick = { onBagClick(bag) }
                )
            }
        }
    }
}

// ============= RESALE BAGS LIST =============
@Composable
fun ResaleBagsList(
    bags: List<BagResponseDTO>,
    onBagClick: (BagResponseDTO) -> Unit,
    onCreateBag: () -> Unit
) {
    if (bags.isEmpty()) {
        EmptyBagsState(
            icon = "💰",
            title = "No Resale Bags Yet",
            message = "Create a resale bag to earn money from your clothes",
            buttonText = "Create Resale Bag",
            color = Color(0xFF4CAF50),
            onCreateBag = onCreateBag
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ResaleStatsCard(bags = bags)
            }

            items(bags) { bag ->
                ResaleBagCard(
                    bag = bag,
                    onClick = { onBagClick(bag) }
                )
            }
        }
    }
}

// ============= DONATION BAG CARD =============
@Composable
fun DonationBagCard(
    bag: BagResponseDTO,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        bag.bagName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        bag.statusDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = Color(0xFFE91E63).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❤️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Donation",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${bag.totalItems} items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share button
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(bag.shareableUrl))
                        android.widget.Toast.makeText(context, "Link copied!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFFE91E63)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shareable link section
            Surface(
                color = Color(0xFFE91E63).copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Shareable Link",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            bag.sharableLink,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE91E63),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action indicator
            if (bag.status == "OPEN") {
                Surface(
                    color = Color(0xFFE91E63).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tap to add more items or schedule pickup",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE91E63)
                        )
                    }
                }
            }
        }
    }
}

// ============= RESALE BAG CARD =============
@Composable
fun ResaleBagCard(
    bag: BagResponseDTO,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        bag.bagName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        bag.statusDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💰", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Resale",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${bag.totalItems}/5 items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (bag.eligibleForFreePickup) "FREE Pickup Ready!" else "Add ${5 - bag.totalItems} more",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (bag.eligibleForFreePickup) Color(0xFF4CAF50) else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(bag.shareableUrl))
                            android.widget.Toast.makeText(context, "Link copied!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = (bag.totalItems.toFloat() / 5f).coerceAtMost(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (bag.eligibleForFreePickup)
                    Color(0xFF4CAF50)
                else
                    Color(0xFF2196F3),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Shareable link section
            Surface(
                color = Color(0xFF4CAF50).copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Shareable Link",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            bag.sharableLink,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pickup info
            Surface(
                color = if (bag.eligibleForFreePickup)
                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (bag.eligibleForFreePickup) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (bag.eligibleForFreePickup) Color(0xFF4CAF50) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            bag.pickupMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (bag.eligibleForFreePickup) Color(0xFF4CAF50) else Color.Gray
                        )
                    }

                    if (!bag.eligibleForFreePickup && bag.totalItems > 0) {
                        Text(
                            "₹${bag.pickupCost.toInt()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }

            // Points earned
            if (bag.pointsAwarded > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB000),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "+${bag.pointsAwarded} points earned",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFB000)
                    )
                }
            }
        }
    }
}

// ============= STATS CARDS =============
@Composable
fun DonationStatsCard(bags: List<BagResponseDTO>) {
    val totalItems = bags.sumOf { it.totalItems }
    val activeBags = bags.count { it.status == "OPEN" }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE91E63).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(
                icon = Icons.Default.Favorite,
                value = "${bags.size}",
                label = "Total Bags",
                color = Color(0xFFE91E63)
            )
            StatItem(
                icon = Icons.Default.CheckCircle,
                value = "$totalItems",
                label = "Items Donated",
                color = Color(0xFFE91E63)
            )
            StatItem(
                icon = Icons.Default.DateRange,
                value = "$activeBags",
                label = "Active",
                color = Color(0xFFE91E63)
            )
        }
    }
}

@Composable
fun ResaleStatsCard(bags: List<BagResponseDTO>) {
    val totalItems = bags.sumOf { it.totalItems }
    val totalPoints = bags.sumOf { it.pointsAwarded }
    val eligibleBags = bags.count { it.eligibleForFreePickup }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(
                icon = Icons.Default.ShoppingBag,
                value = "${bags.size}",
                label = "Total Bags",
                color = Color(0xFF4CAF50)
            )
            StatItem(
                icon = Icons.Default.Star,
                value = "$totalPoints",
                label = "Points",
                color = Color(0xFFFFB000)
            )
            StatItem(
                icon = Icons.Default.CheckCircle,
                value = "$eligibleBags",
                label = "Ready",
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

// ============= EMPTY STATE =============
@Composable
fun EmptyBagsState(
    icon: String,
    title: String,
    message: String,
    buttonText: String,
    color: Color,
    onCreateBag: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            icon,
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCreateBag,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                buttonText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ============= CREATE BAG DIALOG =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBagDialog(
    onDismiss: () -> Unit,
    onCreateBag: (String, BagPurpose) -> Unit
) {
    var bagName by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf<BagPurpose?>(null) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Create New Bag",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bag name input
                OutlinedTextField(
                    value = bagName,
                    onValueChange = {
                        bagName = it
                        showError = false
                    },
                    label = { Text("Bag Name") },
                    placeholder = { Text("e.g., Summer Clothes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError && bagName.isBlank(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Purpose selection
                Text(
                    "Select Purpose",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PurposeOption(
                        purpose = BagPurpose.DONATION,
                        isSelected = selectedPurpose == BagPurpose.DONATION,
                        onClick = { selectedPurpose = BagPurpose.DONATION },
                        modifier = Modifier.weight(1f)
                    )

                    PurposeOption(
                        purpose = BagPurpose.RESALE,
                        isSelected = selectedPurpose == BagPurpose.RESALE,
                        onClick = { selectedPurpose = BagPurpose.RESALE },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (showError && selectedPurpose == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please select a purpose",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (bagName.isNotBlank() && selectedPurpose != null) {
                                onCreateBag(bagName, selectedPurpose!!)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedPurpose?.color ?: Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
fun PurposeOption(
    purpose: BagPurpose,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                purpose.color.copy(alpha = 0.1f)
            else
                Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) purpose.color else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                purpose.icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                purpose.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) purpose.color else Color.Gray
            )
        }
    }
}