package com.app.workreport.ui.checklist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.workreport.container.BaseEvent
import com.app.workreport.container.BaseViewModel
import com.app.workreport.data.model.CheckListTable
import com.app.workreport.data.model.QuestionData
import com.app.workreport.data.repository.ApiRepository
import com.app.workreport.data.repository.ReportRepository
import com.app.workreport.model.*
import com.app.workreport.util.AppPref
import com.app.workreport.util.getLocale
import com.app.workreport.util.xtnHandleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class CheckListViewModel
@Inject constructor(
    val reportRepository: ReportRepository,
    private val apiRepository: ApiRepository
) : BaseViewModel() {
    val checkListData = MutableLiveData<CheckListData>()
    val uploadImageData = MutableLiveData<UploadData>()
    val checkListReportData = MutableLiveData<CheckListReportData>()
    val responseAddJob = MutableLiveData<String>()

    private val locale = getLocale()
    fun getJobCheckList() {
        Log.e("TAG", "getJobCheckList: id ${AppPref.jobId}")
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.getJobCheckList(AppPref.jobId ?: "", locale)
                .catch { e ->
                    _msg.postValue(e.message)
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest { res ->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful) {
                        checkListData.postValue(res.body()?.data)
                    } else {
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

    fun getJobCompletedCheckList() {
        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.getJobCompletedCheckList(AppPref.jobId ?: "", locale)
                .catch {
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest { res ->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful) {
                        checkListReportData.postValue(res.body()?.data)
                    } else {
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }
    }

    fun addCheckList(result: String) {

        val postReport = RequestPostReport(
            locale,
            AppPref.employeeId ?: "",
            AppPref.jobId ?: "",
            "string",
            result
        )
        viewModelScope.launch {
            apiRepository.postReport(postReport)
                .catch { err ->
                    _baseEvent.postValue(BaseEvent.FAILURE)
                    _msg.postValue(err.message)
                }
                .collectLatest { res ->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful) {
                        // removeData()
                        reportRepository.deleteChecklist(AppPref.jobId ?: "")
                        responseAddJob.postValue(res.body()?.message)

/*
                        res.body()?.let {
                        }
*/

                    } else {
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }

        }
    }

    fun addCheckListNew(result: Any) {

        // val  postReport = RequestPostReport(result)
        viewModelScope.launch {
            apiRepository.newPostReport(result)
                .catch { err ->
                    _baseEvent.postValue(BaseEvent.FAILURE)
                    _msg.postValue(err.message)
                }
                .collectLatest { res ->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful) {
                        // removeData()
                        reportRepository.deleteChecklist(AppPref.jobId ?: "")
                        reportRepository.deleteAnswerData(AppPref.jobId ?: "")
                        responseAddJob.postValue(res.body()?.message)

/*
                        res.body()?.let {
                        }
*/

                    } else {
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }

        }
    }

    fun uploadImage(imageUri: String) {
        val file = File(imageUri)
        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imageFile: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModelScope.launch {
            _baseEvent.postValue(BaseEvent.LOADING)
            apiRepository.uploadCheckListImage(
                imageFile,
                "location",
                AppPref.checkListId ?: "",
                locale
            )
                .catch { tt ->
                    Log.e("TAG", "uploadImage: ${tt.message}")
                    _baseEvent.postValue(BaseEvent.FAILURE)
                }
                .collectLatest { res ->
                    _baseEvent.postValue(BaseEvent.SUCCESS)
                    if (res.isSuccessful) {
                        uploadImageData.postValue(res.body()?.data)
                    } else {
                        val error = res.errorBody()?.string()
                        _msg.postValue(error?.xtnHandleError())
                    }
                }
        }

    }

    fun insertCheckList(checkListTable: CheckListTable) = viewModelScope.launch {
        reportRepository.insertCheckList(checkListTable)
    }

/*
    fun deleteCheckList(jobId: String) = viewModelScope.launch {
        reportRepository.deleteChecklist(jobId)
    }
*/

    suspend fun getCheckListBasedOnJob(): CheckListTable {
        return reportRepository.getCheckItemBasedOnJob(AppPref.jobId ?: "")
    }

    fun updateCheckList(ansData: String) = viewModelScope.launch {
        reportRepository.updateCheckList(ansData, AppPref.jobId ?: "")
    }

    fun insertAnswerData(ansData: QuestionData) = viewModelScope.launch {
        reportRepository.insertAnswerData(ansData)
    }

    suspend fun getAnswerData(): List<QuestionData> {
        return reportRepository.getAnswerData(AppPref.jobId ?: "")
    }


}