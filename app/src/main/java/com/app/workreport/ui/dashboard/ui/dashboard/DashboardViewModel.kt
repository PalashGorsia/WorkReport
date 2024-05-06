package com.app.workreport.ui.dashboard.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.workreport.container.BaseEvent
import com.app.workreport.container.BaseViewModel
import com.app.workreport.data.model.AllTasksData
import com.app.workreport.data.model.SaveShootingParts
import com.app.workreport.data.repository.ApiRepository
import com.app.workreport.data.repository.ReportRepository
import com.app.workreport.model.DataTask
import com.app.workreport.model.InvitationRequest
import com.app.workreport.model.TaskList
import com.app.workreport.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
    @Inject constructor(val reportRepository: ReportRepository, val apiRepository: ApiRepository)
    : BaseViewModel() {
    val invitationResponse = MutableLiveData<String>()
    val jobResponse = MutableLiveData<TaskList>()

    fun getAllTaskList(query:String="",status:String="") : Flow<PagingData<DataTask>> {
        val map = mutableMapOf<String,String>()
        map[SEARCH]=query
        map[STATUS_PROGRESS] =status
        return reportRepository.getAllTaskList(map,apiRepository.apiService).cachedIn(viewModelScope)
    }

    fun getJobStatus(workPlanId:String){
        val map = mutableMapOf<String,Any>()
        map[PAGE] = 1
        map[COUNT] = 100
        map[LOCALE] = getLocale()
        map[WORK_PLAN_ID] = workPlanId

        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.getAllTaskList(map)
                .catch {e->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                       // val status = res.body()?.data?.data?.get(0)?.workerStatus
                        jobResponse.postValue(res.body()?.data)
                      //  println(status)
/*
                        if (status==1){

                        }else if (status==2){

                        }else if (status==3){

                        }
*/

                    }else{
                        val error = res.errorBody()?.string()
                        invitationResponse.postValue(error?.xtnHandleError())
                    }
                }
        }

    }
//workPlanId
    fun insertWorkPlan(allTasksData: AllTasksData) = viewModelScope.launch {
        reportRepository.insertTaskList(allTasksData)
    }
    fun insertShootingData(saveShootingParts: SaveShootingParts) = viewModelScope.launch {
        reportRepository.insertShootingData(saveShootingParts)
    }

    fun addWorkInvitation(invitationRequest: InvitationRequest){
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.addWorkInvitation(invitationRequest)
                .catch {e->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest {res->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful){
                        getAllTaskList()
                        invitationResponse.postValue(res.body()?.message)
                    }else{
                        val error = res.errorBody()?.string()
                        invitationResponse.postValue(error?.xtnHandleError())
                    }
                }
        }
    }


}