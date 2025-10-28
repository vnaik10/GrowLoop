import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ============= COLOR PALETTE =============
object DonationColors {
    val Primary = Color(0xFFE91E63)
    val PrimaryLight = Color(0xFFF8BBD0)
    val Secondary = Color(0xFF4CAF50)
    val Success = Color(0xFF4CAF50)
    val Background = Color(0xFFFAFAFA)
    val Surface = Color.White
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val Border = Color(0xFFE0E0E0)
    val Warning = Color(0xFFFFB000)
}

// ============= DATA CLASSES =============
data class NGO(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val address: String,
    val phone: String,
    val email: String,
    val acceptedItems: List<String>,
    val icon: String,
    val rating: Float = 4.5f,
    val distance: String = "2.5 km"
)

object NGOData {
    val ngoList = listOf(
        NGO(
            id = "ngo_1",
            name = "Seva Ashrama",
            description = "Supporting underprivileged children with clothing and education for a brighter future.",
            location = "Bangalore, Karnataka",
            address = "123 MG Road, Bangalore - 560001",
            phone = "+91 98765 43210",
            email = "contact@sevaashrama.org",
            acceptedItems = listOf("Kids Clothes", "Adult Clothes", "Winter Wear", "Uniforms"),
            icon = "🏠",
            rating = 4.8f,
            distance = "1.2 km"
        ),
        NGO(
            id = "ngo_2",
            name = "Hope Children's Home",
            description = "Loving care for 50+ children aged 2-16 years, providing shelter, education, and hope.",
            location = "Mumbai, Maharashtra",
            address = "456 Linking Road, Bandra, Mumbai - 400050",
            phone = "+91 98765 43211",
            email = "info@hopechildrenshome.org",
            acceptedItems = listOf("Kids Clothes", "School Uniforms", "Toys", "Books"),
            icon = "👶",
            rating = 4.9f,
            distance = "3.1 km"
        ),
        NGO(
            id = "ngo_3",
            name = "Shakti Women's Shelter",
            description = "Empowering women and providing safe shelter with dignity and support services.",
            location = "Delhi, NCR",
            address = "789 CP Block, Connaught Place, Delhi - 110001",
            phone = "+91 98765 43212",
            email = "support@shaktishelter.org",
            acceptedItems = listOf("Women's Clothes", "Kids Clothes", "Blankets", "Sarees"),
            icon = "💪",
            rating = 4.7f,
            distance = "4.6 km"
        ),
        NGO(
            id = "ngo_4",
            name = "Vriddha Seva Ashrama",
            description = "Caring for elderly people with love, dignity, and comprehensive health services.",
            location = "Chennai, Tamil Nadu",
            address = "321 Anna Salai, Chennai - 600002",
            phone = "+91 98765 43213",
            email = "care@vriddhashrama.org",
            acceptedItems = listOf("Adult Clothes", "Winter Wear", "Blankets", "Shawls"),
            icon = "👴",
            rating = 4.6f,
            distance = "2.8 km"
        ),
        NGO(
            id = "ngo_5",
            name = "Able Hearts Foundation",
            description = "Supporting differently-abled individuals and their families with adaptive resources.",
            location = "Hyderabad, Telangana",
            address = "654 Banjara Hills, Hyderabad - 500034",
            phone = "+91 98765 43214",
            email = "hello@ablehearts.org",
            acceptedItems = listOf("Adaptive Clothing", "Regular Clothes", "Accessories", "Shoes"),
            icon = "❤️",
            rating = 4.9f,
            distance = "5.2 km"
        )
    )

    fun getNGOById(id: String): NGO? = ngoList.find { it.id == id }
    fun getRandomNGO(): NGO = ngoList.random()
}

