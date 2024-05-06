package com.app.workreport.container

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ViewModel
import androidx.work.Configuration
import com.google.gson.Gson
import com.app.workreport.network.ApiService
import com.app.workreport.util.Event
import com.squareup.picasso.Picasso
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppContainer : Application(),Configuration.Provider {
    lateinit var picasso: Picasso
    lateinit var gson: Gson
    lateinit var api: ApiService

    companion object {
        lateinit var INSTANCE: AppContainer
    }

    override fun onCreate() {
        super.onCreate()
        // ALL INITIALIZING THING GOES HERE
        INSTANCE = this
        initAppDependencies()

      //  AppPref.routineFilter=null
    }
    private fun initAppDependencies() {
        api = ApiService.api
        picasso = ApiService.picasso
        gson = ApiService.gson


    }

    @Inject lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration(): Configuration {
     return Configuration.Builder()
         .setMinimumLoggingLevel(android.util.Log.INFO)
         .setWorkerFactory(workerFactory)
         .build()
    }


}

enum class BaseEvent { LOADING, SUCCESS, FAILURE, COMPLETE, NO_DATA, GENERIC }

open class BaseViewModel : ViewModel() {

    protected val _baseEvent = Event<BaseEvent>()
    val baseEvent = _baseEvent

    protected val _msg = Event<String>()
    val msg = _msg

    protected val _err = Event<Int>()
    val err = _err


//    fun isCustomer(): Boolean? {
//        Repository.User.role?.let {
//            return it == UserRole.CUSTOMER
//        }
//        return null
//    }


}

