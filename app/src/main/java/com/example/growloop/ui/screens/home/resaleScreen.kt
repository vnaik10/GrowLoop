package com.example.growloop.ui.screens.home

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
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController


// ============= MAIN RESALE SCREEN =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResaleScreen(
    navController: NavHostController,
    bagViewModel: BagViewModel = viewModel()
) {
    val bags by bagViewModel.bags
    val isLoading by bagViewModel.isLoading
    val context = LocalContext.current
    var showHowItWorks by remember { mutableStateOf(false) }
    var showCreateBagDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bagViewModel.loadUserBags()
    }

    val resaleBags = bags.filter { it.purpose == "RESALE" }
    val totalEarnings = resaleBags.sumOf { it.pointsAwarded }
    val readyBags = resaleBags.count { it.eligibleForFreePickup }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resale",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showHowItWorks = !showHowItWorks }) {
                        Icon(
                            if (showHowItWorks) Icons.Default.Close else Icons.Default.Info,
                            contentDescription = "How it works",
                            tint = Color(0xFF4CAF50)
                        )
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
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Bag")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hero Card
                    item {
                        HeroCard(
                            onCreateBag = { showCreateBagDialog = true }
                        )
                    }

                    // Stats Row
                    if (resaleBags.isNotEmpty()) {
                        item {
                            ResaleStatsCard(
                                totalBags = resaleBags.size,
                                totalEarnings = totalEarnings,
                                readyBags = readyBags
                            )
                        }
                    }

                    // How It Works (Expandable)
                    item {
                        AnimatedVisibility(
                            visible = showHowItWorks,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            HowItWorksCard()
                        }
                    }

                    // Section Header
                    if (resaleBags.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Your Resale Bags",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { /* View all */ }) {
                                    Text(
                                        "View All",
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }

                    // Resale Bags
                    if (resaleBags.isEmpty()) {
                        item {
                            EmptyResaleState(
                                onCreateBag = { showCreateBagDialog = true }
                            )
                        }
                    } else {
                        items(resaleBags) { bag ->
                            ResaleBagCard(
                                bag = bag,
                                onBagClick = {
                                    navController.navigate("bag_details/${bag.bagId}")
                                },
                                onAddItems = {
                                    navController.navigate("add_item/${bag.bagId}/${bag.bagName}")
                                },
                                onSchedulePickup = {
                                    bagViewModel.schedulePickup(bag.bagId)
                                },
                                onShare = {
                                    val shareIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "Join my resale bag: ${bag.bagName}\n" +
                                                    "Help me reach 5 items for FREE pickup!\n" +
                                                    "${bag.shareableUrl}"
                                        )
                                    }
                                    context.startActivity(
                                        android.content.Intent.createChooser(shareIntent, "Share Bag")
                                    )
                                }
                            )
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Create Bag Dialog
    if (showCreateBagDialog) {
        CreateResaleBagDialog(
            onDismiss = { showCreateBagDialog = false },
            onCreateBag = { bagName ->
                bagViewModel.createBag(bagName, BagPurpose.RESALE)
                showCreateBagDialog = false
            }
        )
    }
}

