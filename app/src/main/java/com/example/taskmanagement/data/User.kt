package com.example.taskmanagement.data

import java.io.Serializable

class User (
        var userID: String? = "",
        var name: String? = "",
        var email: String? = "",
        var password: String? = "",
        var img: String? = "",
        var job_title: String? = "",
        var phone: String? = "",
        var isAdmin: Boolean? = false,
        var isLeader: Boolean? = false
    ) : Serializable{


}