import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class BagViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _bags = mutableStateOf<List<BagResponseDTO>>(emptyList())
    val bags: State<List<BagResponseDTO>> = _bags

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _createBagSuccess = mutableStateOf(false)
    val createBagSuccess: State<Boolean> = _createBagSuccess

    init {
        loadUserBags()
    }

    fun loadUserBags() {
        val firebaseUid = auth.currentUser?.uid ?: return

        _isLoading.value = true
        _errorMessage.value = null

        ApiClient.getUserBags(firebaseUid) { bagsList, error ->
            viewModelScope.launch {
                _isLoading.value = false
                if (bagsList != null) {
                    _bags.value = bagsList
                } else {
                    _errorMessage.value = error ?: "Failed to load bags"
                }
            }
        }
    }

    fun createBag(bagName: String, purpose: BagPurpose) {
        val firebaseUid = auth.currentUser?.uid ?: return

        _isLoading.value = true
        _errorMessage.value = null


        val request = BagCreateRequest(
            bagName = bagName,
            purpose = purpose.toString()
        )

        // ✅ Pass only firebaseUid and the request object
        ApiClient.createBag(firebaseUid, request) { bag, error ->
            viewModelScope.launch {
                _isLoading.value = false
                if (bag != null) {
                    _createBagSuccess.value = true
                    loadUserBags() // Refresh the list
                } else {
                    _errorMessage.value = error ?: "Failed to create bag"
                }
            }
        }
    }


    fun schedulePickup(bagId: Long) {
        val firebaseUid = auth.currentUser?.uid ?: return

        ApiClient.schedulePickup(bagId, firebaseUid) { success, message ->
            viewModelScope.launch {
                if (success) {
                    loadUserBags() // Refresh the list
                } else {
                    _errorMessage.value = message ?: "Failed to schedule pickup"
                }
            }
        }
    }

    fun clearCreateSuccess() {
        _createBagSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
