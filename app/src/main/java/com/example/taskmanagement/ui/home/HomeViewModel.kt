package com.example.taskmanagement.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "© 2023 Mamda-Mcma. All rights reserved."
    }

    val text: LiveData<String> = _text
}