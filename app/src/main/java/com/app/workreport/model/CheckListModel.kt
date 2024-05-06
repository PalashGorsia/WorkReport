package com.app.workreport.model

import android.os.Parcel
import android.os.Parcelable
import com.app.workreport.data.model.QuestionData
import java.io.Serializable


/*CheckListData( checkListId=null, address=Tredegar Hous Country Park Caravan and Motorhome Club Campsite, Coedkernew, contant=, frequency=One Time, type=Temporary, orderAmount=, contractDateTo=null, contractDateFrom=null, workDayDefault=, contactNumber=, nts=null, remarks=, progressStatus=1, status=1, isDeleted=false, createdAt=2023-03-28T05:36:04.120Z, updatedAt=2023-03-28T05:36:04.120Z, __v=0)*/

data class CheckListData(
    val weekDays: List<String>,
    val monthlyDates: List<String>,
    val _id: String,
    val jobNumber: String,
    val name: String,
    val hiragana: String,
    val customerId: String,
    val facilityId: String,
    val checkListId: CheckListId?,
    val address: String?,
    val contant: String?,
    val frequency: String?,
    val type: String?,
    val orderAmount: String?,
    val contractDateTo: String?="",
    val contractDateFrom: String?="",
    val workDayDefault: String?,
    val contactNumber: String?,
    val nts: String?,
    val remarks: String?,
    val progressStatus: Int?,
    val status: Int?,
)


data class CheckListReportData(
    val _id: String,
    val jobId: String,
    val currentDate: String,
    val checkListId: CheckListId,
    val answerData: String
)

data class UploadData(
    val checklistId: String,
    val `file`: String,
    val locale: String,
    val type: String
)

data class CheckListId(
    val _id: String,
    val checklistNumber: String,
    val facilityName: String,
    val facilityType: String,
    val status: Int,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val file: String,
    val fileData: String
)

data class Content(
    val name: String,
    val todos: List<Todo2>
) : Serializable

data class Todo(
    var NC: String,
    var NG: String,//= ""
    var O: String,// = ""
    var No: Int,
    var check_item: String,// = ""
    var comment: String,
    var image_url: String,
    var is_important: Boolean,
    var is_answered: Boolean,
    var reference_example: String,
    var score: Int = 0
) : Serializable


data class Data(
    var additionalInfo: AdditionalInfo,
    val tabs: List<Tab>
) : Serializable

data class Data2(
    var additionalInfo: String,
    val tabs: List<Tab>
) : Serializable

data class AnswerData(
    var additionalInfo: String,
    var answerData: String,
    var comment: String = "",
    var jobId: String,
    var locale: String,
    var employeeId: String,
    var status: Int,

    ) : Serializable


data class AnswerDataWithoutAdditionaInfo(
    var answerData: String,
    var comment: String = "",
    var jobId: String,
    var locale: String,
    var employeeId: String,
    var status: Int,

    ) : Serializable

data class AnswerData2(
    /* var additionalInfo: AdditionalInfo,*/
    var answerData: List<QuestionData>,


    ) : Serializable

data class AdditionalInfo(
    var general_comment: String,
    var receipt_comment: String
) : Serializable


data class Tab(
    var content: List<Content>,
    var title: String,
    var score: String
) : Serializable


data class ChecklistIdOP(
    val _id: String,
    val facilityname: String,
    val facilityTypeId: FacilityTypeId,
    val cklNumber: String
)

data class FacilityTypeId(
    val _id: String,
    val title: String
)

data class SubmitBy(
    val _id: String,
    val designationId: DesignationId,
    val email: String,
    val employeeNumber: String,
    val name: String
)

data class DesignationId(
    val _id: String,
    val inspectionType: String,
    val name: String,
    val userType: String
)

data class PrevData(

    val _id: String,
    val checklistId: String,
    val storeId: String,
    val inspectionType: String,
    val submitBy: String,
    val facility_admin: String,
    val answerData: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
/*data class Content(
    val name: String,
    val todos: List<Todo>
):Serializable*/


data class Todo2(
    var NC: String,
    var NG: String,//= ""
    var O: String,// = ""
    var No: Int,
    var check_item: String,// = ""
    var comment: String,
    var image_url: String,
    var is_important: Boolean,
    var is_answered: Boolean,
    var reference_example: String?="",
    var score: Int = 0,
    var itemId: String,
    var Daily: String?
) : Serializable

class Todos() : Parcelable {

    var NC: String? = ""
    var NG: String? = ""
    var O: String? = ""
    var No: Int = 0
    var check_item: String? = ""
    var comment: String? = ""
    var image_url: String? = ""
    var is_important: Boolean = false
    var is_answered: Boolean = false
    var reference_example: String? = ""
    var score: Int = 0

    constructor(parcel: Parcel) : this() {
        NC = parcel.readString()
        NG = parcel.readString()
        O = parcel.readString()
        No = parcel.readInt()
        check_item = parcel.readString()
        comment = parcel.readString()
        image_url = parcel.readString()
        is_important = parcel.readByte() != 0.toByte()
        is_answered = parcel.readByte() != 0.toByte()
        reference_example = parcel.readString()
        score = parcel.readInt()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(NC)
        parcel.writeString(NG)
        parcel.writeString(O)
        parcel.writeInt(No)
        parcel.writeString(check_item)
        parcel.writeString(comment)
        parcel.writeString(image_url)
        parcel.writeByte(if (is_important) 1 else 0)
        parcel.writeByte(if (is_answered) 1 else 0)
        parcel.writeString(reference_example)
        parcel.writeInt(score)
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Todo) return false
        val artist = o
        if (is_important != artist.is_important) return false
        return if (check_item != null) check_item == artist.check_item else artist.check_item == null
    }

    override fun hashCode(): Int {
        var result = if (check_item != null) check_item.hashCode() else 0
        result = 31 * result + if (is_important) 1 else 0
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getText(): String {

        return check_item!!

    }

    companion object CREATOR : Parcelable.Creator<Todos> {
        override fun createFromParcel(parcel: Parcel): Todos {
            return Todos(parcel)
        }

        override fun newArray(size: Int): Array<Todos?> {
            return arrayOfNulls(size)
        }
    }


}


/*
class InspectionContent(name: String?, todos: List<Todo?>?):
    ExpandableGroup<Todo?>(name, todos)*/
