package com.app.workreport.data.model

import androidx.room.*
import java.io.Serializable

@Entity
data class WorkPlanData(
    @PrimaryKey(autoGenerate = false)
    var workPlanId :String,
    var comment :String
)

@Entity(tableName = "work_plan_report")
data class WorkPlanReportData(
    @PrimaryKey(autoGenerate = false)
    var targetId:String,
    var targetName:String,
    var workPlanId:String,
    var isAfter:Boolean?=false,
    var isBefore:Boolean? = false,
    var isAtWork:Boolean?=false,
    var selectedImageBefore:String ="",
    var selectedImageAfter:String ="",
    var selectedImageAtWork:String ="",
    var isMobile:Boolean?=false
):Serializable

@Entity(tableName = "comment_target")
data class CommentWorkPlanTarget(
    @PrimaryKey(autoGenerate = false)
    var targetId:String,
    var workPlanId:String,
    var targetName: String,
    var commentAfter:String="",
    var commentBefore:String="",
    var commentAtWork:String=""
)

/*data class CompleteJobReport(
    @Embedded val workPlanReportData: WorkPlanReportData,
    @Relation(
        parentColumn = "targetId",
        entityColumn = "targetId"
    )
    val imageData: List<ImageData>

)*/




data class WorkPlanDataAndWorkPlanReportData(
    @Embedded val workPlanData: WorkPlanData,
    @Relation(
       parentColumn = "workPlanId",
       entityColumn = "workPlanId"
    )
    val workPlanReportData: List<WorkPlanReportData>
)


