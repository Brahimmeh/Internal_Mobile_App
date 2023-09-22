package com.example.taskmanagement.data

import java.io.Serializable
import java.util.Date

class Task(
    var taskID: String? = "",
    var name: String? = "",
    var status: String = "",
    var comment: String? = "",

    var user: User? = null,
    var project: String? = null,

    var DatedebPrev: Date? = null,
    var DateFinPrev: Date?=null,
    var DateDebEff: Date?=null,
    var DateFinEff: Date? = null,
    var Budget: String?="",
    var BudgetReel: String ?= "",
    var isdeleted: Boolean? = false
) : Serializable {

}
