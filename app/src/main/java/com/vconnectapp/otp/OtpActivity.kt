package com.vconnectapp.otp

import android.content.Intent
import android.os.Bundle
import android.provider.SimPhonebookContract.SimRecords.PHONE_NUMBER
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vconnectapp.MainActivity
import com.vconnectapp.R
import com.vconnectapp.databinding.ActivityOtpBinding
import com.vconnectapp.login.LoginActivity.Companion.PHONE_NUMBER_KEY
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var otpViewModel: OtpViewModel
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        otpViewModel = ViewModelProvider(this).get(OtpViewModel::class.java)
        auth = Firebase.auth
        initOtpFields()
        initObserver()
        initArguments()
        setVerifyButtonOnClickListener()

        callbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    verifyCode(credential.smsCode)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d("TAG", "onCodeSent:$verificationId")

                    // Save verification ID and resending token so we can use them later
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }

        sendOtpRequestForPhoneNumber()
    }

    private fun sendOtpRequestForPhoneNumber() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(COUNTRY_CODE + phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun initArguments() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER_KEY).toString()
    }

    private fun verifyCode(code: String?) {
        code?.let {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    val user = task.result?.user
                    navigateToMainActivity()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


    private fun initObserver() {
        otpViewModel.apply {
            isOtpFieldValidationError.observe(this@OtpActivity) {
                if (it) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.otp_empty_field_msg),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    verifyCode(getOtpTypedByUser())
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
                    this.otpFieldFour.text.toString(),
                    this.otpFieldFive.text.toString(),
                    this.otpFieldSix.text.toString(),
                )
            }
        }
    }

    private fun getOtpTypedByUser(): String {
        var otpTypedByUser: String

        with(binding) {
            otpTypedByUser =
                otpFieldOne.text.toString() + otpFieldTwo.text.toString() + otpFieldThree.text.toString() +
                        otpFieldFour.text.toString() + otpFieldFive.text.toString() + otpFieldSix.text.toString()
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
                effectiveFocus(otpFieldFour, otpFieldFive, otpFieldThree)
            }
            otpFieldFive.doAfterTextChanged {
                effectiveFocus(otpFieldFive, otpFieldSix, otpFieldFour)
            }
            otpFieldSix.doAfterTextChanged {
                effectiveFocus(otpFieldSix, otpFieldSix, otpFieldFive)
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

    companion object {
        private const val COUNTRY_CODE = "+91"
    }
}