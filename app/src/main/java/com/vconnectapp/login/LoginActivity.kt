package com.vconnectapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vconnectapp.R
import com.vconnectapp.databinding.ActivityLoginBinding
import com.vconnectapp.login.LoginViewModel.ValidationKeys.CHECK_BOX_UNTICKED
import com.vconnectapp.login.LoginViewModel.ValidationKeys.PHONE_NUMBER_EMPTY
import com.vconnectapp.login.LoginViewModel.ValidationKeys.PHONE_NUMBER_INVALID
import com.vconnectapp.otp.OtpActivity
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registeredSalesUsers: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        registeredSalesUsers = Firebase.firestore

        initObserver()
        setNextButtonOnClickListener()
    }

    private fun setNextButtonOnClickListener() {
        with(binding) {
            nextButton.setOnClickListener {
                loginViewModel.validateViews(
                    phoneNumberEditText.text.toString(),
                    privacyPolicyCheckbox.isChecked
                )
            }
        }
    }

    private fun initObserver() {
        loginViewModel.apply {
            validationErrors.observe(this@LoginActivity) {
                if (it.keys.isNotEmpty()) {
                    resetViews()
                    setValidationErrorMessages(it)
                }
            }

            resetAndTriggerCheck.observe(this@LoginActivity) {
                if (it) {
                    resetViews()
                    triggerPhoneNumberServerSideCheck(binding.phoneNumberEditText.text.toString())
                }
            }
        }
    }

    private fun triggerPhoneNumberServerSideCheck(phoneNumber: String) {
        var userFound = false
        registeredSalesUsers.collection(REGISTERED_SALES_AGENT_PATH).get()
            .addOnSuccessListener { allSalesAgents ->

                for (document in allSalesAgents) {
                    if (document.data[PHONE_NUMBER_KEY]?.equals(phoneNumber) == true) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.ready_to_login_text),
                            LENGTH_LONG
                        ).show()
                        userFound = true
                        break
                    }
                }
                if (!userFound) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.failed_login_text),
                        LENGTH_LONG
                    ).show()
                } else {
                    navigateToOtpActivity(phoneNumber)
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.unregistered_user_login),
                    LENGTH_LONG
                ).show()
            }
    }

    private fun navigateToOtpActivity(phoneNumber: String) {
        val intent = Intent(this@LoginActivity, OtpActivity::class.java)
        intent.putExtra(PHONE_NUMBER_KEY, phoneNumber)
        startActivity(intent)
        finish()
    }

    private fun resetViews() {
        with(binding)
        {
            phoneNumberEditText.error = null
            checkboxErrorText.visibility = GONE
        }
    }

    private fun setValidationErrorMessages(errorsMap: Map<String, Int>) {
        errorsMap.apply {
            for (key in errorsMap.keys) {
                when (key) {
                    PHONE_NUMBER_EMPTY -> {
                        binding.phoneNumberEditText.error =
                            this[PHONE_NUMBER_EMPTY]?.let { it1 -> getString(it1) }
                    }
                    PHONE_NUMBER_INVALID -> {
                        binding.phoneNumberEditText.error =
                            this[PHONE_NUMBER_INVALID]?.let { it1 -> getString(it1) }
                    }
                    CHECK_BOX_UNTICKED -> {
                        with(binding.checkboxErrorText) {
                            text = this@apply[CHECK_BOX_UNTICKED]?.let { getString(it) }
                            visibility = VISIBLE
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val PHONE_NUMBER_KEY = "phoneNumber"
        const val REGISTERED_SALES_AGENT_PATH = "registeredSalesAgents"
    }
}
