package com.app.workreport.ui.otp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.workreport.container.BaseEvent
import com.app.workreport.container.BaseViewModel
import com.app.workreport.data.repository.ApiRepository
import com.app.workreport.model.UserLoginData
import com.app.workreport.model.VerifyUserData
import com.app.workreport.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel
    @Inject constructor(private val apiRepository: ApiRepository)
    : BaseViewModel() {
    val userData = MutableLiveData<VerifyUserData>()
    val userDataResentOtp = MutableLiveData<UserLoginData>()
    var userNo = ""
    var userOtp = ""
    val isVerify = MutableLiveData<Boolean>(false)
    fun verifyOtp() {
        //  if (validate()) {
        val map = mutableMapOf<String, String>()
        map[CONTACT_NUMBER] = userNo
        map[OTP] = userOtp
        map[DEVICE_TYPE] = ANDROID
        map[DEVICE_TOKEN] = AppPref.deviceToken?:""//"a123"
        map[LOCALE] = AppPref.local?:""
        _baseEvent.postValue(BaseEvent.LOADING)
        viewModelScope.launch {
            apiRepository.verifyOtp(map)
                .catch { err->
                    _baseEvent.postValue(BaseEvent.FAILURE)
                    _msg.postValue(err.message)
                    _err.postValue(ERROR_CODE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        res.body()?.let {
                            userData.postValue(it.data)
                            isVerify.postValue(true)
                          //  AppPref.local = it.data?.locale
                        }

                    }else{
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

    fun resendOtp() {
        val map = mutableMapOf<String, String>()
        map[CONTACT_NUMBER] = userNo
        map[LOCALE] = AppPref.local?:""
        _baseEvent.postValue(BaseEvent.LOADING)
        viewModelScope.launch {
            apiRepository.resendOtp(map)
                .catch { err->
                    _baseEvent.postValue(BaseEvent.FAILURE)
                    _msg.postValue(err.message)
                    _err.postValue(ERROR_CODE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        res.body()?.let {
                            userDataResentOtp.postValue(it.data)
                        }
                    }else{
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }


        }
    }

}