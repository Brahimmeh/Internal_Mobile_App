package com.example.taskmanagement

import com.example.taskmanagement.data.User

data class UserWithSelection(
    val user: User,
    var selected: Boolean = false
)