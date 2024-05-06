package com.app.workreport.model

data class HolidaysData (
    val data : List<HolidaysList>,
    val total : Int
)

data class HolidaysList (
    val _id : String?,
    val date : String?,
    val workerId : String?,
    val type : String?,
    val holidayId : HolidayId?
   // val holidayId :String,
    /*val isDeleted : Boolean,
    val isApproved : Boolean,
    val isCancelled : Boolean,*/

):java.io.Serializable

data class HolidayId(
val _id:String?,
var name:String?

):java.io.Serializable

data class HolidayData(
    val holidayDate : String?,
    val name : String?,
    val id : String?
):java.io.Serializable
