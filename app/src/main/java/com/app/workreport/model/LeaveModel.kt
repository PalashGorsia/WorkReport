package com.app.workreport.model

import com.app.workreport.util.DDMMYYY
import com.app.workreport.util.YYYY_MM_DD_T_HH_MM_SS_24
import com.app.workreport.util.convertDateFormat
import kotlin.reflect.KProperty

data class LeaveData (
    val workerId : String?,
    val date : String ?,
    val isApproved : Boolean,
    val isCancelled : Boolean,
    val isDeleted : Boolean,
    val type : String?,
    val _id : String,
    val createdAt : String,
    val updatedAt : String,
    val __v : Int
)

class DateFormatDDMMYY{
    var date :String? = null

    operator fun setValue(
        thisRef : Any?,
        property : KProperty<*>,
        value: String?
    ){
        if (value != null){
            date = convertDateFormat(value, YYYY_MM_DD_T_HH_MM_SS_24 , DDMMYYY)
        }
    }

    operator fun getValue(thisRef : Any?, property : KProperty<*>):String?{
        return date
    }
}
/**/

data class LeaveResponse(
    val data : List<LeaveData>,
    val total : Int
)

data class DeleteLeave(
  var locale: String,
  var LeaveId: String
)
/*"locale": "EN",
  "LeaveId": "ffdfdsfdsfdsfdsdsfs"*/