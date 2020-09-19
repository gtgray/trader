package tk.atna.tradernet.network

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tk.atna.tradernet.stuff.CompressHelper
import java.util.*
import java.util.concurrent.TimeUnit

class ApiClient private constructor() {

    private lateinit var okHttpBuilder: OkHttpClient.Builder
    private lateinit var adapterBuilder: Retrofit.Builder
    private lateinit var retrofit: Retrofit

    companion object {
        @Volatile
        private var INSTANCE: ApiClient? = null

        private const val BASE_SOCKET_URL = "https://ws3.tradernet.ru"
        private const val BASE_SERVER_URL = "https://tradernet.ru/"
        private const val BASE_TICKER_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="


        fun getInstance(): ApiClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: init()
                    .also { apiClient -> INSTANCE = apiClient }
            }
        }

        private fun init() =
            ApiClient()
                .createAdapter()
                .enableLogging()

    }

    private fun createAdapter(): ApiClient {
        okHttpBuilder = OkHttpClient.Builder()
//                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
//                .writeTimeout(60000, TimeUnit.MILLISECONDS)
        //
        adapterBuilder = Retrofit.Builder()
            .baseUrl(BASE_SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(CompressHelper.moshi()).asLenient())
        //
        return this
    }

    private fun enableLogging(): ApiClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        okHttpBuilder.addInterceptor(logging)
        return this
    }

    fun <S> createService(serviceClass: Class<S>): S {
        retrofit = adapterBuilder
            .client(okHttpBuilder.build())
            .build()
        //
        return retrofit.create(serviceClass)
    }

    fun launchSocket(): Socket {
        val client = okHttpBuilder.build()
        IO.setDefaultOkHttpWebSocketFactory(client)
        IO.setDefaultOkHttpCallFactory(client)
        val opts = IO.Options()
        opts.transports = arrayOf(WebSocket.NAME)
        return IO.socket(BASE_SOCKET_URL, opts)
    }

    fun buildTickerUrl(tail: String?): String? {
        return tail?.let {
            "$BASE_TICKER_URL${tail.toLowerCase(Locale.US)}"
        }
    }

}
