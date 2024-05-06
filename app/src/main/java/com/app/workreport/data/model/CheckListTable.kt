package com.app.workreport.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CheckListTable")
data class CheckListTable(
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "json")
    var json: String,
    @ColumnInfo(name = "isReportSubmitted")
    var isReportSubmitted: Boolean,
    @ColumnInfo(name = "checkListId")
    var checkListId:String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "jobId")
    var jobId:String,
    @ColumnInfo(name = "userId")//employeeId
    var userId:String

   // @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int =  0
)
@Entity(tableName = "QuestionData")
data class QuestionData(
    var jobId: String,
    var reference_example: String,
    var NC: String,
    var NG: String,
    var O: String,
    var No: Int ,
    var Daily:String,
    var comment :String,
    var check_item :String,
    var is_important: Boolean,
    var is_answered: Boolean ,
    var image_url: String,
    @PrimaryKey(autoGenerate = false)
    var itemId : String
)

