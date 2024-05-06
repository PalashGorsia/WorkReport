package com.app.workreport.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.workreport.data.dao.ReportDao
import com.app.workreport.data.model.*


@Database(
    entities = [WorkPlanData::class,
        ImageData::class,
        WorkPlanReportData::class,
        AllTasksData::class,
        SaveShootingParts::class,
        CommentWorkPlanTarget::class,
        CheckListTable::class,
        QuestionData::class],
    version = 4,
    exportSchema = false
)
abstract class ReportDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao

}