package ru.vladik.kuber.api.kuber

import okhttp3.OkHttpClient
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vladik.kuber.utils.EnvVars
import ru.vladik.kuber.utils.getEnvVarOrEmpty
import ru.vladik.kuber.utils.getEnvVarOrThrow
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*


@Component
object KuberApiProvider {

    fun provide() : KuberApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(getEnvVarOrThrow(EnvVars.BASE_URL.name))
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(true).withToken())
            .build()
        return retrofit.create(KuberApi::class.java)
    }

    private fun OkHttpClient.withToken(): OkHttpClient {
        val token = getEnvVarOrEmpty(EnvVars.TOKEN.name)
        if (token.isEmpty()) return this
        return this.newBuilder().addInterceptor {
            val request = it.request().newBuilder().apply {
                addHeader("Authorization", "Bearer $token")
            }.build()
            return@addInterceptor it.proceed(request)
        }.build()
    }

    private fun getOkHttpClient(unsafeMode: Boolean): OkHttpClient {
        if (!unsafeMode) return OkHttpClient()
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }
                }
            )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(HostnameVerifier { hostname, session -> true }).build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}