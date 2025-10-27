package com.example.growloop.ui.screens.Auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.growloop.navigation.Pages
import kotlinx.coroutines.delay

@Composable
fun LoginPage(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    // State variables to hold the text from the input fields
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Error states
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Handle authentication state changes
    LaunchedEffect(key1 = authState.value) {
        when (val currentState = authState.value) {
            is AuthState.Authenticated -> {
                isLoading = false
                Toast.makeText(
                    context,
                    currentState.message ?: "Welcome back!",
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate(Pages.DASHBOARD.name) {
                    popUpTo(Pages.LOGIN.name) { inclusive = true }
                }
            }

            is AuthState.UnAuthenticated -> {
                isLoading = false
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute != Pages.LOGIN.name) {
                    navController.navigate(Pages.LOGIN.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is AuthState.ErrorMessage -> {
                isLoading = false
                Toast.makeText(
                    context,
                    currentState.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is AuthState.Loading -> {
                isLoading = true
            }

            else -> Unit
        }
    }

    // Auto-clear error states after delay
    LaunchedEffect(emailError, passwordError) {
        delay(3000)
        emailError = false
        passwordError = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main login card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFC1E794),
                            Color(0xFFBCE2BE)
                        )
                    )
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sign in to continue to GrowLoop",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        emailError = it.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    singleLine = true,
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email Icon"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = emailError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF508E60),
                        focusedLabelColor = Color(0xFF508E60),
                        focusedLeadingIconColor = Color(0xFF508E60)
                    ),
                    enabled = !isLoading
                )
                if (emailError) {
                    Text(
                        text = if (email.isBlank()) "Email is required" else "Please enter a valid email",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = it.isBlank()
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    isError = passwordError,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF508E60),
                        focusedLabelColor = Color(0xFF508E60),
                        focusedLeadingIconColor = Color(0xFF508E60)
                    ),
                    enabled = !isLoading
                )
                if (passwordError) {
                    Text(
                        text = "Password is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login Button
                Button(
                    onClick = {
                        emailError = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        passwordError = password.isBlank()

                        if (!emailError && !passwordError) {
                            isLoading = true
                            authViewModel.login(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF508E60)
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Signing In...")
                        }
                    } else {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Forgot Password (Optional)
                TextButton(
                    onClick = {
                        // Navigate to forgot password screen
                        // navController.navigate(Pages.FORGOT_PASSWORD.name)
                        Toast.makeText(context, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show()
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        "Forgot Password?",
                        color = Color(0xFF508E60),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Register redirect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    )
                    TextButton(
                        onClick = {
                            navController.navigate(Pages.REGISTER.name) {
                                popUpTo(Pages.REGISTER.name) { inclusive = true }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text(
                            "Sign Up",
                            color = Color(0xFF508E60),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
