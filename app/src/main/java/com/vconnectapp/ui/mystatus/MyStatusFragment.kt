package com.vconnectapp.ui.mystatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vconnectapp.R
import com.vconnectapp.databinding.FragmentMyStatusBinding

class MyStatusFragment : Fragment() {

    private var _binding: FragmentMyStatusBinding? = null
    private lateinit var myStatusViewModel: MyStatusViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        get() = _binding!!
    private var myStatus: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyStatusBinding.inflate(inflater, container, false)
        myStatusViewModel =
            ViewModelProvider(this).get(MyStatusViewModel::class.java)
        val root: View = binding.root

        initRadioGroupListeners()

        return root
    }

    private fun initRadioGroupListeners() {
        with(binding) {
            workingStatusRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                run {
                    myStatus = when (checkedId) {
                        R.id.working_radio_button -> true

                        R.id.not_working_radio_button -> false

                        else -> null
                    }
                    this.saveStatusButton.isEnabled = true
                }

            }

            saveStatusButton.apply {
                setOnClickListener {
                    if (isEnabled) {
                        myStatusViewModel.saveMyStatus(myStatus)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}