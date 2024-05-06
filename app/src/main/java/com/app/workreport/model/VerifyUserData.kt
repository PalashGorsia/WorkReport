package com.app.workreport.model

import java.io.Serializable

data class VerifyUserData(
    val loginToken: LoginToken?,
    val _id: String?,
    val employeeNumber: String?,
    val name: String?,
    val email: String?,
    val password: String?,
    val hiragana: String?,
    val birthDay: String?,
    val qualification: String?,
    val age: String?,
    val companyName: String?,
    val contactNumber: String?,
    val locale: String?,
    val status: Int?,
    val isDeleted: Boolean?,
    val notification: Int?,
    val designationId: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val __v: Int?,
    val otp: String?
):Serializable
data class LoginToken (
    val token : String?,
    val deviceType : String?,
    val deviceToken : String?,
    val createdAt : String?
):Serializable