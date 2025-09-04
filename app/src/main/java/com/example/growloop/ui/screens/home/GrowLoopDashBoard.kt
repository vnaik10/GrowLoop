@file:OptIn(ExperimentalMaterial3Api::class)

package com.growloop.dashboard


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.growloop.ui.theme.growLoopColors
import kotlinx.coroutines.delay
import java.util.Calendar

// Data Classes
data class ImpactStat(
    val title: String,
    val value: Int,
    val icon: String,
    val description: String,
    val color: Color
)

data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: String,
    val color: Color,
    val backgroundColor: Color
)

data class RecentActivity(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SustainableDashboard() {
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { CurvedBottomNavigationBar(selectedNavIndex) { selectedNavIndex = it } }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            item { TopBarWithDialogs() }
            item { EnhancedImpactStatsSection() }
            item { QuickActionsGrid() }
            item { RecentActivitySection() }
        }

    }
}

@Composable
fun QuickActionsGrid() {
    val primary = MaterialTheme.colorScheme.primary
    val sec = MaterialTheme.colorScheme.secondary
    val quickActions = remember {

        listOf(
            QuickAction(
                title = "Give Clothes",
                subtitle = "Donate or sell items",
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/tShirt_bykwob.png",
                color = Color.White,
                backgroundColor = primary
            ),
            QuickAction(
                title = "My Closet",
                subtitle = "View listed items",
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/hanger_hklbmi.png",
                color = Color.White,
                backgroundColor = sec
            ),
            QuickAction(
                title = "Wallet",
                subtitle = "₹2,340 earned",
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/wallet_onyefm.png",
                color = Color.White,
                backgroundColor = Color(0xFFF59E0B)
            ),
            QuickAction(
                title = "Impact Journey",
                subtitle = "Track your progress",
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/earth_f7oivu.png",
                color = Color.White,
                backgroundColor = Color(0xFF10B981)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(280.dp)
        ) {
            items(quickActions) { action ->
                QuickActionCard(action = action)
            }
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { /* TODO: Handle action */ }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = action.backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(action.icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = action.title,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Navigate",
                    tint = action.color.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = action.color
                )
                Text(
                    text = action.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = action.color.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun RecentActivitySection() {
    val recentActivities = remember {
        listOf(
            RecentActivity(
                title = "Donated 3 shirts",
                description = "Helped 2 families in need",
                time = "2 hours ago",
                icon = Icons.Default.Favorite,
                color = Color(0xFFEF4444)
            ),
            RecentActivity(
                title = "Earned ₹450",
                description = "Sold vintage jacket",
                time = "1 day ago",
                icon = Icons.Default.AccountBalanceWallet,
                color = Color(0xFF10B981)
            ),
            RecentActivity(
                title = "Impact milestone reached",
                description = "200+ items given new life!",
                time = "3 days ago",
                icon = Icons.Default.EmojiEvents,
                color = Color(0xFFF59E0B)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(
                onClick = { /* TODO: View all activities */ }
            ) {
                Text(
                    text = "View All",
                    color = MaterialTheme.growLoopColors.brandGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                recentActivities.forEachIndexed { index, activity ->
                    RecentActivityItem(
                        activity = activity,
                        showDivider = index < recentActivities.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivityItem(
    activity: RecentActivity,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: View activity details */ }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(activity.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = activity.title,
                    tint = activity.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = activity.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showDivider) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
}


// Helper function to get time of day
@Composable
private fun getTimeOfDay(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (currentHour) {
        in 5..11 -> "Morning"
        in 12..17 -> "Afternoon"
        in 18..21 -> "Evening"
        else -> "Night"
    }
}

@Composable
fun EnhancedImpactStatsSection() {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val impactStats = remember {
        listOf(
            ImpactStat(
                title = "Clothes Given New Life",
                value = 247,
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/shirts_gdloex.png",
                description = "Items donated & sold",
                color = Color(0xFF6366F1) // Indigo
            ),
            ImpactStat(
                title = "Kids Supported",
                value = 89,
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/clothing_pymkol.png",
                description = "Through donations",
                color = Color(0xFFEC4899) // Pink
            ),
            ImpactStat(
                title = "Textile Waste Saved",
                value = 156,
                icon = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/earth_f7oivu.png",
                description = "Kg prevented from landfill",
                color = Color(0xFF10B981) // Emerald
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Enhanced Section Header
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.growLoopColors.brandGreen.copy(alpha = 0.1f),
                                MaterialTheme.growLoopColors.brandGreenDark.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.growLoopColors.brandGreen.copy(alpha = 0.15f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Trending up",
                                    tint = MaterialTheme.growLoopColors.brandGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Your Impact Journey",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Making a difference, one item at a time",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Progress Indicator
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.growLoopColors.brandGreen.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.growLoopColors.brandGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarRate,
                                contentDescription = "Star",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )

                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Enhanced Stats Cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(impactStats.size) { index ->
                val stat = impactStats[index]
                EnhancedImpactStatCard(
                    stat = stat,
                    index = index
                )
            }
        }
    }
}

@Composable
fun EnhancedImpactStatCard(
    stat: ImpactStat,
    index: Int
) {
    var hasAnimated by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (hasAnimated) stat.value else 0,
        animationSpec = tween(
            durationMillis = 800, // Reduced from 1500ms
            easing = LinearOutSlowInEasing // Less CPU intensive
        )
    )

    val animatedProgress by animateFloatAsState(
        targetValue = if (hasAnimated) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )

    LaunchedEffect(Unit) {
        delay(300L + (index * 200L)) // Staggered animation
        hasAnimated = true
    }

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = stat.color.copy(alpha = 0.4f),
                ambientColor = stat.color.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            stat.color.copy(alpha = 0.05f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Decorative background elements
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                drawCircle(
                    color = stat.color.copy(alpha = 0.1f),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.8f, -20.dp.toPx())
                )
                drawCircle(
                    color = stat.color.copy(alpha = 0.05f),
                    radius = 60.dp.toPx(),
                    center = Offset(-10.dp.toPx(), size.height * 0.7f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section with icon and progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = animatedValue.toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp
                            ),
                            color = stat.color
                        )

                        // Progress bar
                        Card(
                            modifier = Modifier
                                .width(60.dp)
                                .height(4.dp),
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = stat.color.copy(alpha = 0.2f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                stat.color,
                                                stat.color.copy(alpha = 0.8f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    // Enhanced Icon
                    Card(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = stat.color.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(2.dp, stat.color.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(stat.icon)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stat.title,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                // Bottom section with text
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stat.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Achievement indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(3) { starIndex ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (starIndex < 2) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = when (index) {
                                0 -> "Excellent!"
                                1 -> "Amazing!"
                                else -> "Outstanding!"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = stat.color
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CompactImpactCard(stat: ImpactStat) {
    var hasAnimated by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (hasAnimated) stat.value else 0,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "compact_counter_animation"
    )

    LaunchedEffect(Unit) {
        delay(200)
        hasAnimated = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = stat.color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, stat.color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animatedValue.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = stat.color
                )
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = stat.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(stat.icon)
                    .crossfade(true)
                    .build(),
                contentDescription = stat.title,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(stat.color.copy(alpha = 0.2f), CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)


// Curved Bottom Navigation (Most Creative)
@Composable
fun CurvedBottomNavigationBar(
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    val navItems = listOf(
        NavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem(
            "MyBag",
            Icons.Filled.ShoppingBag,
            Icons.Outlined.ShoppingBag,
            badgeCount = 3
        ),
        NavigationItem("Shop", Icons.Filled.Store, Icons.Outlined.Store),
        NavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false
            )
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF11998e), // ← CHANGED: Deep teal
                        Color(0xFF38ef7d)  // ← CHANGED: Bright green
                    )
                )
            )
    )
    {
        // Navigation items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                CurvedNavigationItem(
                    item = item,
                    isSelected = index == selectedIndex,
                    isCenter = index == 1 || index == 2,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }
}

@Composable
fun CurvedNavigationItem(
    item: NavigationItem,
    isSelected: Boolean,
    isCenter: Boolean,
    onClick: () -> Unit
) {
    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) -8f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "curve_offset"
    )

    Column(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier.size(46.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f) // ← CHANGED
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.title,
                            tint = Color(0xFF11998e), // ← CHANGED
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = item.unselectedIcon,
                    contentDescription = item.title,
                    tint = Color.White.copy(alpha = 0.8f), // ← CHANGED
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Text label only when unselected
        if (!isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.9f) // ← CHANGED
            )
        }
    }
}


@Composable
fun ImpactStatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        )
    }
}


@Composable
fun TopBarSection(
    name: String = "Vikas",
    onNotificationClick: () -> Unit = {},
    onLoyaltyClick: () -> Unit = {}
) {
    val loyaltyPoints = 275

    // Animation values
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
    )



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6366F1), // Indigo
                        Color(0xFF8B5CF6), // Purple
                        Color(0xFFEC4899)  // Pink
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .alpha(animatedAlpha)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Left side - Greeting
            Column {
                Text(
                    text = "Good ${getTimeOfDay()}!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "Welcome back, $name",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Right side - Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Notification Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // let Material3 apply its own ripple
                        ) { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    // Notification badge (optional)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }

                // Loyalty Points Floating Action Button
                FloatingActionButton(
                    onClick = onLoyaltyClick,
                    containerColor = Color.White,
                    contentColor = Color(0xFF6366F1),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    modifier = Modifier
                        .height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        // Diamond logo
                        AsyncImage(
                            model = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/diamond_esqgvd.png",
                            contentDescription = "Loyalty Points",
                            modifier = Modifier
                                .size(20.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = loyaltyPoints.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF6366F1)
                        )
                    }
                }
            }
        }
    }
}


// Usage with dialogs for interactions
@Composable
fun TopBarWithDialogs() {
    var showLoyaltyDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    TopBarSection(
        onNotificationClick = { showNotificationDialog = true },
        onLoyaltyClick = { showLoyaltyDialog = true }
    )

    // Loyalty Points Dialog
    if (showLoyaltyDialog) {
        AlertDialog(
            onDismissRequest = { showLoyaltyDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Notifications",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            },
            text = {
                Text(
                    text = "No new notifications at the moment.",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.Medium)
                }
            }
        )

    }

    // Notifications Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Notifications",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            },
            text = {
                Text(
                    text = "No new notifications at the moment.",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.Medium)
                }
            }
        )

    }
}
