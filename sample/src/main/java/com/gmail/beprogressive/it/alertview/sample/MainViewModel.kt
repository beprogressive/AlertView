package com.gmail.beprogressive.it.alertview.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val buttonMessage = MutableLiveData("Show Alert")
    val buttonLongMessage = MutableLiveData("Show Long Alert")
    val alertMessage = MutableLiveData("")
    val expandable = MutableLiveData(true)
    val enableButton = MutableLiveData(true)

    fun setAlertMessage(message: String?) {
        alertMessage.value = message
    }

    fun clearAlertMessage() {
        alertMessage.value = null
    }
}