import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.growloop.ui.screens.Auth.model.BagResponseDTO
import com.example.growloop.ui.screens.bag.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BagsScreen(
    navController: NavController,
    viewModel: BagViewModel = viewModel()
) {
    val bags = listOf(
    BagResponseDTO(
        bagId = 1L,
        bagName = "Summer Clothes",
        sharableLinkToken = "abc123",
        status = "ACTIVE",
        createdAt = "2025-09-20T10:15:00",
        totalItems = 2,
        pointsAwarded = 120,
        deliveryCharge = 5.99,
        canAcceptItems = true,
        eligibleForFreePickup = false,
        pickupCost = 2.99,
        pickupMessage = "Pickup available within 2 days",
        creatorName = "Alice"
    ),
    BagResponseDTO(
        bagId = 2L,
        bagName = "Old Books",
        sharableLinkToken = "def456",
        status = "PENDING",
        createdAt = "2025-09-18T14:30:00",
        totalItems = 8,
        pointsAwarded = 80,
        deliveryCharge = 3.50,
        canAcceptItems = false,
        eligibleForFreePickup = true,
        pickupCost = 0.0,
        pickupMessage = "Eligible for free pickup!",
        creatorName = "Bob"
    ),
    BagResponseDTO(
        bagId = 3L,
        bagName = "Kitchen Essentials",
        sharableLinkToken = "ghi789",
        status = "COMPLETED",
        createdAt = "2025-09-15T09:00:00",
        totalItems = 20,
        pointsAwarded = 200,
        deliveryCharge = 7.49,
        canAcceptItems = false,
        eligibleForFreePickup = false,
        pickupCost = 3.50,
        pickupMessage = "Delivered successfully",
        creatorName = "Charlie"
    )
    )

    val isLoading by viewModel.isLoading.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf<BagResponseDTO?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadUserBags()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FFFE),
                        Color(0xFFE8F8F5)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Header
            EnhancedHeader(onBackClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            } )

            if (isLoading) {
                LoadingView()
            } else if (bags.isEmpty()) {
                EmptyBagsView(onCreateBag = { showCreateDialog = true })
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bags) { bag ->
                        EnhancedBagCard(
                            bag = bag,
                            onCardClick = {
                                navController.navigate("bag_details/${bag.bagId}")
                            },
                            onShareClick = {

                            },

                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Enhanced FAB
        EnhancedFloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showCreateDialog) {
        CreateBagDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { bagName ->
                coroutineScope.launch {
                    showCreateDialog = false
                    Toast.makeText(context, "Creating bag...", Toast.LENGTH_SHORT).show()
                    delay(1000)
                    Toast.makeText(context, "Bag created successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.loadUserBags()
                }
            }
        )
    }


}




