package com.app.workreport.util

import com.app.workreport.container.AppContainer

object AppPref {
    private const val PREF_ACCESS_TOKEN = "access_token"
    private const val PREF_DEVICE_TOKEN = "device_token"
    private const val PREF_IS_LOGIN = "local"
    private const val PREF_IS_LOGGED_IN = "is_logged_in"
    private const val PREF_IS_LOG_OUT = "is_logged_out"
    private const val PREF_CHECK_LIST_ID = "cidfgg"
    private const val PREF_EMPLOYEE_ID = "employeeId"
    private const val PREF_JOB_ID = "JOBid"
    private const val PREF_JUMP_NEXT_PAGE = "jump_to_gjgfl"
    private const val PREF_USER_ID = "uuuuu_iiii_qqqq"
    private const val PREF_LEAD_ID = "uuuuuuu_iiiiiii_qqqqgggg"
    private const val PREF_SINGLE_WORKER = "uuuuuuu_iiiiiii_qqqqgggggggggg"
    private const val PREF_IS_ANSWER_SELECTED = "uuuuuuu_iiiiiii_qqqqgggggggggg_ppppp"

    var accessToken: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_ACCESS_TOKEN, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_ACCESS_TOKEN)
        }

    var userID: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_USER_ID, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_USER_ID)
        }


    var leadID: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_LEAD_ID, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_LEAD_ID)
        }

    var isSingleWorker: Boolean
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_SINGLE_WORKER, "$value")
        }
        get() {
            AppContainer.INSTANCE.xtnGetKey(PREF_SINGLE_WORKER)?.let {
                return if (it.isNotEmpty()) it.toBoolean()
                else false
            } ?: kotlin.run {
                return false
            }
        }

    var deviceToken: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_DEVICE_TOKEN, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_DEVICE_TOKEN)
        }

    var local: String? = getLocale()


    var checkListId: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_CHECK_LIST_ID, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_CHECK_LIST_ID)
        }

    var employeeId: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_EMPLOYEE_ID, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_EMPLOYEE_ID)
        }

    var jobId: String?
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_JOB_ID, value ?: "")
        }
        get() {
            return AppContainer.INSTANCE.xtnGetKey(PREF_JOB_ID)
        }


    var isLoggedIn: Boolean
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_IS_LOGGED_IN, "$value")
        }
        get() {
            AppContainer.INSTANCE.xtnGetKey(PREF_IS_LOGGED_IN)?.let {
                return if (it.isNotEmpty()) it.toBoolean()
                else false
            } ?: kotlin.run {
                return false
            }
        }

    var jumpToNextPage: Boolean
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_JUMP_NEXT_PAGE, "$value")
        }
        get() {
            AppContainer.INSTANCE.xtnGetKey(PREF_JUMP_NEXT_PAGE)?.let {
                return if (it.isNotEmpty()) it.toBoolean()
                else false
            } ?: kotlin.run {
                return false
            }
        }

    var isLogOut: Boolean
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_IS_LOG_OUT, "$value")
        }
        get() {
            AppContainer.INSTANCE.xtnGetKey(PREF_IS_LOG_OUT)?.let {
                return if (it.isNotEmpty()) it.toBoolean()
                else false
            } ?: kotlin.run {
                return false
            }
        }


    var isAnswerSelected: Boolean
        set(value) {
            AppContainer.INSTANCE.xtnPutKey(PREF_IS_ANSWER_SELECTED, "$value")
        }
        get() {
            AppContainer.INSTANCE.xtnGetKey(PREF_IS_ANSWER_SELECTED)?.let {
                return if (it.isNotEmpty()) it.toBoolean()
                else false
            } ?: kotlin.run {
                return false
            }
        }

}