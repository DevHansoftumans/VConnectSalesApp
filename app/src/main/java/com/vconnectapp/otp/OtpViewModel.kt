package com.vconnectapp.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtpViewModel : ViewModel() {
    private val _mutableIsOtpFieldValidationError = MutableLiveData<Boolean>()
    val isOtpFieldValidationError: LiveData<Boolean> = _mutableIsOtpFieldValidationError

    private val _mutableIsSuccessfulOtpLogin = MutableLiveData<Boolean>()
    val isSuccessfulOtpLogin: LiveData<Boolean> = _mutableIsSuccessfulOtpLogin

    fun verifyOtpWithServerSideCheck(otpTypedByUser: String) {
        _mutableIsSuccessfulOtpLogin.value = otpTypedByUser == "1234"
    }

    fun validateOtpFields(field1: String, field2: String, field3: String, field4: String) {
        var anyFieldInvalid = false

        if (field1.isEmpty() || field2.isEmpty() || field3.isEmpty() || field4.isEmpty()) {
            anyFieldInvalid = true
        }
        _mutableIsOtpFieldValidationError.value = anyFieldInvalid
    }
}