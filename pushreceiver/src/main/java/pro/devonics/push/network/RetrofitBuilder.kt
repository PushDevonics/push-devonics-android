package pro.devonics.push.network

import android.util.Base64
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.devonics.push.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "RetrofitBuilder"

object RetrofitBuilder {

    private val baseUrl = Base64.decode(BASE_URL, 8).decodeToString()

    val loggingInterceptor = HttpLoggingInterceptor()

    private val okhttpClient = OkHttpClient().newBuilder()
        .addInterceptor(loggingInterceptor)

    private fun getRetrofit(): Retrofit {
        Log.d(TAG, "getRetrofit:")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient.build())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}