package com.example.taskmanagement.data

import java.io.Serializable
import java.util.Date

class Project (
    var ProjectID: String? = "",
    var name: String? = "",
    var type: String? = "",

    var team: MutableList<User>? = null,
    var tasks: MutableList<Task>? = null,
    var responsable: User? = null,

    var DatedebPrev: Date? = null,
    var DateFinPrev: Date?=null,
    var DateDebEff: Date?=null,
    var DateFinEff: Date? = null,
    var Budget: String?="",
    var BudgetReel: String ?= "",
    var status: String ?= ""
) : Serializable {

    fun addUserToTeam(user: User) {
        if (team == null) {
            team = mutableListOf()
        }
        team?.add(user)
    }

    // Define a function to add a task to the tasks list
    fun addTask(task: Task) {
        if (tasks == null) {
            tasks = mutableListOf()
        }
        tasks?.add(task)
    }
}

