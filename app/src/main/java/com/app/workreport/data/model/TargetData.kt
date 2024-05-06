package com.app.workreport.data.model

data class TargetData(
    var name: String="",
    var isBefore: Boolean = true,
    var isAfter: Boolean=false,
    var isAtWork: Boolean =false
)
