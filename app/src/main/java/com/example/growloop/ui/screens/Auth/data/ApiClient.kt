
import android.util.Log
import com.example.growloop.ui.screens.Auth.model.ApiResponse
import com.example.growloop.ui.screens.Auth.model.ItemCreateRequest
import com.example.growloop.ui.screens.Auth.model.ItemResponseDTO
import com.example.growloop.ui.screens.Auth.model.UserRegistrationRequest
import com.example.growloop.ui.screens.Auth.model.UserResponseDTO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/" // For emulator
    // For real device: "http://192.168.1.100:8080/api/" (use your PC's IP)

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    // User Registration
    fun registerUser(
        firebaseUid: String,
        request: UserRegistrationRequest,
        callback: (Boolean, String) -> Unit
    ) {
        val json = gson.toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${BASE_URL}users/register")
            .addHeader("Firebase-UID", firebaseUid)
            .post(requestBody)
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("ApiClient", "Register Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<UserResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<UserResponseDTO>>() {}.type
                        )
                        callback(apiResponse.success, apiResponse.message)
                    } catch (e: Exception) {
                        Log.e("ApiClient", "Parse error: ${e.message}")
                        callback(false, "Failed to parse response: ${e.message}")
                    }
                } else {
                    Log.e("ApiClient", "Register failed: ${response.code} ${response.message}")
                    callback(false, "Registration failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiClient", "Network error: ${e.message}")
                callback(false, "Network error: ${e.message}")
            }
        })
    }

    // Check User Exists
    fun checkUserExists(firebaseUid: String, callback: (Boolean) -> Unit) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}users/exists")
            .addHeader("Firebase-UID", firebaseUid)
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<Boolean> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<Boolean>>() {}.type
                        )
                        callback(apiResponse.data ?: false)
                    } catch (e: Exception) {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
        })
    }

    // Create Bag
// In ApiClient.kt
    fun createBag(
        firebaseUid: String,
        request: BagCreateRequest,  // ✅ Accept the request object directly
        callback: (BagResponseDTO?, String?) -> Unit
    ) {
        val json = gson.toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/create")
            .addHeader("Firebase-UID", firebaseUid)
            .post(requestBody)
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("ApiClient", "Create Bag Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<BagResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<BagResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        Log.e("ApiClient", "Parse error: ${e.message}")
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Create bag failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }


    // Get User's Bags
    fun getUserBags(
        firebaseUid: String,
        callback: (List<BagResponseDTO>?, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/my-bags")
            .addHeader("Firebase-UID", firebaseUid)
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("ApiClient", "Get Bags Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<List<BagResponseDTO>> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<List<BagResponseDTO>>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Get bags failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    // Get Bag by Share Token (Public - No Auth)
    fun getBagByShareToken(
        shareToken: String,
        callback: (BagResponseDTO?, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/share/${shareToken}")
            // No Firebase-UID header needed for public sharing
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<BagResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<BagResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Invalid or expired share link")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    // Add Item to Bag
    fun addItemToBag(
        bagId: Long,
        firebaseUid: String,
        request: ItemCreateRequest,
        callback: (ItemResponseDTO?, String?) -> Unit
    ) {
        val json = gson.toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${BASE_URL}items/bags/${bagId}")
            .addHeader("Firebase-UID", firebaseUid)
            .post(requestBody)
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<ItemResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<ItemResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Add item failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    // Get Bag Items - Fix the URL
    fun getBagItems(
        bagId: Long,
        firebaseUid: String,
        callback: (List<ItemResponseDTO>?, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}items/bags/${bagId}") // ✅ Fixed URL
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<List<ItemResponseDTO>> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<List<ItemResponseDTO>>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Get items failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    // Schedule Pickup - Fixed
    fun schedulePickup(
        bagId: Long,
        firebaseUid: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/${bagId}/schedule-pickup")
            .addHeader("Firebase-UID", firebaseUid)
            .post("".toRequestBody())
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: ApiResponse<BagResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<ApiResponse<BagResponseDTO>>() {}.type
                        )
                        callback(apiResponse.success, apiResponse.message)
                    } catch (e: Exception) {
                        callback(false, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(false, "Schedule pickup failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Network error: ${e.message}")
            }
        })
    }
}
