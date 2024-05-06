package com.app.workreport.response

data class GetJobDetailsResponse(
    val `data`: GetJobDetailsResponseData,
    val message: String,
    val status: Int
)


data class GetJobDetailsResponseData(
    val __v: Int,
    val _id: String,
    val answerData: String,
    val createdAt: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val jobId: String,
    val status: Int,
    val updatedAt: String
)