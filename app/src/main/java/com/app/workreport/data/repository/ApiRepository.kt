package com.app.workreport.data.repository


import com.app.workreport.model.InvitationRequest
import com.app.workreport.model.LeaveRequest
import com.app.workreport.model.RequestPostReport
import com.app.workreport.network.ApiService
import com.app.workreport.response.*
import com.app.workreport.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor( val apiService: ApiService) {

    fun loginUser(map:Map<String,String>) : Flow<Response<UserRes>> = flow {
        val response= apiService.loginUser(map)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun logoutUser() : Flow<Response<BaseRes>> = flow {
        val response= apiService.logoutUser()
        emit(response)
    }.flowOn(Dispatchers.IO)


     fun verifyOtp(
        map: Map<String, String>,
    ) :Flow<Response<UserVerifyRes>> = flow {
        val response = apiService.verifyOtp(map)
         emit(response)
    }.flowOn(Dispatchers.IO)


     fun resendOtp(
        map: Map<String, String>,
    ) :Flow<Response<UserRes>> = flow {
        val response = apiService.resendOtp(map)
         emit(response)
    }.flowOn(Dispatchers.IO)

    fun uploadImage(localDBID:String,imageUri:String): Flow<Response<ImageRes>> = flow{
        val map = mutableMapOf<String, RequestBody?>()
        map[LOCALE] = AppPref.local?.xtnPartFromString()
        map[LOCALE_DB_ID] = localDBID.xtnPartFromString()
        var shootingImage: MultipartBody.Part? = null
        imageUri.let {
            if (it.isNotEmpty() && it.xtnIsURL().not())
                shootingImage = it.xtnAsRequestBodyFromPath(paramName = IMAGE_PRM)
        }
        val response = apiService.uploadTargetImage(map,shootingImage)
      //  xtnLog(response.toString(),"responceApiImage")
        emit(response)
    }.flowOn(Dispatchers.IO)

    //postReport: RequestPostReport
    fun postReport(postReport: RequestPostReport): Flow<Response<BaseRes>> = flow{
        val response = apiService.addReport(postReport)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun newPostReport(newData: Any): Flow<Response<BaseRes>> = flow{
        val response = apiService.addNewReport(newData)
        emit(response)
    }.flowOn(Dispatchers.IO)



    fun getReportDetails(reportId:String,local:String): Flow<Response<WorkPlanDetailRes>> = flow{
        val response = apiService.getReportDetails(reportId,local)
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getWorkPlanDetail(workPlanId:String,local:String): Flow<Response<WorkPlanDetailRes>> = flow{
        val response = apiService.getWorkPlanDetail(workPlanId,local)
        emit(response)
    }.flowOn(Dispatchers.IO)

    /* new task */

    fun addWorkerLeave(postReport: LeaveRequest): Flow<Response<BaseRes>> = flow{
        val response = apiService.addWorkerLeave(postReport)
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun addWorkInvitation(invitationRequest: InvitationRequest): Flow<Response<BaseRes>> = flow{
        val response = apiService.addWorkInvitation(invitationRequest)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getAllTaskList(map: Map<String, Any>): Flow<Response<AllTaskListRes>> = flow{
        val response = apiService.getAllTaskList(map)
        emit(response)
    }.flowOn(Dispatchers.IO)


  fun getJobCheckList(jobId:String,locale:String): Flow<Response<ResponseCheckList>> = flow{
        val response = apiService.getJobCheckList(jobId,locale)
        emit(response)
    }.flowOn(Dispatchers.IO)



/*  fun getJobCompletedCheckList(jobId:String,locale:String): Flow<Response<ResponseCheckDetailList>> = flow{
        val response = apiService.getJobCompletedCheckList(jobId,locale)
        emit(response)
    }.flowOn(Dispatchers.IO)*/
fun getJobCompletedCheckList(jobId:String,locale:String): Flow<Response<ResponseCheckDetailList>> = flow{
    val response = apiService.getJobCompletedCheckList(jobId,locale)
    emit(response)
}.flowOn(Dispatchers.IO)


    fun uploadCheckListImage(file :  MultipartBody.Part,type:String,checkListId:String,local:String): Flow<Response<ResponseUploadImage>> = flow{
        val response = apiService.uploadCheckListImage(type,checkListId,local,file)
        emit(response)
    }.flowOn(Dispatchers.IO)


  fun deleteLeave(local: String,id: String): Flow<Response<BaseRes>> = flow{
        val response = apiService.deleteLeave(local,id)
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getHolidays(map: Map<String, Any>): Flow<Response<ResponseHolidays>> = flow{
        val response = apiService.getHolidays(map)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getTaskDetail(map: Map<String, Any>): Flow<Response<AllTaskListRes>> = flow{
        val response = apiService.getTaskDetail(map)
        emit(response)
    }.flowOn(Dispatchers.IO)

}