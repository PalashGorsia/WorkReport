package com.app.workreport.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/** prm for Api */
const val CONTACT_NUMBER ="contactNumber"
const val OTP ="otp"
const val DEVICE_TYPE ="deviceType"
const val DEVICE_TOKEN ="deviceToken"
const val LOCALE ="locale"
const val WORKER_ID ="workerId"
const val LOCALE_DB_ID ="localId"
const val PAGE ="page"
const val COUNT="count"
const val VIEW_TYPE="viewType"
const val LIST="list"
const val CALENDAR="list"
const val STATUS_PROGRESS="progressStatus"
const val SEARCH="search"
const val WORK_PLAN_ID ="workPlanId"
const val REPORT_ID ="reportId"
const val IMAGE_PRM ="file"
const val EMPLOYEE_ID ="employeeId"
const val JOB_ID ="jobId"
const val ID = "id"
const val LEAVE_ID ="LeaveId"
const val SORT_BY = "sortBy"
const val SORT = "sort"
const val START_DATE ="startDate"
const val END_DATE = "endDate"

const val TARGET_DATA ="target_data_list"
const val NETWORK_STATUS = "network_status"

/*const value*/
const val ANDROID ="Android"
const val BEFORE ="Before"
const val AFTER ="After"
const val AT_WORK ="At Work"
const val EXPIRE_TOKEN ="Unauthorized"
const val TAGE = "testApplication"
const val PAGE_COUNT =10
const val TEXT_UPDATE_TIME =500L
const val FILE_PATH ="file_path"
const val LANGUAGE_KEY_ENGLISH = "en"
const val LANGUAGE_KEY_JAPANESE = "ja"
const val LANGUAGE_KEY_WEB_ENGLISH = "EN"
const val LANGUAGE_KEY_WEB_JAPANESE = "JP"
const val STATUS_ONLINE ="online"
// const val STATUS_OFFLINE ="offline"
const val ASC = "ASC"

//Japanese

/*0 In-progress, 1 Work Plan, 2 submitted Site Report, 3 Manage Report, 4 Completed*/
const val COMPLETE_STATUS =4
const val IN_PROGRESS_STATUS =0
const val WORK_PLAN_STATUS =1
const val SUBMITTED_REPORT_STATUS = 2
const val MANAGE_REPORT_STATUS =3
const val ERROR_CODE =400

const val ERROR_MESSAGE_TYPE =4
const val NO_NO ="0"
const val VALID_NO ="1"


/*date format */
const val DDMMYYY ="dd MMM yyyy"
const val MMM_YYY ="MMM yyyy"
const val YYYY_MM_DD_T_HH_MM_SS_24 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val EE = "EE"
const val EEEE = "EEEE"
const val DD = "dd"
const val MMM = "MMM"
const val MM = "MM"
const val YYYY_MM_DD = "yyyy-MM-dd"
const val YYYY_MM = "yyyy-MM"
const val UTC = "UTC"
//package com.hygeineapp.utils
const val BUNDLE = "bundle"
const val POSITION = "position"
const val SUN_EN ="Sun"
const val STATUS ="status"
const val TITLE = "title"
const val IS_COMPLETED = "isCompleted"
const val IMAGE = "image"
const val TYPE = "type"
const val DATE = "date"
const val URL = "url"
const val CHECKLIST_ID = "checklist_id"
const val IS_UPLOAD_IMAGE = "isUploadImage"
const val DATA = "data"
const val ADDITIONAL_COMMENT = "additional_comment"
const val RECIPIENT_COMMENT = "recipient_comment"
const val MESSAGE = "message"
const val SHOW_CANCEL = "show_cancel"
const val BUTTON_TEXT = "button_text"
const val PERMISSION_REQUEST_CODE_CAMERA =101
const val LEAVE = "leave"
const val HOLIDAY = "holiday"
const val WEEKEND = "weekend"

var REQUEST_TYPE = 0

val JOB_TYPE = "job"



////"type" : "leave",type: "weekend" ,type: "holiday"
internal const val NO_INDEX = -1
internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}