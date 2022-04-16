package com.vconnectapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vconnectapp.R

class LoginViewModel : ViewModel() {
    private val _mutableValidationErrors = MutableLiveData<Map<String, Int>>()
    val validationErrors: LiveData<Map<String, Int>> = _mutableValidationErrors

    private val _mutableResetAndTriggerCheck = MutableLiveData<Boolean>()
    val resetAndTriggerCheck: LiveData<Boolean> = _mutableResetAndTriggerCheck

    private val _mutableIsSuccessfulLogin = MutableLiveData<Boolean>()
    val isSuccessfulLogin: LiveData<Boolean> = _mutableIsSuccessfulLogin

    companion object ValidationKeys {
        const val PHONE_NUMBER_EMPTY = "phone_number_empty"
        const val PHONE_NUMBER_INVALID = "phone_number_invalid"
        const val CHECK_BOX_UNTICKED = "check_box_unticked"
    }

    fun validateViews(phoneNumber: String, checkboxState: Boolean) {
        val errorsMap = mutableMapOf<String, Int>()
        val phoneNumberRegex = Regex(pattern = "[1-9][0-9]{9}")
        var anyFieldInvalid = false

        if (phoneNumber.trim().isEmpty()) {
            errorsMap[PHONE_NUMBER_EMPTY] = R.string.phone_number_empty_error_msg
            anyFieldInvalid = true
        } else if (!phoneNumber.matches(phoneNumberRegex)) {
            errorsMap[PHONE_NUMBER_INVALID] = R.string.phone_number_invalid_error_msg
            anyFieldInvalid = true

        }

        if (!checkboxState) {
            errorsMap[CHECK_BOX_UNTICKED] = R.string.checkbox_unchecked_error_msg
            anyFieldInvalid = true
        }

        if (anyFieldInvalid) {
            _mutableValidationErrors.value = errorsMap
        } else {
            _mutableResetAndTriggerCheck.value = true
        }
    }

    fun triggerPhoneNumberServerSideCheck(phoneNumber: String) {
        _mutableIsSuccessfulLogin.value = phoneNumber == "7349090397"
    }
}