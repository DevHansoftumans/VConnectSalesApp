package com.vconnectapp.otp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.vconnectapp.MainActivity
import com.vconnectapp.R
import com.vconnectapp.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var otpViewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        otpViewModel = ViewModelProvider(this).get(OtpViewModel::class.java)
        initOtpFields()
        initObserver()
        setVerifyButtonOnClickListener()
    }

    private fun initObserver() {
        otpViewModel.apply {
            isSuccessfulOtpLogin.observe(this@OtpActivity) {
                if (it) {
                    navigateToMainActivity()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.otp_mismatch_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            isOtpFieldValidationError.observe(this@OtpActivity) {
                if (it) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.otp_empty_field_msg),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    verifyOtpWithServerSideCheck(getOtpTypedByUser())
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@OtpActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setVerifyButtonOnClickListener() {
        with(binding) {
            verifyButton.setOnClickListener {
                otpViewModel.validateOtpFields(
                    this.otpFieldOne.text.toString(),
                    this.otpFieldTwo.text.toString(),
                    this.otpFieldThree.text.toString(),
                    this.otpFieldFour.text.toString()
                )
            }
        }
    }

    private fun getOtpTypedByUser(): String {
        var otpTypedByUser: String

        with(binding) {
            otpTypedByUser =
                otpFieldOne.text.toString() + otpFieldTwo.text.toString() + otpFieldThree.text.toString() + otpFieldFour.text.toString()
        }

        return otpTypedByUser
    }

    private fun initOtpFields() {
        with(binding) {
            otpFieldOne.doAfterTextChanged {
                effectiveFocus(otpFieldOne, otpFieldTwo, otpFieldOne)
            }
            otpFieldTwo.doAfterTextChanged {
                effectiveFocus(otpFieldTwo, otpFieldThree, otpFieldOne)
            }
            otpFieldThree.doAfterTextChanged {
                effectiveFocus(otpFieldThree, otpFieldFour, otpFieldTwo)
            }
            otpFieldFour.doAfterTextChanged {
                effectiveFocus(otpFieldFour, otpFieldFour, otpFieldThree)
            }
        }
    }

    private fun effectiveFocus(
        currentEditText: TextInputEditText,
        nextEditText: TextInputEditText,
        previousEditText: TextInputEditText
    ) {
        if (currentEditText.text.toString().length == 1) {
            nextEditText.requestFocus()
        } else if (currentEditText.text.toString().isEmpty()) {
            previousEditText.requestFocus()
        }
    }
}