package com.app.workreport.util

import com.app.workreport.model.HolidayData
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


fun Date.xtnFormat(format: String = YYYY_MM_DD_T_HH_MM_SS_24): String {
    val temp = SimpleDateFormat(format, Locale.getDefault())
    //temp.timeZone = TimeZone.getDefault()
    return temp.format(this)
}

fun convertDateFormat(
    date: String?,
    currentFormat: String?,
    toBeChangedformat: String?
): String {
    if (date == null) return ""
    val finalDate: String
    val curFormater =
        SimpleDateFormat(currentFormat!!, Locale.getDefault())
    var dateObj: Date? = null
    try {
        dateObj = curFormater.parse(date)
    } catch (e: ParseException) {
        // Logger.exceptionLog(e)
    }
    val postFormater = SimpleDateFormat(toBeChangedformat!!, Locale.getDefault())
    finalDate = if (dateObj != null) postFormater.format(dateObj) else ""
    return finalDate
}

fun convertUTCToLocal(date: String):String{
    val curFormat = SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_24, Locale.getDefault())
    curFormat.timeZone = TimeZone.getTimeZone(UTC)
    val dat = curFormat.parse(date)
    curFormat.timeZone = TimeZone.getDefault()
    return convertDateFormat(dat?.let { curFormat.format(it) }, YYYY_MM_DD_T_HH_MM_SS_24, DDMMYYY)
}


val currentDateFormat: String
    get() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat(YYYY_MM_DD, Locale.US)
        return df.format(c.time)
    }

val monthFirstDate: String
    get() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = calendar.time
        val df = SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_24, Locale.US)
        return df.format(tomorrow)
    }
val nextThreeMonthLastDate: String
    get() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH,4)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = calendar.time
        val df = SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_24, Locale.US)
        return df.format(tomorrow)
    }

/*fun main(){
    val da = nextThreeMonthLastDate
    println("First Day of month  $da")
}*/

fun getDatesBetween(
    startDate: Date,
    endDate: Date,
    leaveList: ArrayList<String>,
    holidayList: ArrayList<HolidayData>,
    weekEndList: ArrayList<String>
): List<String> {
    val datesInRange: MutableList<String> = ArrayList()
    val calendar: Calendar = getCalendarWithoutTime(startDate)
    val dateF = SimpleDateFormat(YYYY_MM_DD, Locale.US)
    val endCalendar: Calendar = getCalendarWithoutTime(endDate)
    endCalendar.add(Calendar.DATE, 1)

    while (calendar.before(endCalendar)) {
        val selectedDate = dateF.format(calendar.time).toString()
        val holidayDate =  holidayList.find { li-> selectedDate.equals(li.holidayDate,true)}?.holidayDate
        val leaveDate = leaveList.find { selectedDate.equals(it,true) }?:""
        val weekEnd = weekEndList.find { selectedDate.equals(it,true) }?:""

        if (selectedDate!=holidayDate&&selectedDate!=leaveDate&&selectedDate!= weekEnd){
            val dateFormat = SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_24, Locale.US)
            datesInRange.add(dateFormat.format(calendar.time))
        }
        calendar.add(Calendar.DATE, 1)
    }
    return datesInRange
}

fun getCalendarWithoutTime(date: Date): Calendar {
    val calendar: Calendar = GregorianCalendar()
    calendar.time = date
    calendar[Calendar.HOUR] = 0
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar
}