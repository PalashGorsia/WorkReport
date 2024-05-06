package com.app.workreport.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.app.workreport.data.model.*
import com.app.workreport.util.AFTER
import com.app.workreport.util.BEFORE
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    /** new flow */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCheckList(checkListData: CheckListTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswerData(checkListData: QuestionData): Long

    @Query("SELECT * FROM QuestionData WHERE jobId =:jobId")
    fun getAnswerData(jobId: String): List<QuestionData>

    @Query("DELETE FROM QuestionData WHERE jobId =:jobId")
    fun deleteAnswerData(jobId: String)

//@Query("UPDATE CheckListTable SET json = :json WHERE jobId =:jobId")

    /*  @Query("UPDATE QuestionData SET ")
      fun updateAnswerData(checkListData: QuestionData)*/
//json: String, jobId: String

    @Query("DELETE FROM CheckListTable WHERE jobId =:jobId")
    fun deleteCheckList(jobId: String)

    /** Old Flow */
    @Query("SELECT * FROM CheckListTable WHERE jobId =:checkListId")
    fun getCheckList(checkListId: String): LiveData<MutableList<CheckListTable>>

/*
    @Query("SELECT * FROM CheckListTable")
    suspend fun getCheckListSize(): MutableList<CheckListTable?>
*/

    @Query("SELECT * FROM CheckListTable where date= :date AND jobId =:jobId")
    fun getCheckItemBasedOnDate(date: String, jobId: String): LiveData<CheckListTable>

    @Query("SELECT * FROM CheckListTable where jobId =:jobId")
    fun getCheckItemBasedOnJob(jobId: String): CheckListTable


    //update
    @Query("UPDATE CheckListTable SET json = :json WHERE jobId =:jobId")
    fun update(json: String, jobId: String)


    /*save data all task dashboard */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(allTasksData: AllTasksData)

    /*get all Task data */
    @Query("SELECT * FROM allTaskList")
    fun getAllTask(): Flow<List<AllTasksData>>


    /*delete all task data*/
    @Query("DELETE FROM allTaskList")
    suspend fun deleteAllTask()

    /* for add workPlan based on workPlan id in database */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkPlan(workPlanData: WorkPlanData)


    /* for add workReport inside workPlan based on workPlan id in database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(workPlanReportData: List<WorkPlanReportData>)


    @Query("DELETE FROM workplandata WHERE workPlanId= :id")
    suspend fun deleteWorkPlanById(id: String)


    @Query("DELETE FROM work_plan_report WHERE workPlanId= :id")
    suspend fun deleteWorkPlanReportByTrId(id: String)

    @Query("UPDATE  workplandata SET comment= :commentText WHERE workPlanId= :workPlanId")
    suspend fun updateComment(commentText: String, workPlanId: String)

    @Transaction
    @Query("SELECT * FROM workplandata WHERE workPlanId = :workPlanId ORDER BY workPlanId ASC")
    fun getReportDataByPlanId(workPlanId: String): Flow<WorkPlanDataAndWorkPlanReportData>

    // @Transaction
    @Query("SELECT * FROM workplandata WHERE workPlanId= :workPlanId")
    suspend fun getWorkPlanData(workPlanId: String): WorkPlanData

    //Update target Name
    @Query("UPDATE  work_plan_report SET targetName= :updatedName WHERE targetName= :oldName")
    suspend fun updateReportData(updatedName: String, oldName: String)

    /*todo comment related operation*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(commentWorkPlanTarget: List<CommentWorkPlanTarget>)

    @Query("SELECT * FROM comment_target WHERE targetId= :targetId")
    suspend fun getTargetComment(targetId: String): CommentWorkPlanTarget

    @Query("UPDATE  comment_target  SET commentBefore= :updatedName WHERE targetId= :trId")
    suspend fun updateCommentBefore(updatedName: String, trId: String)

    @Query("UPDATE  comment_target  SET commentAfter= :updatedName WHERE targetId= :trId")
    suspend fun updateCommentAfter(updatedName: String, trId: String)

    @Query("UPDATE  comment_target  SET commentAtWork= :updatedName WHERE targetId= :trId")
    suspend fun updateCommentAtWork(updatedName: String, trId: String)

    @Query("UPDATE  comment_target  SET targetName= :updatedName WHERE targetId= :trId")
    suspend fun updateTargetName(updatedName: String, trId: String)


    /*todo DELETE data through workPlanId*/

    @Query("DELETE FROM comment_target  WHERE workPlanId= :workPlanId")
    suspend fun deleteCommentById(workPlanId: String)

    @Query("DELETE FROM imageData WHERE planId= :workPlanId")
    suspend fun deleteImageById(workPlanId: String)

    @Query("DELETE FROM work_plan_report WHERE workPlanId= :workPlanId")
    suspend fun deleteTargetById(workPlanId: String)

    /*todo Delete data through targetId */
    @Query("DELETE FROM comment_target  WHERE targetId= :targetId")
    suspend fun deleteCommentByTrId(targetId: String)

    @Query("DELETE FROM imageData WHERE targetId= :targetId")
    suspend fun deleteImageByTrId(targetId: String)

    @Query("DELETE FROM work_plan_report WHERE targetId= :targetId")
    suspend fun deleteTargetByTrId(targetId: String)


    /*start operations related to image table  */

    /*insert image */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(user: ImageData)

    /*get all  image */

    @Query("SELECT * FROM imageData ORDER BY targetId ASC")
    fun getAllImage(): Flow<List<ImageData>>

    /*  get image for workout */

    fun getImagesFun(
        workPlanId: String,
        trId: String,
        shootingType: String
    ): Flow<List<ImageData>> =
        when (shootingType) {
            AFTER -> getImageByWorkoutId(pId = workPlanId)
            BEFORE -> getImageByWorkoutId(pId = workPlanId)
            else ->
                getImageByWorkoutId(pId = workPlanId)
        }


    /*gte selected status through workplan id and target id */
    @Query("SELECT * FROM work_plan_report WHERE workPlanId= :pId AND targetId= :trId")
    fun getShootingStatus(pId: String, trId: String): Flow<WorkPlanReportData>


    @Query("SELECT * FROM work_plan_report WHERE workPlanId= :pId AND targetId= :trId")
    suspend fun getShootingStatus2(pId: String, trId: String): WorkPlanReportData

    @Query("SELECT * FROM imageData WHERE planId= :pId ORDER BY targetId ASC")
    fun getImageByWorkoutId(pId: String): Flow<List<ImageData>>


    @Query("SELECT * FROM imageData WHERE targetId= :tId ORDER BY targetId ASC")
    fun getImageByTargetId(tId: String): Flow<List<ImageData>>


    @Query("SELECT * FROM imageData WHERE planId= :pId AND targetId= :trName AND shootingType= :sType")
    fun getImageData(trName: String, pId: String, sType: String): Flow<List<ImageData>>


    /*working fine
    * used for get selected images accoding to workPlanId*/

    @Query("SELECT * FROM imageData WHERE planId= :pId ORDER BY targetId ASC")
    fun getImageSelected(pId: String): Flow<List<ImageData>>

    /* update status selected image */
    @Query("UPDATE imageData SET isSelected= :status WHERE id= :imageId")
    suspend fun updateImageStatusById(imageId: Int, status: Boolean)

    /* update imageAWSUrl  */
    @Query("UPDATE imageData SET imageUrl= :path WHERE id= :imageId")
    suspend fun updateImageUrlById(imageId: Int, path: String)

    //for testing
//    @Query("SELECT * FROM imageData ")
//    fun getImageData():MutableLiveData<List<ImageData>>
    //for testing
    @Query("DELETE FROM imageData")
    fun deleteAllImage()

    @Query("DELETE FROM imageData WHERE id= :imageId")
    suspend fun deleteImageById(imageId: Int?)

    /*for delete target Name both table*/

    @Query("DELETE FROM work_plan_report WHERE workPlanId= :workPlanId")
    suspend fun deleteTargetReport(workPlanId: String)

    @Query("DELETE FROM imageData WHERE planId = :pId")
    suspend fun deleteTargetReportImage(pId: String)


//    @Query("DELETE FROM user WHERE id= :idImage")
//    fun deleteImageById(idImage:Int)

    @Query("DELETE FROM work_plan_report")
    fun deleteAllReport()


    /*related shooting data offline*/
    /*insert shooting data */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShootingData(saveShootingParts: SaveShootingParts)

    /*get shooting data through workout id */
    @Query("SELECT * FROM shooting_data WHERE workOutId= :workPlanId")
    fun getShootingData(workPlanId: String): Flow<List<SaveShootingParts>>

    @Query("SELECT * FROM imageData WHERE planId= :workPlanId AND targetId= :targetId AND shootingType= :shootingType AND isSelected= :status")
    suspend fun getSelectedImage(
        workPlanId: String,
        targetId: String,
        shootingType: String,
        status: Boolean = true
    ): ImageData


    @Transaction
    @Query("SELECT * FROM work_plan_report WHERE workPlanId= :workPlanId")
    fun getTargetListJob(workPlanId: String): PagingSource<Int, WorkPlanReportData>


    /*todo update selected image*/

    @Query("UPDATE work_plan_report SET selectedImageBefore= :path WHERE targetId= :targetId")
    suspend fun updateSelectedImageBefore(targetId: String, path: String)

    @Query("UPDATE work_plan_report SET selectedImageAfter= :path WHERE targetId= :targetId")
    suspend fun updateSelectedImageAfter(targetId: String, path: String)

    @Query("UPDATE work_plan_report SET selectedImageAtWork= :path WHERE targetId= :targetId")
    suspend fun updateSelectedImageAtWork(targetId: String, path: String)

}