// ============= MAIN DONATION FLOW SCREEN =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(
    navController: NavHostController,
    bagViewModel: BagViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(1) }
    var selectedBag by remember { mutableStateOf<BagResponseDTO?>(null) }
    var selectedNGO by remember { mutableStateOf<NGO?>(null) }
    var donationNote by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newBagName by remember { mutableStateOf("") }
    var showRandomAnimation by remember { mutableStateOf(false) }

    val bags by bagViewModel.bags
    val isLoading by bagViewModel.isLoading
    val createBagSuccess by bagViewModel.createBagSuccess

    LaunchedEffect(createBagSuccess) {
        if (createBagSuccess) {
            bagViewModel.clearCreateSuccess()
            bagViewModel.loadUserBags()
        }
    }

    LaunchedEffect(showRandomAnimation) {
        if (showRandomAnimation) {
            delay(1500)
            showRandomAnimation = false
        }
    }

    Scaffold(
        containerColor = DonationColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            when (currentStep) {
                                1 -> "Select Bag"
                                2 -> "Choose NGO"
                                3 -> "Add Note"
                                4 -> "Review"
                                else -> "Success"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Step $currentStep of ${if (currentStep <= 4) 4 else 4}",
                            fontSize = 12.sp,
                            color = DonationColors.TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DonationColors.Surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentStep <= 4) {
                StepIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
                HorizontalDivider(color = DonationColors.Border.copy(alpha = 0.3f))
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(
                        slideOutHorizontally { -it } + fadeOut()
                    )
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    1 -> BagSelectionStep(
                        bags = bags.filter { it.isDonationBag() },
                        isLoading = isLoading,
                        onBagSelected = { selectedBag = it; currentStep = 2 },
                        onCreateNew = { showCreateDialog = true }
                    )
                    2 -> NGOSelectionStep(
                        selectedNGO = selectedNGO,
                        showRandomAnimation = showRandomAnimation,
                        onNGOSelected = { selectedNGO = it },
                        onRandomClick = { showRandomAnimation = true; selectedNGO = NGOData.getRandomNGO() },
                        onContinue = { if (selectedNGO != null) currentStep = 3 }
                    )
                    3 -> DonationNoteStep(
                        selectedNGO = selectedNGO!!,
                        donationNote = donationNote,
                        onNoteChange = { donationNote = it },
                        onContinue = { currentStep = 4 }
                    )
                    4 -> ReviewStep(
                        bag = selectedBag!!,
                        ngo = selectedNGO!!,
                        note = donationNote,
                        onConfirm = { currentStep = 5 }
                    )
                    5 -> SuccessStep(onBackToHome = { navController.popBackStack() })
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Donation Bag", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Text("Enter a name for your donation bag:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newBagName,
                        onValueChange = { newBagName = it },
                        placeholder = { Text("e.g., Winter Clothes Donation", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DonationColors.Primary,
                            focusedLabelColor = DonationColors.Primary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBagName.isNotBlank()) {
                            bagViewModel.createBag(newBagName, BagPurpose.DONATION)
                            showCreateDialog = false
                            newBagName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary)
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false; newBagName = "" }) {
                    Text("Cancel", color = DonationColors.TextSecondary)
                }
            }
        )
    }
}

// ============= STEP INDICATOR =============
@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = when {
                        step < currentStep -> DonationColors.Success
                        step == currentStep -> DonationColors.Primary
                        else -> DonationColors.Border
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (step < currentStep) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                "$step",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (step == currentStep) Color.White else DonationColors.TextSecondary
                            )
                        }
                    }
                }

                if (step < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .padding(horizontal = 6.dp)
                            .background(
                                if (step < currentStep) DonationColors.Success else DonationColors.Border,
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
    }
}

// ============= STEP 1: BAG SELECTION =============
@Composable
fun BagSelectionStep(
    bags: List<BagResponseDTO>,
    isLoading: Boolean,
    onBagSelected: (BagResponseDTO) -> Unit,
    onCreateNew: () -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DonationColors.Primary)
        }
    } else if (bags.isEmpty()) {
        EmptyBagsState(onCreateBag = onCreateNew)
    } else {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
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
                                "Select Donation Bag",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = DonationColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Choose from your bags or create new",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DonationColors.TextSecondary
                            )
                        }
                        FilledTonalButton(
                            onClick = onCreateNew,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = DonationColors.Primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            items(bags) { bag ->
                DonationBagCard(bag = bag, onClick = { onBagSelected(bag) })
            }
        }
    }
}

