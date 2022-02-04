package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi


import com.google.gson.GsonBuilder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RestClient {

    companion object {
        val apiType = "Android"
        val apiVersion = "1.0"
        val languageID = "1"

        //Live URI
        // var base = "http://3.6.67.109/"
        var base = "http://www.camfire.in/"
        //beta
        // var base = "http://betaapplication.com/camfire/"
        var base_url = base + "v1/"

        val image_base_url = "https://camfire.s3.ap-south-1.amazonaws.com/" + AWSConfiguration.mediaPath
        val image_base_url_post = "https://camfire-bkt.s3.ap-south-1.amazonaws.com/" + AWSConfiguration.mediaPath
        val image_base_url_signutureVideo = "https://camfire.s3.ap-south-1.amazonaws.com/signaturevideo/"
        val image_base_url_users = "https://camfire.s3.ap-south-1.amazonaws.com/" + AWSConfiguration.userPath
        val image_base_Level="https://camfire-admin.s3.ap-south-1.amazonaws.com/badges/"

        //  var image_base_Level =base+"backend/web/uploads/badgeImages/"
        /*var image_base_url_users = base + "backend/web/uploads/users/"
        var image_base_url_post = base + "backend/web/uploads/post/"*/

//      var image_base_url = "http://betaapplication.com/flymyown/backend/web/uploads/"
//      http://betaapplication.com/flymyown/backend/web/index.php/v1/apilog/file-upload-multiple

        var timthumb_path1 = image_base_url + "timthumb.php?src="
        var timthumb_path = image_base_url

        var googleBaseUrl = "https://maps.googleapis.com/maps/api/"
        var base_url_phase1 = base_url

        internal var REST_CLIENT: RestApi? = null

        private var restAdapter: Retrofit? = null
        internal var REST_GOOGLE_CLIENT: RestApi? = null
        private var restGoogleAdapter: Retrofit? = null

        init {
            setupRestClient()
            setupRestGoogleClient()
        }

        fun setupRestGoogleClient() {

            val gson = GsonBuilder().setLenient().create()
            restGoogleAdapter = Retrofit.Builder()
                .baseUrl(googleBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(
                    setOkHttpClientBuilder()
                        .build()
                )
                .build()
        }

        fun setOkHttpClientBuilder(): OkHttpClient.Builder {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(loggingInterceptor)
            builder.connectTimeout(300, TimeUnit.SECONDS)
            builder.readTimeout(80, TimeUnit.SECONDS)
            builder.writeTimeout(90, TimeUnit.SECONDS)
            return builder
        }


        /*var spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        var client = OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .build()*/

        fun setupRestClient() {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val builder = OkHttpClient.Builder()
            builder.addInterceptor(loggingInterceptor)
            builder.connectTimeout(300, TimeUnit.SECONDS)
            builder.readTimeout(80, TimeUnit.SECONDS)
            builder.writeTimeout(90, TimeUnit.SECONDS)
            val gson = GsonBuilder().setLenient().create()
            restAdapter = Retrofit.Builder()
                .baseUrl(base_url_phase1)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(
                    builder
                        .build()
                )
                .build()
        }


        fun get(): RestApi? {
            if (REST_CLIENT == null) {
                REST_CLIENT = restAdapter!!.create(RestApi::class.java)
            }
            return REST_CLIENT
        }

        fun getGoogle(): RestApi? {
            if (REST_GOOGLE_CLIENT == null) {
                REST_GOOGLE_CLIENT = restGoogleAdapter!!.create(
                    RestApi::class.java
                )

            }
            return REST_GOOGLE_CLIENT
        }

    }
}