@Composable
fun EnhancedBagCard(
    bag: BagResponseDTO,
    onCardClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var showShareDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Bag Name, Status, Share Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Bag Name
                Text(
                    text = bag.bagName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    EnhancedStatusBadge(status = bag.status)

                    // Share Button
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share bag",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Creator and Contributors Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Creator and Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = bag.creatorName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ${formatDate(bag.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E)
                    )
                }

                // Contributors Count
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Contributors",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2/5",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shareable Link Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Link,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "growloop.com/bag/${bag.sharableLinkToken}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = {
                            // Handle copy link logic
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy link",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items Count and Delivery Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Items Count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${bag.totalItems} items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                // Delivery Status
                if (bag.totalItems >= 5) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Free Delivery",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Add ${5 - bag.totalItems} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF6F00),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "for free delivery",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Schedule Pickup Button
            Button(
                onClick = {
                    // Handle schedule pickup logic
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (bag.totalItems >= 5) "Schedule Free Pickup" else "Schedule Pickup (₹${bag.deliveryCharge.toInt()})",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Share Dialog
    if (showShareDialog) {
        InviteUserDialog(
            bagName = bag.bagName,
            shareableLink = "growloop.com/bag/${bag.sharableLinkToken}",
            onDismiss = { showShareDialog = false },
            onInvite = {
                // Handle invite logic
                showShareDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteUserDialog(
    bagName: String,
    shareableLink: String,
    onDismiss: () -> Unit,
    onInvite: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Invite Contributors",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share \"$bagName\" with friends and neighbors to collect more donations together",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4A4A),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Shareable Link Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8F9FA),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Shareable Link",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shareableLink,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF757575),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Bag Link", shareableLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy link",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF757575)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFFE0E0E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Close",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT,
                                    "Join my donation bag \"$bagName\" and help collect clothes for those in need! $shareableLink")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                            onInvite()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Share",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "More contributors = Free delivery faster!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun EnhancedHeader(
    onBackClick: () -> Unit = {},
    onBagClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFFA483E0),
                        Color(0xFFEC4899)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .alpha(animatedAlpha)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Arrow
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                val gradientColors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFE8F5E8),
                    Color(0xFFC8E6C9),
                    Color(0xFFFFFFFF)
                )

                Text(
                    text = "My Donation Bags",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Manage and share your clothing donations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors.map { it.copy(alpha = 0.8f) }
                        )
                    )
                )
            }

            // Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onMenuClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }

    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF4CAF50),
        contentColor = Color.White,
        modifier = modifier
            .scale(scale)
            .size(64.dp)
            .shadow(12.dp, CircleShape),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Create Bag",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun EnhancedStatusBadge(status: String) {
    val (backgroundColor, textColor, text) = when (status.uppercase()) {
        "OPEN" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Open"
        )
        "AWAITING_PICKUP" -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            "Awaiting"
        )
        "COLLECTED" -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            "Collected"
        )
        "CLOSED" -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF616161),
            "Closed"
        )
        else -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            status
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CompactStatusBadge(status: String) {
    val (color, text, icon) = when (status.uppercase()) {
        "OPEN" -> Triple(Color(0xFF4CAF50), "Open", Icons.Default.FiberManualRecord)
        "AWAITING_PICKUP" -> Triple(Color(0xFFFF9800), "Awaiting", Icons.Default.Schedule)
        "COLLECTED" -> Triple(Color(0xFF2196F3), "Collected", Icons.Default.Done)
        "CLOSED" -> Triple(Color(0xFF9E9E9E), "Closed", Icons.Default.Block)
        else -> Triple(Color(0xFF757575), status, Icons.Default.Info)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DeliveryStatusChip(totalItems: Int) {
    val (backgroundColor, textColor, text, icon) = if (totalItems >= 5) {
        Quadruple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Color(0xFF2E7D32),
            "FREE Delivery! 🎉",
            Icons.Default.CheckCircle
        )
    } else {
        val itemsNeeded = 5 - totalItems
        Quadruple(
            Color(0xFFFF9800).copy(alpha = 0.1f),
            Color(0xFFE65100),
            "Add $itemsNeeded more",
            Icons.Default.Add
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
    }
}

// Helper data class for Quadruple
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
@Composable
fun PaytmStyleSuccessBadge() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Success",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun DeliveryStatusSection(
    totalItems: Int,
    eligibleForFreePickup: Boolean
) {
    val progress = minOf(totalItems.toFloat() / 5f, 1f)
    val itemsNeeded = maxOf(5 - totalItems, 0)

    Column {
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE8E8E8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        if (eligibleForFreePickup)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFFF9800)
                    )
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delivery Status Text
        if (eligibleForFreePickup) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🎉 Free Delivery Eligible!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        } else {
            Text(
                text = "Add $itemsNeeded more items for free delivery",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFF6F00),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun ShareableLinkSection(
    linkToken: String,
    onCopyClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Link,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Shareable Link",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "growloop.com/bag/$linkToken",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    // Implement copy logic here
                    onCopyClick()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy link",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressStatsSection(
    bag: BagResponseDTO,
    shimmer: Float
) {
    val progress = minOf(bag.totalItems.toFloat() / 5f, 1f)
    val isEligibleForFreePickup = bag.totalItems >= 5

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${bag.totalItems}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "/5 items",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF757575),
                    fontWeight = FontWeight.Medium
                )
            }

            if (isEligibleForFreePickup) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(shimmer)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FREE Delivery Eligible! 🎉",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            } else {
                val itemsNeeded = 5 - bag.totalItems
                Text(
                    text = "$itemsNeeded more items for FREE delivery",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFF6F00),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Points badge
        if (bag.pointsAwarded > 0) {
            EnhancedPointsBadge(points = bag.pointsAwarded)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Enhanced Progress Bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE8E8E8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    Brush.horizontalGradient(
                        colors = if (isEligibleForFreePickup) {
                            listOf(Color(0xFF4CAF50), Color(0xFF66BB6A), Color(0xFF81C784))
                        } else {
                            listOf(Color(0xFFFF9800), Color(0xFFFFB74D), Color(0xFFFFCC02))
                        }
                    )
                )
                .clip(RoundedCornerShape(6.dp))
        )

        // Progress indicator dot
        if (progress > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterStart)
                    .offset(x = (progress * (LocalContext.current.resources.displayMetrics.density * 280 - 8)).dp)
            )
        }
    }
}

@Composable
fun ActionButtonsSection(
    bag: BagResponseDTO,
    onCardClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (bag.canAcceptItems) {
            OutlinedButton(
                onClick = onCardClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4CAF50)
                ),
                border = BorderStroke(2.dp, Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Items",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            onClick = onCardClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0090FF)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Text(
                if (bag.canAcceptItems) "View Details" else "Manage",
                fontWeight = FontWeight.SemiBold,

            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EnhancedPointsBadge(points: Int) {
    Surface(
        color = Color(0xFFFFF8E1),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Points",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$points",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFFF8F00),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = " pts",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFF8F00),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun CreateBagDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var bagName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val isValidName = remember(bagName) {
        bagName.trim().length in 3..20
    }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Create New Bag",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Give your donation bag a memorable name.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedTextField(
                    value = bagName,
                    onValueChange = { bagName = it.take(20) },
                    label = { Text("Bag Name") },
                    placeholder = { Text("e.g. Summer Clothes") },
                    singleLine = true,
                    isError = bagName.isNotEmpty() && !isValidName,
                    supportingText = {
                        if (bagName.isNotEmpty() && !isValidName) {
                            Text("Must be 3-20 characters", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("${bagName.length}/20", color = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (isValidName) {
                                isLoading = true
                                onSave(bagName.trim())
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = isValidName && !isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Loading your bags...",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4A4A4A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please wait a moment",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun EmptyBagsView(onCreateBag: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Static illustration
        Box(
            modifier = Modifier.size(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4CAF50).copy(alpha = 0.15f),
                                Color(0xFF4CAF50).copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = "No bags",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "No donation bags yet",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF2E2E2E),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Start your donation journey by creating\nyour first bag to collect clothes",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF4A4A4A),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "• Organize donations by categories\n• Share with friends and family\n• Track progress towards free delivery",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onCreateBag,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Create Your First Bag",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Enhanced helper function to format date
fun formatDate(dateString: String): String {
    return try {
        val parts = dateString.split("T")[0].split("-")
        val day = parts[2]
        val month = when (parts[1]) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Aug"
            "09" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"
            else -> parts[1]
        }
        "$day $month"
    } catch (e: Exception) {
        "Recently"
    }
}