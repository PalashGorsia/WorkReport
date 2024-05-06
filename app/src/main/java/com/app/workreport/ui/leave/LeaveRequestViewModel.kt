package com.app.workreport.ui.leave

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.workreport.container.BaseEvent
import com.app.workreport.container.BaseViewModel
import com.app.workreport.data.repository.ApiRepository
import com.app.workreport.data.repository.ReportRepository
import com.app.workreport.model.HolidaysList
import com.app.workreport.model.LeaveData
import com.app.workreport.model.LeaveRequest
import com.app.workreport.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveRequestViewModel
@Inject constructor(val reportRepository: ReportRepository, private val apiRepository: ApiRepository)
    : BaseViewModel() {
    val responseLeave = MutableLiveData<Boolean>()
    val responseDeleteLeave = MutableLiveData<Boolean>()
    val responseHolidays = MutableLiveData<List<HolidaysList>>()
    val resMessDeleteLeave = MutableLiveData<String>()
    val resMessAddLeave = MutableLiveData<String>()


    val locale = getLocale()

    fun getApplyLeaveList(pageNo:Int) : Flow<PagingData<LeaveData>> = reportRepository.getApplyLeaveList(pageNo,apiRepository.apiService).cachedIn(viewModelScope)

    fun addWorkerLeave(leaveRequest: LeaveRequest){
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.addWorkerLeave(leaveRequest)
                .catch {e->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        responseLeave.postValue(true)
                        resMessAddLeave.postValue(res.body()?.message)
                        getHolidays()
                    }else{
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

    fun deleteLave(id: String){
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.deleteLeave(locale,id)
                .catch { e->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        getHolidays()
                        responseDeleteLeave.postValue(true)
                        resMessDeleteLeave.postValue(res.body()?.message)
                    }else{
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

    fun getHolidays() {
        val map = mutableMapOf<String,Any>()
        map[LOCALE] = locale
        map[WORKER_ID] = AppPref.userID?:""
        map[COUNT] = 365
        map[PAGE] = 1
        map["viewType"] ="calendar"
        map[SORT_BY] = DATE
        map[SORT] = ASC
        map[START_DATE] = monthFirstDate
        map[END_DATE] = nextThreeMonthLastDate
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.getHolidays(map)
                .catch { e->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        responseHolidays.postValue(res.body()?.data?.data) //as ArrayList<HolidaysList>?)
                    }else{
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

}