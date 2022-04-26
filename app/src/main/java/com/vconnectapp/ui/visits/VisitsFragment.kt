package com.vconnectapp.ui.slideshow

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vconnectapp.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var stateCounter = 1

        with(binding) {
            nextBtn.setOnClickListener {
                ++stateCounter
                refreshScreen(stateCounter)
            }

            backBtn.setOnClickListener {
                --stateCounter
                refreshScreen(stateCounter)
            }
        }
    }


    private fun refreshScreen(counter: Int) {
        when (counter) {
            1 -> {
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
            2 -> {
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
            3 -> {
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