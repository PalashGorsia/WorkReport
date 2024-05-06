package com.app.workreport.model

import java.io.Serializable

data class TaskList(
    val data: List<DataTask?>?,
    val total: Int
) : Serializable

data class WorkPlanList(
    val data: DataTask
) : Serializable

data class DataTask(
    val _id: String,
    val comment: String,
    val status: Int,
    val workerStatus: Int?,
    val assign: Int?,
    val createdAt: String,
    val checklistId: String?,
    val date: String?,
    val time: String,
    val assignedTo: AssignedTo?,
    val assignedTeamTo: AssignedTeamTo?,
    val job: Job,
    val customer: Customer,
    val facility: Facility,
    val shooting: List<ShootingGetData>,
    val report: Report
) : Serializable

//: Parcelable
data class ShootingGetData(
    var targetName: String,
    var shootingTimeBefore: ShootingTimeGetData?,
    var shootingTimeAfter: ShootingTimeGetData?,
    var shootingTimeAtWork: ShootingTimeGetData?,
    var _id: String
) : Serializable


data class ShootingTimeGetData(
    val visible: Boolean,
    val images: List<ImageDataGet>?,
    val comment: String?
) : Serializable

data class ImageDataGet(
    var url: String,
    var selected: Boolean,
    var _id: String
) : Serializable


/*data class DataTask (
    val _id : String,
    val shooting : List<ShootingPlan>?,
    val assignedTo : AssignedTo,
    val status : Int,
    val date : String?,
    val time : String,
    val job : Job,
    val customer : Customer,
    val facility : Facility,
    val report : Report
)*/

data class AssignedTo(
    val _id: String? = "",
    val name: String? = ""
) : Serializable

data class AssignedTeamTo(
    val _id: String? = "",
    val name: String? = "",
    val leadId: String? = "",
    val members: ArrayList<String>? = ArrayList(),
) : Serializable

data class Job(
    val _id: String,
    val jobNumber: String,
    val name: String,
    val hiragana: String,
    val customerId: String,
    val facilityId: String,
    val address: String,
    val contant: String,
    val frequency: String,
    val type: String,
    val orderAmount: String,
    val contractPeriod: String,
    val workDayDefault: String,
    val contactNumber: String,
    val remarks: String,
    val file: String,
    val nts: String,
    val progressStatus: Int
) : Serializable

data class Facility(
    val _id: String?,
    val facilityNumber: String?,
    val name: String?
) : Serializable

data class Customer(
    val _id: String?,
    val name: String?,
    val email: String?,
    val contactNumber: String?
) : Serializable

data class Report(
    val _id: String?,
    val shooting: List<ShootingType>,
    val comment: String
) : Serializable

data class ShootingType(

    val targetName: String,
    val shootingTimeBefore: ShootingTime,
    val shootingTimeAfter: ShootingTime,
    val _id: String
) : Serializable