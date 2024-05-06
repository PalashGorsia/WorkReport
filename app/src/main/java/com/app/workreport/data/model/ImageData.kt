package com.app.workreport.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.workreport.util.xtnFormat
import java.util.*

@Entity(tableName = "imageData")
data class ImageData(
    var targetId: String,
    var imagePath: String,
    var isSelected: Boolean,
    var targetName: String,
    var planId: String,
    var shootingType: String,
    var imageUrl: String = "",
    var dateTime: String = Date().xtnFormat()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
