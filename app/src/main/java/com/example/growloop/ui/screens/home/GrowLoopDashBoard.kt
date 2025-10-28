// Add all necessary imports at the top of your file
package com.example.responsivedashboard // Use your actual package name

import android.widget.MediaController
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.growloop.navigation.Pages
import com.example.growloop.ui.screens.Auth.AuthViewModel
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

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SustainableDashboard(navController: NavHostController) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBarWithDialogs() },
        bottomBar = {
            CurvedBottomNavigationBar(selectedNavIndex, { selectedNavIndex = it }, navigateTo = {route ->
                navController.navigate(route)
            })
        }

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),

            ) {

            item { EnhancedImpactStatsSection() }
            item { QuickActionsGrid(navController) }
            item { RecentActivitySection() }
        }

    }
}

// MODIFIED FOR RESPONSIVENESS
@Composable
fun QuickActionsGrid(navController: NavHostController) {
    val primary = MaterialTheme.colorScheme.primary
    val sec = MaterialTheme.colorScheme.secondary
    val quickActions = remember {
        listOf(
            QuickAction(
                "Give Clothes",
                "Donate or sell items",
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/tShirt_bykwob.png",
                Color.White,
                primary
            ),
            QuickAction(
                "Resale",
                "View listed items",
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/hanger_hklbmi.png",
                Color.White,
                sec
            ),
            QuickAction(
                "Wallet",
                "₹2,340 earned",
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/wallet_onyefm.png",
                Color.White,
                Color(0xFFF59E0B)
            ),
            QuickAction(
                "Recycle",
                "Track your progress",
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/earth_f7oivu.png",
                Color.White,
                Color(0xFF10B981)
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
            // Use Adaptive to automatically adjust column count based on screen width.
            columns = GridCells.Adaptive(minSize = 150.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            // Remove the fixed height to let the grid wrap its content.
            // Disable scrolling since it's nested inside a parent LazyColumn.
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp), // You can still keep a height if you want to cap the size
            userScrollEnabled = false
        ) {
            items(quickActions) { action ->
                QuickActionCard(action = action,navController)
            }
        }
    }
}

// MODIFIED FOR RESPONSIVENESS
@Composable
fun QuickActionCard(action: QuickAction, navController : NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 130.dp)
            .clickable {

                when(action.title) {
                "Give Clothes" -> navController.navigate(route = Pages.DONATE.name)
                    "Resale" -> navController.navigate(route = Pages.RESALE.name)
                }
            }
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

// MODIFIED FOR RESPONSIVENESS
@Composable
fun EnhancedImpactStatCard(
    stat: ImpactStat,
    index: Int
) {
    var hasAnimated by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (hasAnimated) stat.value else 0,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
    )
    val animatedProgress by animateFloatAsState(
        targetValue = if (hasAnimated) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    LaunchedEffect(Unit) {
        delay(300L + (index * 200L)) // Staggered animation
        hasAnimated = true
    }

    // Get screen width to make card size proportional
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = screenWidth * 0.45f // Card will be 45% of the screen width

    Card(
        // Use the calculated proportional width and an aspect ratio for height.
        modifier = Modifier
            .width(cardWidth)
            .aspectRatio(1.125f) // Maintains a 180:160 ratio
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
            // ... (rest of the card's inner content is the same)
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


// ---- NO CHANGES NEEDED FOR THE REST OF THE CODE ----
// ---- The following composables are already well-structured for responsiveness ----

@Composable
fun RecentActivitySection(authViewModel: AuthViewModel = viewModel()) {
    val recentActivities = remember {
        listOf(
            RecentActivity(
                "Donated 3 shirts",
                "Helped 2 families in need",
                "2 hours ago",
                Icons.Default.Favorite,
                Color(0xFFEF4444)
            ),
            RecentActivity(
                "Earned ₹450",
                "Sold vintage jacket",
                "1 day ago",
                Icons.Default.AccountBalanceWallet,
                Color(0xFF10B981)
            ),
            RecentActivity(
                "Impact milestone reached",
                "200+ items given new life!",
                "3 days ago",
                Icons.Default.EmojiEvents,
                Color(0xFFF59E0B)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
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
                onClick = { authViewModel.signOut() }
            ) {
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary // Using theme color
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
            .clickable {
            }
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
    val impactStats = remember {
        listOf(
            ImpactStat(
                "Clothes Given New Life",
                247,
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/shirts_gdloex.png",
                "Items donated & sold",
                Color(0xFF6366F1)
            ),
            ImpactStat(
                "Kids Supported",
                89,
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/clothing_pymkol.png",
                "Through donations",
                Color(0xFFEC4899)
            ),
            ImpactStat(
                "Textile Waste Saved",
                156,
                "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/earth_f7oivu.png",
                "Kg prevented from landfill",
                Color(0xFF10B981)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
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
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
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
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Trending up",
                                    tint = MaterialTheme.colorScheme.primary,
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
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
fun CurvedBottomNavigationBar(
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {},
    navigateTo: (String) -> Unit
){
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
                        Color(0xFF11998e),
                        Color(0xFF38ef7d)
                    )
                )
            )
    )
    {
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
                    onClick = { onItemSelected(index) },
                    navigateTo = { navigateTo(item.title) }
                )
            }
        }
    }
}

@Composable
fun CurvedNavigationItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    navigateTo: () -> Unit
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
            ) {
                onClick()
                navigateTo()
            },
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
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.title,
                            tint = Color(0xFF11998e),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = item.unselectedIcon,
                    contentDescription = item.title,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (!isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}


@Composable
fun TopBarSection(
    onNotificationClick: () -> Unit = {},
    onLoyaltyClick: () -> Unit = {},
) {
    val loyaltyPoints = 2909

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

            Column {
                val gradientColors = listOf(
                    Color(0xFFCACED0),
                    Color(0xFF00F2FE),
                    Color(0xFF43E97B),
                    Color(0xFFFEC163),
                )

                Text(
                    text = "Good ${getTimeOfDay()}!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                )


                Text(
                    text = "Welcome Back Vikas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                )
            }

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
                        ) { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }

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
                        .defaultMinSize(minWidth = 80.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1756909253/diamond_esqgvd.png",
                            contentDescription = "Loyalty Points",
                            modifier = Modifier
                                .size(20.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = formatPoints(loyaltyPoints),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

fun formatPoints(points: Int): String {
    return when {
        points >= 1_000_000 -> String.format("%.1fM", points / 1_000_000f).removeSuffix(".0")
        points >= 1_000 -> String.format("%.1fK", points / 1_000f).removeSuffix(".0")
        else -> points.toString()
    }
}

@Composable
fun TopBarWithDialogs() {
    var showLoyaltyDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    TopBarSection(
        onNotificationClick = { showNotificationDialog = true },
        onLoyaltyClick = { showLoyaltyDialog = true }
    )

    if (showLoyaltyDialog) {
        AlertDialog(
            onDismissRequest = { showLoyaltyDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Loyalty Points",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            },
            text = {
                Text(
                    text = "You have 2,909 points! Keep up the great work.",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showLoyaltyDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF6366F1)
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.Medium)
                }
            }
        )
    }

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
