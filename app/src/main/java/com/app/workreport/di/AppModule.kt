package com.app.workreport.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.workreport.data.dao.ReportDao
import com.app.workreport.data.repository.ReportRepository
import com.app.workreport.data.database.ReportDatabase
import com.app.workreport.network.ApiService
import com.app.workreport.BuildConfig
import com.app.workreport.container.AppContainer
import com.app.workreport.network.AppUrl
import com.app.workreport.util.AppPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class )

object AppModule {
    /** for data base */
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context) :ReportDatabase=
        Room.databaseBuilder(context,
            ReportDatabase::class.java,"reportDatabase")
            .addMigrations(migration_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesReportDao(reportDatabase: ReportDatabase):ReportDao =
        reportDatabase.reportDao()

    @Provides
    fun providerReportRepository(reportDao: ReportDao):ReportRepository =
        ReportRepository(reportDao)


    @Provides
    @Singleton
    fun providesRetrofit():Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient(httpLoggingInterceptor(), cache(file(AppContainer.INSTANCE))))
            .build()

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit) :ApiService=
        retrofit.create(ApiService::class.java)


    private fun okHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG)
            builder.addInterceptor(httpLoggingInterceptor)
        return builder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            if (AppPref.isLoggedIn) { // add any condition here to add access token
                requestBuilder.addHeader(
                    AppUrl.HDR_AUTHORIZATION, AppPref.accessToken ?: ""
                )
            }
            Log.e("token", "${AppPref.accessToken}")
            val temp = requestBuilder.build()
            Log.d("OkHttp", "Headers: ${temp.headers}")
            chain.proceed(temp)
        }).cache(cache)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }
    private fun cache(file: File): Cache {
        return Cache(file, (10 * 1000 * 1000).toLong())
    }


    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private fun file(context: Context): File {
        val file = File(context.cacheDir, "HttpCache")
        file.mkdirs()
        return file
    }
    private val migration_1_2 =object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE work_plan_report ADD COLUMN isMobile BOOLEAN NOT NULL DEFAULT (FALSE)")
            //database.execSQL("ALTER TABLE work_plan_report ADD COLUMN commentBefore TEXT NOT NULL DEFAULT(test)")
           // database.execSQL("ALTER TABLE work_plan_report ADD COLUMN commentAtWork TEXT NOT NULL DEFAULT(test)")
           // database.execSQL("ALTER TABLE imageData ADD COLUMN commentAfter TEXT NOT NULL DEFAULT(test)")
           // database.execSQL("ALTER TABLE imageData ADD COLUMN commentBefore TEXT NOT NULL DEFAULT(test)")
           // database.execSQL("ALTER TABLE imageData ADD COLUMN commentAtWork TEXT NOT NULL DEFAULT(test)")
         //   database.execSQL(" TABLE imageData ADD COLUMN dateTime TEXT NOT NULL DEFAULT(01)")//Boolean
        }

    }
}


