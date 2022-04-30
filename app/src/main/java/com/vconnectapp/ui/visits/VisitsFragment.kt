package com.vconnectapp.ui.visits

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vconnectapp.databinding.FragmentVisitsBinding
import com.vconnectapp.utils.CheckpointsUtility.CHECKPOINT_1
import com.vconnectapp.utils.CheckpointsUtility.CHECKPOINT_2
import com.vconnectapp.utils.CheckpointsUtility.CHECKPOINT_3

class VisitsFragment : Fragment() {
    private var _binding: FragmentVisitsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val visitsViewModel =
            ViewModelProvider(this).get(VisitsViewModel::class.java)

        _binding = FragmentVisitsBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        val textView: TextView = binding.textSlideshow
//        visitsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var stateCounter: Int = CHECKPOINT_1

        with(binding) {
            nextBtn.setOnClickListener {
                ++stateCounter
                refreshScreen(stateCounter)
            }

            backBtn.setOnClickListener {
                --stateCounter
                refreshScreen(stateCounter)
            }

            checkpoint1.ledCheckbox.setOnCheckedChangeListener{
                    _, isChecked ->
                if(isChecked){
                    checkpoint1.ledQuantity.visibility = VISIBLE
                }else{
                    checkpoint1.ledQuantity.visibility = GONE
                }
            }

            checkpoint1.cctvCheckbox.setOnCheckedChangeListener{
                    _, isChecked ->
                if(isChecked){
                    checkpoint1.cctvQuantity.visibility = VISIBLE
                }else{
                    checkpoint1.cctvQuantity.visibility = GONE
                }
            }

            checkpoint1.projCheckbox.setOnCheckedChangeListener{
                    _, isChecked ->
                if(isChecked){
                    checkpoint1.projQuantity.visibility = VISIBLE
                }else{
                    checkpoint1.projQuantity.visibility = GONE
                }
            }
        }
    }


    private fun refreshScreen(stateCounter: Int) {
        when (stateCounter) {
            CHECKPOINT_1 -> {
                with(binding) {
                    checkpoint1.root.visibility = VISIBLE
                    checkpoint2.root.visibility = GONE
                    checkpoint3.root.visibility = GONE
                    tvDetails.setTextColor(Color.BLACK)
                    tvResponse.setTextColor(Color.GRAY)
                    tvLocation.setTextColor(Color.GRAY)
                    backBtn.isEnabled = false
                    nextBtn.isEnabled = true
                }
            }
            CHECKPOINT_2 -> {
                with(binding) {
                    checkpoint1.root.visibility = GONE
                    checkpoint2.root.visibility = VISIBLE
                    checkpoint3.root.visibility = GONE
                    tvDetails.setTextColor(Color.GRAY)
                    tvResponse.setTextColor(Color.BLACK)
                    tvLocation.setTextColor(Color.GRAY)
                    backBtn.isEnabled = true
                    nextBtn.isEnabled = true
                }
            }
            CHECKPOINT_3 -> {
                with(binding) {
                    checkpoint1.root.visibility = GONE
                    checkpoint2.root.visibility = GONE
                    checkpoint3.root.visibility = VISIBLE
                    tvDetails.setTextColor(Color.GRAY)
                    tvResponse.setTextColor(Color.GRAY)
                    tvLocation.setTextColor(Color.BLACK)
                    backBtn.isEnabled = true
                    nextBtn.isEnabled = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}