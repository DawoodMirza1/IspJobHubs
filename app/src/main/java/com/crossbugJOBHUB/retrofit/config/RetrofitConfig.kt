package com.crossbugJOBHUB.retrofit.config

import android.content.Context
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.google.gson.GsonBuilder
import com.crossbugJOBHUB.commons.Keys
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


object RetrofitAPI {

    private const val BASE_URL = "http://arbazmateen.com/jobshubapi/"

    private const val API_KEY = "123456789"

    private val gson = GsonBuilder().setLenient().create()

    private val logging = HttpLoggingInterceptor()
    private val httpClient = OkHttpClient.Builder()

    init {

        logging.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(logging)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        httpClient.writeTimeout(30, TimeUnit.SECONDS)

    }

    fun getClient(): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()

    fun getHeaderClient(ctx: Context? = null): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->

                    val original = chain.request()

                    val requestBuilder = original.newBuilder()

                    requestBuilder.header("Accept", "application/json")
                    requestBuilder.header("User-Agent", "com.digitalmunshi.munshi")

                    if (ctx != null) {
                        val packageName = ctx.applicationContext.packageName
                        val applicationName = ctx.resources.getString(R.string.app_name)
                        val completeAppName = "$packageName.$applicationName"
                        requestBuilder.header("User-Agent", completeAppName)

                        val jwt by lazy {
                            Prefs(ctx).getString(Keys.AUTHORIZATION, "")
                        }

                        requestBuilder.header("Authorization", jwt)
                    }

                    val request = requestBuilder.method(original.method(), original.body()).build()

                    chain.proceed(request)
                }
                .build()

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
    }


    fun getAPIKeyClient(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->

                    val original = chain.request()
                    val originalUrl = original.url()

                    val url = originalUrl.newBuilder().addQueryParameter("apiKey", API_KEY).build()

                    val requestBuilder = original.newBuilder().url(url)

                    val request = requestBuilder.build()

                    chain.proceed(request)
                }
                .build()

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
    }

}

fun <T> response(call: Call<T>, onSuccess: ((T?) -> Unit), onFailure: ((errorMessage: String) -> Unit)) {
    try {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                when {
                    response.isSuccessful -> response.body()?.let {
                        onSuccess(it)
                    }
                    else -> {
                        onFailure(response.raw().message())
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    onFailure("Request time out! Slow Connection!")
                } else {
                    onFailure(t.message.toString())
                }
            }
        })
    } catch (e: IOException) {
        onFailure(e.message.toString())
    }
}
