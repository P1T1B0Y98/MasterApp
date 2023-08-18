import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.api.http.HttpResponse

class AuthorizationInterceptor(private val authManager: AuthManager) : HttpInterceptor {
    companion object {
        @Volatile
        private var token: String? = null

        fun updateToken(newToken: String?) {
            token = newToken
        }
    }

    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val token = authManager.getToken()

        val newRequest = request.newBuilder()
            .addHeader("Authorization", if (token != null) "Bearer $token" else "")
            .addHeader("Accept-Language", "en")
            .build()

        return chain.proceed(newRequest)
    }
}