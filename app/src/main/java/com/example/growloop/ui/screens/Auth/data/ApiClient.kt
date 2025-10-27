
import com.example.growloop.ui.screens.Auth.model.BagApiResponse
import com.example.growloop.ui.screens.Auth.model.BagCreateRequest
import com.example.growloop.ui.screens.Auth.model.BagResponseDTO
import com.example.growloop.ui.screens.Auth.model.ItemResponseDTO
import com.example.growloop.ui.screens.Auth.model.UserApiResponse
import com.example.growloop.ui.screens.Auth.model.UserRegistrationRequest
import com.example.growloop.ui.screens.Auth.model.UserResponseDTO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()


    private val gson = Gson()
    fun registerUser(
        firebaseUid: String,
        request: UserRegistrationRequest,
        callback: (Boolean, String) -> Unit
    ) {
        val json = gson.toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url("${BASE_URL}users/register")
            .addHeader("Content-Type", "application/json")
            .addHeader("Firebase-UID", firebaseUid)
            .post(requestBody)
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: UserApiResponse<UserResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<UserApiResponse<UserResponseDTO>>() {}.type
                        )
                        callback(apiResponse.success, apiResponse.message)
                    } catch (e: Exception) {
                        callback(false, "Failed to parse response: ${e.message}")
                    }
                } else {

                    callback(false, "Registration failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Network error: ${e.message}")
            }
        })
    }

    // Check if user exists in backend
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
                        val apiResponse: BagApiResponse<Boolean> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<Boolean>>() {}.type
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

    // Get user profile
    fun getUserProfile(firebaseUid: String, callback: (UserResponseDTO?, String?) -> Unit) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}users/profile")
            .addHeader("Firebase-UID", firebaseUid)
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: BagApiResponse<UserResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<UserResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Failed to get profile: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    fun createBag(
        firebaseUid: String,
        request: BagCreateRequest,
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

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: BagApiResponse<BagResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<BagResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
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

    // Get user's bags
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

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: BagApiResponse<List<BagResponseDTO>> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<List<BagResponseDTO>>>() {}.type
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

    // Get bag by ID
    fun getBagById(
        bagId: Long,
        firebaseUid: String,
        callback: (BagResponseDTO?, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/${bagId}")
            .addHeader("Firebase-UID", firebaseUid)
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: BagApiResponse<BagResponseDTO> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<BagResponseDTO>>() {}.type
                        )
                        callback(apiResponse.data, apiResponse.message)
                    } catch (e: Exception) {
                        callback(null, "Failed to parse response: ${e.message}")
                    }
                } else {
                    callback(null, "Get bag failed: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }
        })
    }

    // Get bag items
    fun getBagItems(
        bagId: Long,
        firebaseUid: String,
        callback: (List<ItemResponseDTO>?, String?) -> Unit
    ) {
        val httpRequest = Request.Builder()
            .url("${BASE_URL}bags/${bagId}/items")
            .addHeader("Firebase-UID", firebaseUid)
            .get()
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val apiResponse: BagApiResponse<List<ItemResponseDTO>> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<List<ItemResponseDTO>>>() {}.type
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

    // Schedule pickup
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
                        val apiResponse: BagApiResponse<Any> = gson.fromJson(
                            responseBody,
                            object : TypeToken<BagApiResponse<Any>>() {}.type
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
