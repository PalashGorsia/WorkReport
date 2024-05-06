package com.app.workreport.response

import com.google.gson.annotations.SerializedName
import com.app.workreport.data.model.DataReportGallery
import com.app.workreport.model.*

/**
 * Wrapper for APi Response
 */
sealed class Result<T> {
    data class SUCCESS<T>(val data: T, val msg: String) : Result<T>()
    data class ERROR<T>(val msg: String, val serverCode: Int = 400) : Result<T>()
}

/**
 * Class of handling Base Reponses of the API's, Also SUPER class of every Api's Response
 */
open class BaseRes {
    @SerializedName("message")
    val message: String = ""
    @SerializedName("status")
    val statusCode: Int = 0
}


/**
 * Other BaseClass for having Api's Reponse
 */
class BaseRes2 : BaseRes() {
    @SerializedName("data")
    val data: String = ""
}

/**
 * Response class for some of the common apis
 * signup, login etc.
 */



class UserRes : BaseRes() {
    @SerializedName("data")
    val data: UserLoginData? = null
}
class UserVerifyRes : BaseRes() {
    @SerializedName("data")
    val data: VerifyUserData? = null
}
class AllTaskListRes : BaseRes() {
    @SerializedName("data")
    val data: TaskList? = null
}
class WorkPlanDetailRes : BaseRes() {
    @SerializedName("data")
    val data: WorkPlanList? = null
}

class ImageRes:BaseRes(){
    @SerializedName("data")
    val data: ImageData? = null
}

class ReportDetailRes:BaseRes(){
    @SerializedName("data")
    val data:DataReportGallery?=null
}

class ResponsesLeave:BaseRes(){
    @SerializedName("data")
    val data:LeaveData?=null
}

class ResponsesLeaveList:BaseRes(){
    @SerializedName("data")
    val data:LeaveResponse?=null
}

class ResponseCheckList:BaseRes(){
    @SerializedName("data")
    val data : CheckListData?=null
}

class ResponseCheckDetailList:BaseRes(){
    @SerializedName("data")
    val data : CheckListReportData?=null
}

class ResponseUploadImage:BaseRes(){
    @SerializedName("data")
    val data : UploadData?=null
}

class ResponseHolidays : BaseRes(){
    @SerializedName("data")
    val data : HolidaysData?=null
}
