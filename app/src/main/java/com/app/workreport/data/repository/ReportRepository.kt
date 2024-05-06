package com.app.workreport.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.workreport.data.dao.ReportDao
import com.app.workreport.data.model.*
import com.app.workreport.model.DataTask
import com.app.workreport.model.LeaveData
import com.app.workreport.network.ApiService
import com.app.workreport.ui.dashboard.ui.paging.LeaveListPagingSource
import com.app.workreport.ui.dashboard.ui.paging.TaskListPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportRepository @Inject constructor(private val reportDao: ReportDao) {
    suspend fun insertCheckList(checkListTable: CheckListTable) = withContext(Dispatchers.IO) {
        reportDao.insertCheckList(checkListTable)
    }

    suspend fun insertAnswerData(checkListTable: QuestionData) = withContext(Dispatchers.IO) {
        reportDao.insertAnswerData(checkListTable)
    }

    suspend fun deleteChecklist(jobId: String) = withContext(Dispatchers.IO) {
        reportDao.deleteCheckList(jobId)
    }

    suspend fun deleteAnswerData(jobId: String) = withContext(Dispatchers.IO) {
        reportDao.deleteAnswerData(jobId)
    }

    suspend fun updateCheckList(answerData: String, jobId: String) = withContext(Dispatchers.IO) {
        reportDao.update(answerData, jobId)
    }

    suspend fun getCheckItemBasedOnJob(job: String): CheckListTable = withContext(Dispatchers.IO) {
        reportDao.getCheckItemBasedOnJob(job)
    }

    suspend fun insertTaskList(workPlanData: AllTasksData) = withContext(Dispatchers.IO) {
        reportDao.insertTaskList(workPlanData)
    }

    suspend fun deleteAllTask() = withContext(Dispatchers.IO) {
        reportDao.deleteAllTask()
    }

    fun getAllTask(): Flow<List<AllTasksData>> = reportDao.getAllTask()


    fun getImageByWorkoutId(workPlanId: String): Flow<List<ImageData>> =
        reportDao.getImageByWorkoutId(workPlanId)

    suspend fun getAnswerData(jobId: String): List<QuestionData> =
        withContext(Dispatchers.IO) {
            reportDao.getAnswerData(jobId)
        }


    fun getAllTaskList(
        map: Map<String, String>,
        apiService: ApiService,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): Flow<PagingData<DataTask>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { TaskListPagingSource(map, apiService) }
        ).flow
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 1, enablePlaceholders = false)
    }


    /*related shooting data offline*/

    suspend fun insertShootingData(saveShootingParts: SaveShootingParts) =
        withContext(Dispatchers.IO) {
            reportDao.insertShootingData(saveShootingParts)
        }


    fun getApplyLeaveList(
        pagNo: Int,
        apiService: ApiService,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): Flow<PagingData<LeaveData>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { LeaveListPagingSource(pagNo, apiService) }
        ).flow
    }
}