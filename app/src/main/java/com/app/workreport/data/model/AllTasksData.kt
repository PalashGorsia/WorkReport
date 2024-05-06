package com.app.workreport.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allTaskList")
data class AllTasksData(
    @PrimaryKey(autoGenerate = false)
    var taskId:String,
    var date:String,
    var customerName:String,
    var facilityName:String,
    var address:String,
    var jobName:String,
    var jobID:String,
    var employeeId:String,
    var progressStatus:Int

)
@Entity(tableName = "shooting_data")
data class SaveShootingParts(
    var workOutId:String,
    @PrimaryKey(autoGenerate = false)
    var targetId:String,
    var targetName: String,
    var isBefore:Boolean,
    var isAfter:Boolean,
    var isAtWork:Boolean

)

