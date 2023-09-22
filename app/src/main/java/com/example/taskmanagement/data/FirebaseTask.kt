package com.example.taskmanagement.data

import java.util.Date

data class FirebaseTask(
    var taskID: String? = "",
    var name: String? = "",
    var status: String = "",
    var comment: String? = "",
    var user: User? = null,
    var project: String? = null,
    var DatedebPrev: Date? = null,
    var DateFinPrev: Date? = null,
    var DateDebEff: Date? = null,
    var DateFinEff: Date? = null,
    var Budget: String? = "",
    var budgetReel: Any? = null, // Store budgetReel as Any to handle both Long and String
    var isdeleted: Boolean? = false
)
