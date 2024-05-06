package com.app.workreport.model

import java.io.Serializable

data class RequestPostReport(
    var locale: String,
    var employeeId: String,
    var jobId: String,
    var comment: String="",
    var answerData: String,
    //var shooting: ArrayList<Shooting>
):Serializable


/*{
  "locale": "EN",
  "jobId": "string",
  "employeeId": "string",
  "comment": "string",
  "answerData": "string"
}*/
//123

data class Shooting(
    var targetName: String,
    var shootingTimeBefore: ShootingTime,
    var shootingTimeAfter: ShootingTime,
    var shootingTimeAtWork:ShootingTime

):Serializable
data class ShootingTime(
    var visible:Boolean,
    var images: List<Images>,
    var updatedAt: String,
    var comment:String
):Serializable
data class Images(
    var url: String,
    var selected: Boolean
):Serializable