package com.vconnectapp.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtpViewModel : ViewModel() {
    private val _mutableIsOtpFieldValidationError = MutableLiveData<Boolean>()
    val isOtpFieldValidationError: LiveData<Boolean> = _mutableIsOtpFieldValidationError

    fun validateOtpFields(
        field1: String,
        field2: String,
        field3: String,
        field4: String,
        field5: String,
        field6: String
    ) {
        var anyFieldInvalid = false

        if (field1.isEmpty() || field2.isEmpty() || field3.isEmpty() || field4.isEmpty() || field5.isEmpty() || field6.isEmpty()) {
            anyFieldInvalid = true
        }
        _mutableIsOtpFieldValidationError.value = anyFieldInvalid
    }
}