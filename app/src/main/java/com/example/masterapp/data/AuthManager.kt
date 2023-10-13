import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson

// Define the keys for storing user and token in shared preferences
private const val KEY_USER = "User"
private const val KEY_TOKEN = "accessToken"
private const val ENCRYPTED_PREFS_NAME = "my_encrypted_prefs"

// Create a data class to represent the user profile
data class UserProfile(val id: String?, val authenticationUid: String?, val fullName: String?, val firstName: String?, val email: String)

// Create an object to manage authentication
object AuthManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
           Log.i("AuthManager", "Error initializing encrypted shared preferences: ${e.message}")
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun setToken(token: String?) {
        AuthorizationInterceptor.updateToken(token)
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        setToken(null)
        clearUserData()
    }

    fun clearUserData() {
        val userProfile = getUserProfile()
        if (userProfile != null) {
            val clearedUserProfile = UserProfile(
                id = null,
                authenticationUid = null,
                fullName = null,
                firstName = null,
                email = userProfile.email
            )
            setUserProfile(clearedUserProfile)
        }
    }

    fun setUserProfile(userProfile: UserProfile? = null) {
        sharedPreferences.edit()
            .putString(KEY_USER, Gson().toJson(userProfile))
            .apply()
    }

    fun getUserProfile(): UserProfile? {
        val userProfileJson = sharedPreferences.getString(KEY_USER, null)
        return Gson().fromJson(userProfileJson, UserProfile::class.java)
    }

    fun isSignedIn(): Boolean {
        return getUserProfile() != null
    }

    fun getUserName(): String? {
        val userProfile = getUserProfile()
        return userProfile?.fullName ?: userProfile?.firstName
    }

    fun getUserId(): String {
        return getUserProfile()?.id ?: ""
    }
}