// ============= HERO CARD =============
@Composable
fun HeroCard(onCreateBag: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.1f),
                            Color(0xFF66BB6A).copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("💰", fontSize = 32.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            "Resale Made Easy",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "Earn money from clothes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            "you no longer need",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onCreateBag,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Resale Bag",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// ============= STATS CARD =============
@Composable
fun ResaleStatsCard(
    totalBags: Int,
    totalEarnings: Int,
    readyBags: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Your Impact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(
                    icon = Icons.Default.ShoppingBag,
                    value = "$totalBags",
                    label = "Bags",
                    color = Color(0xFF4CAF50)
                )

                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )

                StatColumn(
                    icon = Icons.Default.Star,
                    value = "₹$totalEarnings",
                    label = "Estimated",
                    color = Color(0xFFFFB000)
                )

                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )

                StatColumn(
                    icon = Icons.Default.CheckCircle,
                    value = "$readyBags",
                    label = "Ready",
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun StatColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
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

// ============= HOW IT WORKS CARD =============
@Composable
fun HowItWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "How Resale Works",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            listOf(
                HowItWorksStep("1️⃣", "Create bag & add items", "Add clothes you want to sell"),
                HowItWorksStep("2️⃣", "5+ items = FREE pickup", "Or pay ₹50 for pickup"),
                HowItWorksStep("3️⃣", "We QC & list for sale", "Quality check and photograph"),
                HowItWorksStep("4️⃣", "Earn money + points", "Get paid when items sell")
            ).forEach { step ->
                HowItWorksRow(step)
                if (step != listOf(
                        HowItWorksStep("1️⃣", "Create bag & add items", "Add clothes you want to sell"),
                        HowItWorksStep("2️⃣", "5+ items = FREE pickup", "Or pay ₹50 for pickup"),
                        HowItWorksStep("3️⃣", "We QC & list for sale", "Quality check and photograph"),
                        HowItWorksStep("4️⃣", "Earn money + points", "Get paid when items sell")
                    ).last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

data class HowItWorksStep(val emoji: String, val title: String, val description: String)

@Composable
fun HowItWorksRow(step: HowItWorksStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            step.emoji,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp)
        )

        Column {
            Text(
                step.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                step.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// ============= RESALE BAG CARD =============
@Composable
fun ResaleBagCard(
    bag: BagResponseDTO,
    onBagClick: () -> Unit,
    onAddItems: () -> Unit,
    onSchedulePickup: () -> Unit,
    onShare: () -> Unit
) {
    val borderColor = when {
        bag.eligibleForFreePickup -> Color(0xFF4CAF50)
        bag.totalItems > 0 -> Color(0xFFFF9800)
        else -> Color.LightGray.copy(alpha = 0.3f)
    }

    val backgroundColor = when {
        bag.status == "AWAITING_PICKUP" -> Color(0xFF4CAF50).copy(alpha = 0.05f)
        bag.status == "SCHEDULED" -> Color(0xFF2196F3).copy(alpha = 0.05f)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBagClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, borderColor)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = when (bag.status) {
                                "OPEN" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                "AWAITING_PICKUP" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                "SCHEDULED" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                else -> Color.Gray.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                bag.statusDisplayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = when (bag.status) {
                                    "OPEN" -> Color(0xFF4CAF50)
                                    "AWAITING_PICKUP" -> Color(0xFFFF9800)
                                    "SCHEDULED" -> Color(0xFF2196F3)
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
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

            // Progress Section
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

                Text(
                    if (bag.eligibleForFreePickup)
                        "FREE Pickup Ready!"
                    else
                        "Add ${5 - bag.totalItems} more",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (bag.eligibleForFreePickup)
                        Color(0xFF4CAF50)
                    else
                        Color(0xFFFF9800),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = (bag.totalItems.toFloat() / 5f).coerceAtMost(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = if (bag.eligibleForFreePickup)
                    Color(0xFF4CAF50)
                else
                    Color(0xFF2196F3),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
            Surface(
                color = if (bag.eligibleForFreePickup)
                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (bag.eligibleForFreePickup)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Info,
                            contentDescription = null,
                            tint = if (bag.eligibleForFreePickup)
                                Color(0xFF4CAF50)
                            else
                                Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            bag.pickupMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (bag.eligibleForFreePickup)
                                Color(0xFF4CAF50)
                            else
                                Color.Gray
                        )
                    }

                    if (!bag.eligibleForFreePickup && bag.totalItems > 0) {
                        Text(
                            "₹${bag.pickupCost.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }

            // Earnings Display
            if (bag.pointsAwarded > 0 || bag.totalItems >= 3) {
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
                        if (bag.pointsAwarded > 0)
                            "Earned: ₹${bag.pointsAwarded}"
                        else
                            "Est. Earning: ₹${bag.totalItems * 50}-${bag.totalItems * 100}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFB000)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (bag.status) {
                    "OPEN" -> {
                        OutlinedButton(
                            onClick = onAddItems,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4CAF50)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Items", fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2196F3)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF2196F3)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontWeight = FontWeight.Medium)
                        }

                        if (bag.totalItems > 0) {
                            Button(
                                onClick = onSchedulePickup,
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (bag.eligibleForFreePickup)
                                        Color(0xFF4CAF50)
                                    else
                                        Color(0xFF2196F3)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (bag.eligibleForFreePickup) "FREE Pickup" else "Pickup",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    "AWAITING_PICKUP", "SCHEDULED" -> {
                        Button(
                            onClick = onBagClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "View Details",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============= EMPTY STATE =============
@Composable
fun EmptyResaleState(onCreateBag: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("💰", fontSize = 64.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "No Resale Bags Yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Start earning money from clothes\nyou no longer need",
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
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Create First Resale Bag",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============= CREATE BAG DIALOG =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResaleBagDialog(
    onDismiss: () -> Unit,
    onCreateBag: (String) -> Unit
) {
    var bagName by remember { mutableStateOf("") }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("💰", fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Create Resale Bag",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = bagName,
                    onValueChange = {
                        bagName = it
                        showError = false
                    },
                    label = { Text("Bag Name") },
                    placeholder = { Text("e.g., Summer Collection") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError && bagName.isBlank(),
                    supportingText = if (showError && bagName.isBlank()) {
                        { Text("Please enter a bag name", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
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
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Add 5+ items for FREE pickup!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            if (bagName.isNotBlank()) {
                                onCreateBag(bagName)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
