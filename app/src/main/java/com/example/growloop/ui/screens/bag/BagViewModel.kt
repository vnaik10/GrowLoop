import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.growloop.ui.screens.Auth.model.BagCreateRequest
import com.example.growloop.ui.screens.Auth.model.BagResponseDTO
import com.example.growloop.ui.screens.Auth.model.ItemResponseDTO
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BagViewModel : ViewModel() {

    private val _bags = MutableStateFlow<List<BagResponseDTO>>(emptyList())
    val bags = _bags.asStateFlow()

    private val _currentBag = MutableStateFlow<BagResponseDTO?>(null)
    val currentBag = _currentBag.asStateFlow()

    private val _bagItems = MutableStateFlow<List<ItemResponseDTO>>(emptyList())
    val bagItems = _bagItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success = _success.asStateFlow()

    fun loadUserBags() {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid == null) {
            _error.value = "User not authenticated"
            return
        }

        _isLoading.value = true
        _error.value = null

        ApiClient.getUserBags(firebaseUid) { bags, errorMessage ->
            _isLoading.value = false

            if (bags != null) {
                _bags.value = bags
                Log.d("BagViewModel", "Loaded ${bags.size} bags")
            } else {
                _error.value = errorMessage ?: "Failed to load bags"
                Log.e("BagViewModel", "Error loading bags: $errorMessage")
            }
        }
    }

    fun createBag(bagName: String) {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid == null) {
            _error.value = "User not authenticated"
            return
        }

        if (bagName.isBlank()) {
            _error.value = "Bag name cannot be empty"
            return
        }

        _isLoading.value = true
        _error.value = null

        val request = BagCreateRequest(bagName = bagName.trim())

        ApiClient.createBag(firebaseUid, request) { bag, errorMessage ->
            _isLoading.value = false

            if (bag != null) {
                _success.value = "Bag created successfully!"
                Log.d("BagViewModel", "Created bag: ${bag.bagName}")
                // Reload bags to include the new one
                loadUserBags()
            } else {
                _error.value = errorMessage ?: "Failed to create bag"
                Log.e("BagViewModel", "Error creating bag: $errorMessage")
            }
        }
    }

    fun loadBagDetails(bagId: Long) {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid == null) {
            _error.value = "User not authenticated"
            return
        }

        _isLoading.value = true
        _error.value = null

        ApiClient.getBagById(bagId, firebaseUid) { bag, errorMessage ->
            _isLoading.value = false

            if (bag != null) {
                _currentBag.value = bag
                Log.d("BagViewModel", "Loaded bag details: ${bag.bagName}")
            } else {
                _error.value = errorMessage ?: "Failed to load bag details"
                Log.e("BagViewModel", "Error loading bag details: $errorMessage")
            }
        }
    }

    fun loadBagItems(bagId: Long) {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid == null) {
            _error.value = "User not authenticated"
            return
        }

        ApiClient.getBagItems(bagId, firebaseUid) { items, errorMessage ->
            if (items != null) {
                _bagItems.value = items
                Log.d("BagViewModel", "Loaded ${items.size} items for bag $bagId")
            } else {
                _error.value = errorMessage ?: "Failed to load bag items"
                Log.e("BagViewModel", "Error loading bag items: $errorMessage")
            }
        }
    }

    fun schedulePickup(bagId: Long) {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid == null) {
            _error.value = "User not authenticated"
            return
        }

        _isLoading.value = true
        _error.value = null

        ApiClient.schedulePickup(bagId, firebaseUid) { success, message ->
            _isLoading.value = false

            if (success) {
                _success.value = message ?: "Pickup scheduled successfully!"
                // Reload bag details to reflect status change
                loadBagDetails(bagId)
            } else {
                _error.value = message ?: "Failed to schedule pickup"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
}
