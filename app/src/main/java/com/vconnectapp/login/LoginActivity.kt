package com.vconnectapp.login

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.vconnectapp.R
import com.vconnectapp.databinding.ActivityLoginBinding
import com.vconnectapp.login.LoginViewModel.ValidationKeys.CHECK_BOX_UNTICKED
import com.vconnectapp.login.LoginViewModel.ValidationKeys.PHONE_NUMBER_EMPTY
import com.vconnectapp.login.LoginViewModel.ValidationKeys.PHONE_NUMBER_INVALID
import com.vconnectapp.otp.OtpActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

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
                    loginViewModel.triggerPhoneNumberServerSideCheck(binding.phoneNumberEditText.text.toString())
                }
            }

            isSuccessfulLogin.observe(this@LoginActivity) {
                if (it) {
                    navigateToOtpActivity()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.phone_number_not_registered_error_msg),
                        LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navigateToOtpActivity() {
        val intent = Intent(this@LoginActivity, OtpActivity::class.java)
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
}
