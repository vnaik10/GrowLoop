package com.example.growloop.ui.screens.Auth

import ApiClient
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.growloop.ui.screens.Auth.model.UserRegistrationRequest
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState: MutableLiveData<AuthState> = MutableLiveData<AuthState>()
    val authState = _authState


    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.UnAuthenticated
        } else {
            _authState.value = AuthState.Authenticated("Registered")
        }
    }

    fun login(email: String, pass: String) {
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Authenticated("Login successful")
                Log.d("AuthViewModel", "User logged in successfully: ${auth.currentUser?.email}")
            } else {
                _authState.value = AuthState.ErrorMessage(
                    task.exception?.message ?: "Login failed"
                )
                Log.e("AuthViewModel", "Login failed: ${task.exception?.message}")
            }
        }
    }

    fun getCurrentUserInfo(): Pair<String?, String?> {
        val user = auth.currentUser
        return Pair(user?.email, user?.uid)
    }

    private fun registerUserWithBackend(userName: String) {
        val firebaseUser = auth.currentUser
        firebaseUser?.getIdToken(false)?.addOnSuccessListener { result ->
            val firebaseToken = result.token
            val firebaseUid = firebaseUser.uid
            val email = firebaseUser.email ?: ""

            val registrationRequest = UserRegistrationRequest(
                userName = userName,
                email = email,
                phoneNumber = firebaseUser.phoneNumber ?: "",
                addressText = null,
                latitude = null,
                longitude = null
            )

            ApiClient.registerUser(firebaseUid, registrationRequest) { success, message ->
                // Ensure we're updating on the main thread
                if (success) {
                    _authState.postValue(AuthState.Authenticated("Registration successful"))
                    Log.d("AuthViewModel", "User registered successfully: $message")
                } else {

                    firebaseUser.delete().addOnCompleteListener { deleteTask ->
                        Log.d(
                            "AuthViewModel",
                            "Firebase user deleted after backend registration failure"
                        )
                    }
                    // Post the error message
                    _authState.postValue(AuthState.ErrorMessage("Registration failed: $message"))
                    Log.e("AuthViewModel", "Registration failed: $message")
                }
            }
        }?.addOnFailureListener { exception ->
            // Handle token retrieval failure
            _authState.postValue(AuthState.ErrorMessage("Authentication token error: ${exception.message}"))
        }
    }


    fun signUp(userName: String, email: String, pass: String) {

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                registerUserWithBackend(userName)
            } else {
                _authState.value = AuthState.ErrorMessage(task.exception?.message ?: "Login Failed")
            }

        }

    }

    private fun syncUserWithBackEnd() {
        val firebaseUser = auth.currentUser
        firebaseUser?.getIdToken(false)?.addOnSuccessListener { result ->
            val firebaseToken = result.token
            val firebaseUid = firebaseUser.uid

            ApiClient.checkUserExists(firebaseUid) { exists ->
                if (!exists) {
                    registerUserWithBackend(firebaseUser.displayName ?: "User")
                } else {
                    Log.d("AuthViewModel", "User already exists in backend")
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}

sealed class AuthState {
    class Authenticated(val message : String) : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class ErrorMessage(val message: String) : AuthState()
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371 // km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}