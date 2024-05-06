package com.app.workreport.data.model
data class ReportData(
    var targetName: String = "", var comment: String = "",
    var targetId: String = "", var imageBefore: String = "", var imageAfter: String = "",var imageAtWork:String="",
    var commentBefore:String="", var commentAfter:String ="",var commentAtWork:String="",
    var isAfter:Boolean=false,
    var isBefore:Boolean = false,
    var isAtWork:Boolean=false
)

data class CommentData(val comment: String,val showAddMore:Boolean)
