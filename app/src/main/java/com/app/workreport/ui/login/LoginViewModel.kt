package com.app.workreport.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.workreport.container.BaseEvent
import com.app.workreport.container.BaseViewModel
import com.app.workreport.data.repository.ApiRepository
import com.app.workreport.model.UserLoginData
import com.app.workreport.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(private val apiRepository: ApiRepository)
    : BaseViewModel() {
    val userData = MutableLiveData<UserLoginData>()
    var userNo = MutableLiveData("")
    val isNoValid = MutableLiveData(false)
    val logOutStatus =MutableLiveData<Boolean>()
    fun login(){
        if (isValid()){
            _baseEvent.postValue(BaseEvent.LOADING)
            val map = mutableMapOf<String, String>()
            map[CONTACT_NUMBER] = userNo.value.toString() //email.value ?: ""
            map[LOCALE] = "EN"
            viewModelScope.launch {
                apiRepository.loginUser(map)
                    .catch {e->
                        _baseEvent.postValue(BaseEvent.FAILURE)
                        _err.postValue(ERROR_CODE)
                        Log.d("main", "getPost: ${e.message}")
                    }.collectLatest {response->
                        _baseEvent.postValue(BaseEvent.SUCCESS)
                        if (response.isSuccessful){
                            userData.postValue(response.body()?.data)
                        }else{
                            val error = response.errorBody()?.string()
                            _msg.postValue(error?.xtnHandleError())
                        }
                    }
            }

        }
    }
    private fun isValid():Boolean{
        return if (isNoValid.value==true) true
        else{
            if (userNo.value?.trim()?.isEmpty() == true)
                msg.postValue(NO_NO)
            else
                msg.postValue(VALID_NO)
            false
        }
    }
    fun onTextChanged(s: CharSequence) {
        if (s.length >=10)
            isNoValid.postValue(true)
        else
            isNoValid.postValue(false)
    }
    fun logout(){
        _baseEvent.postValue(BaseEvent.LOADING)
        viewModelScope.launch {
            apiRepository.logoutUser()
                .catch {e->
                    _baseEvent.postValue(BaseEvent.FAILURE)
                    _err.postValue(ERROR_CODE)
                    Log.d("main", "getPost: ${e.message}")
                }.collectLatest {response->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (response.isSuccessful){
                        xtnLog(response.message(), TAGE)
                        logOutStatus.postValue(true)
                      //  userData.postValue(response.body()?.data)
                    }else{
                        val error = response.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }

        }
    }





}