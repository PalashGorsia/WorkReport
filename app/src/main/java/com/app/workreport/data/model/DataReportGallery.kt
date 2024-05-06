package com.app.workreport.data.model

import com.app.workreport.model.Customer
import com.app.workreport.model.Facility
import com.app.workreport.model.Job
import com.app.workreport.model.ShootingType


data class DataReportGallery(
    val data : GalleryData
)

data class GalleryData (

    val _id : String,
    val shooting : List<ShootingType>,
    val comment : String,
    val job : Job,
    val workplan : Workplan,
    val customer : Customer,
    val facility : Facility,
    val employeeData : EmployeeData
)
data class Workplan (

    val _id : String,
    val date : String,
    val time : String
)

data class EmployeeData (

    val _id : String,
    val name : String,
    val companyName : String
)