@Composable
fun DonationBagCard(bag: BagResponseDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DonationColors.Border.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(10.dp),
                color = bag.getPurposeColor().copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (bag.isDonationBag()) {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dbpdnuuog/image/upload/v1761632197/online-shopping_vmb6qi.png",
                            contentDescription = "Bag Icon",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(bag.getPurposeIcon(), fontSize = 26.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bag.bagName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DonationColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${bag.totalItems} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonationColors.TextSecondary
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = DonationColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmptyBagsState(onCreateBag: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("📦", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "No Donation Bags Yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = DonationColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Create your first donation bag",
                style = MaterialTheme.typography.bodyMedium,
                color = DonationColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreateBag,
                colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Bag")
            }
        }
    }
}

// ============= STEP 2: NGO SELECTION =============
@Composable
fun NGOSelectionStep(
    selectedNGO: NGO?,
    showRandomAnimation: Boolean,
    onNGOSelected: (NGO) -> Unit,
    onRandomClick: () -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Choose NGO Partner",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = DonationColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select a trusted NGO for your donation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DonationColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        FilledTonalButton(
                            onClick = onRandomClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = DonationColors.Secondary.copy(alpha = 0.1f),
                                contentColor = DonationColors.Secondary
                            )
                        ) {
                            if (showRandomAnimation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DonationColors.Secondary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Shuffle, null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find Nearby NGO", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            items(NGOData.ngoList) { ngo ->
                NGOCard(ngo = ngo, isSelected = selectedNGO?.id == ngo.id, onSelect = { onNGOSelected(ngo) })
            }
        }

        AnimatedVisibility(
            visible = selectedNGO != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Surface(shadowElevation = 8.dp, color = DonationColors.Surface) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun NGOCard(ngo: NGO, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DonationColors.Primary.copy(alpha = 0.05f) else DonationColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) DonationColors.Primary else DonationColors.Border.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = DonationColors.Primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(ngo.icon, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        ngo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DonationColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = DonationColors.Success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Verified",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = DonationColors.Success,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                AnimatedVisibility(visible = isSelected, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                    Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = DonationColors.Primary) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                ngo.description,
                style = MaterialTheme.typography.bodySmall,
                color = DonationColors.TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = DonationColors.TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${ngo.location} • ${ngo.distance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DonationColors.TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = DonationColors.Warning, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "${ngo.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = DonationColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ngo.acceptedItems) { item ->
                    Surface(
                        color = DonationColors.Border.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = DonationColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ============= STEP 3: DONATION NOTE =============
@Composable
fun DonationNoteStep(
    selectedNGO: NGO,
    donationNote: String,
    onNoteChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val maxLength = 500

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = DonationColors.Primary.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(selectedNGO.icon, fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "Donating to",
                                style = MaterialTheme.typography.labelMedium,
                                color = DonationColors.TextSecondary
                            )
                            Text(
                                selectedNGO.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DonationColors.TextPrimary
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Add Personal Note (Optional)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DonationColors.TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = donationNote,
                            onValueChange = { if (it.length <= maxLength) onNoteChange(it) },
                            placeholder = { Text("Share your message...", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DonationColors.Primary,
                                focusedLabelColor = DonationColors.Primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${donationNote.length}/$maxLength",
                                style = MaterialTheme.typography.bodySmall,
                                color = DonationColors.TextSecondary
                            )

                            if (donationNote.isNotEmpty()) {
                                TextButton(onClick = { onNoteChange("") }, contentPadding = PaddingValues(0.dp)) {
                                    Text("Clear", color = DonationColors.Primary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = DonationColors.Primary.copy(alpha = 0.03f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DonationColors.Primary.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Example Messages",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = DonationColors.Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            "These are my daughter's clothes that she outgrew. Hope they help!",
                            "Warm winter clothes for the upcoming season. Stay cozy!",
                            "Thank you for the amazing work you do."
                        ).forEach { example ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onNoteChange(example) },
                                color = DonationColors.Surface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    example,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp),
                                    color = DonationColors.TextSecondary,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(shadowElevation = 8.dp, color = DonationColors.Surface) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Review Donation", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ============= STEP 4: REVIEW =============
@Composable
fun ReviewStep(bag: BagResponseDTO, ngo: NGO, note: String, onConfirm: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = DonationColors.Primary.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎁", fontSize = 32.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Review Your Donation",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = DonationColors.TextPrimary
                        )
                        Text(
                            "Please verify the details below",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DonationColors.TextSecondary
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Donation Bag",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = DonationColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = bag.getPurposeColor().copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(bag.getPurposeIcon(), fontSize = 24.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    bag.bagName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DonationColors.TextPrimary
                                )
                                Text(
                                    "${bag.totalItems} items",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DonationColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Recipient NGO",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = DonationColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = DonationColors.Primary.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(ngo.icon, fontSize = 24.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    ngo.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DonationColors.TextPrimary
                                )
                                Text(
                                    ngo.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DonationColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            if (note.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Message,
                                    null,
                                    tint = DonationColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Your Message",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = DonationColors.TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DonationColors.TextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = DonationColors.Success.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DonationColors.Success.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                tint = DonationColors.Success,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Next Steps",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = DonationColors.Success
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            "NGO will contact you within 24 hours",
                            "Schedule a convenient pickup time",
                            "Items collected from your location",
                            "Receive donation confirmation"
                        ).forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(28.dp),
                                    shape = CircleShape,
                                    color = DonationColors.Success.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DonationColors.Success
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DonationColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(shadowElevation = 8.dp, color = DonationColors.Surface) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Donation", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ============= STEP 5: SUCCESS =============
@Composable
fun SuccessStep(onBackToHome: () -> Unit) {
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DonationColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            AnimatedVisibility(
                visible = showAnimation,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DonationColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = DonationColors.Success.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DonationColors.Success,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "Donation Confirmed!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DonationColors.Success
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Thank you for your generous donation. The NGO will contact you soon.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DonationColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = DonationColors.Primary.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💝", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Your Impact",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DonationColors.Primary
                                    )
                                    Text(
                                        "Helping families in need",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DonationColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showAnimation,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = DonationColors.Primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Home, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back to Home", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }
    }
}