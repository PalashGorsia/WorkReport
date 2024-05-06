package com.app.workreport.model

import com.app.workreport.util.getLocale

data class LeaveRequest(
    var locale: String,
    var workerId: String,
    val leave : List<String>
)
data class InvitationRequest(
    var locale: String= getLocale(),
    var workPlanId: String,
    val workerStatus : Int,
)
