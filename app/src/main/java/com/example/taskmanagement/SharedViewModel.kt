package com.example.taskmanagement
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskmanagement.data.User

class SharedViewModel : ViewModel() {
    private val _currentusrLiveData = MutableLiveData<User?>()
    val currentusrLiveData: LiveData<User?> = _currentusrLiveData

    // Function to observe currentusr
    fun observeCurrentUser() : LiveData<User?> {
        return currentusrLiveData
    }

    // Function to update currentusr
    fun setCurrentUser(user: User?) {
        _currentusrLiveData.value = user
    }
}